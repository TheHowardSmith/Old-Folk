"""Build the region map used by the app and the website.

    python scripts/make_map.py ne_110m_admin_0_countries.geojson ../site/map.json \
        ../android/app/src/main/res/raw/region_map.json

Input: Natural Earth 1:110m admin-0 countries (public domain,
https://www.naturalearthdata.com). Output: a small JSON file with one SVG path
per map region (the keys of map_regions in rules/classify.yaml), a path for
land outside every region, a label position for each region, and the view box.
Projection: plain equirectangular, 80°N to 56°S, which reads like an old
school atlas and keeps the Pacific islands at both edges.

Requires shapely (only for this script; the builder itself doesn't need it).
"""
from __future__ import annotations

import json
import sys

from shapely.geometry import box, shape, mapping
from shapely.ops import unary_union

W = 1000.0
LAT_TOP, LAT_BOTTOM = 80.0, -56.0
H = round(W * (LAT_TOP - LAT_BOTTOM) / 360.0, 1)

REGIONS = {
    "north_america": "USA CAN GRL",
    "latin_america": "MEX GTM BLZ HND SLV NIC CRI PAN COL VEN GUY SUR ECU PER BOL BRA PRY URY ARG CHL FLK",
    "caribbean": "CUB HTI DOM JAM PRI BHS TTO",
    "british_isles": "GBR IRL",
    "western_europe": "FRA DEU AUT CHE BEL NLD LUX",
    "nordic": "NOR SWE FIN DNK ISL",
    "eastern_europe": "POL CZE SVK HUN ROU BGR SRB HRV SVN BIH MNE MKD ALB KOS UKR BLR MDA LTU LVA EST",
    "southern_europe": "PRT ESP ITA GRC CYP CYN",
    "middle_east": "TUR SYR LBN ISR PSX PSE JOR IRQ IRN SAU YEM OMN ARE QAT KWT GEO ARM AZE EGY LBY TUN DZA MAR SAH",
    "central_asia": "KAZ UZB TKM KGZ TJK AFG",
    "south_asia": "IND PAK BGD NPL BTN LKA",
    "east_asia": "CHN MNG PRK KOR JPN TWN",
    "southeast_asia": "MMR THA LAO KHM VNM MYS IDN PHL BRN TLS",
    "pacific": "AUS NZL PNG FJI SLB VUT NCL",
}
# Where each label goes (lon, lat) and, for labels set out in the sea, the point
# of the region its leader line runs to.
LABELS = {
    "north_america": (-100, 47), "latin_america": (-60, -12), "caribbean": (-62, 27),
    "british_isles": (-27, 57), "western_europe": (-24, 47), "nordic": (2, 73),
    "eastern_europe": (42, 62), "southern_europe": (-18, 36), "middle_east": (44, 28),
    "africa": (20, 4), "central_asia": (88, 58), "south_asia": (78, 21),
    "east_asia": (108, 36), "southeast_asia": (112, 4), "pacific": (140, -25),
}
ANCHORS = {
    "caribbean": (-76, 20), "british_isles": (-3, 54), "western_europe": (3, 47),
    "nordic": (14, 63), "southern_europe": (-5, 40),
}
URALS = 60.0      # Russia west of here is Eastern Europe, east of it Siberia


def project(lon: float, lat: float) -> tuple[float, float]:
    return (round((lon + 180) / 360 * W, 1), round((LAT_TOP - lat) / (LAT_TOP - LAT_BOTTOM) * H, 1))


def to_path(geom) -> str:
    polys = [geom] if geom.geom_type == "Polygon" else list(getattr(geom, "geoms", []))
    out = []
    for p in polys:
        if p.is_empty or p.geom_type != "Polygon":
            continue
        for ring in [p.exterior] + list(p.interiors):
            pts = [project(x, y) for x, y in ring.coords]
            if len(pts) < 4:
                continue
            out.append("M" + "L".join(f"{x:g},{y:g}" for x, y in pts[:-1]) + "Z")
    return "".join(out)


def main(src: str, *dsts: str) -> None:
    features = json.load(open(src, encoding="utf-8"))["features"]
    owner = {code: key for key, codes in REGIONS.items() for code in codes.split()}
    parts: dict[str, list] = {k: [] for k in list(REGIONS) + ["africa", "land"]}
    unassigned = []
    west, east = box(-180, -90, URALS, 90), box(URALS, -90, 180, 90)
    for f in features:
        p = f["properties"]
        code = p.get("ADM0_A3") or p.get("ISO_A3")
        geom = shape(f["geometry"])
        if code == "ATA":
            continue
        if code == "RUS":
            parts["eastern_europe"].append(geom.intersection(west))
            parts["central_asia"].append(geom.intersection(east))
            continue
        if code == "USA":   # Hawaii goes with the Pacific
            hawaii = box(-162, 17, -152, 24)
            parts["pacific"].append(geom.intersection(hawaii))
            parts["north_america"].append(geom.difference(hawaii))
            continue
        if code == "FRA":   # French Guiana is in South America
            guiana = box(-60, 0, -50, 8)
            parts["latin_america"].append(geom.intersection(guiana))
            parts["western_europe"].append(geom.difference(guiana))
            continue
        key = owner.get(code) or ("africa" if p.get("CONTINENT") == "Africa" else None)
        if key is None:
            unassigned.append(f"{code} {p.get('ADMIN')}")
            key = "land"
        parts[key].append(geom)
    print("not in a region (drawn as plain land):", ", ".join(sorted(unassigned)), file=sys.stderr)
    crop = box(-180, LAT_BOTTOM, 180, LAT_TOP)
    out = {"w": W, "h": H, "regions": [], "land": ""}
    for key, geoms in parts.items():
        if not geoms:
            continue
        g = unary_union([x.buffer(0) for x in geoms if not x.is_empty]).intersection(crop)
        g = g.simplify(0.35, preserve_topology=True)
        d = to_path(g)
        if key == "land":
            out["land"] = d
            continue
        lx, ly = project(*LABELS[key])
        entry = {"k": key, "d": d, "x": lx, "y": ly}
        if key in ANCHORS:
            entry["ax"], entry["ay"] = project(*ANCHORS[key])
        out["regions"].append(entry)
    for dst in dsts:
        json.dump(out, open(dst, "w", encoding="utf-8"), separators=(",", ":"))
        print(f"wrote {dst}: {len(out['regions'])} regions", file=sys.stderr)


if __name__ == "__main__":
    main(sys.argv[1], *sys.argv[2:])
