from __future__ import annotations

from pathlib import Path

from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app" / "src" / "main" / "res"
SOURCE = RES / "drawable" / "ic_launcher_foreground.png"
SOURCE_ORIGINAL = ROOT / "tools" / "icon_sources" / "ic_launcher_source.png"
BACKGROUND = (87, 62, 44, 255)
SCALE = 0.86

SIZES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}


def fit_icon(source: Image.Image, size: int, scale: float = SCALE) -> Image.Image:
    canvas = Image.new("RGBA", (size, size), BACKGROUND)
    inner = max(1, round(size * scale))
    resized = source.resize((inner, inner), Image.Resampling.LANCZOS)
    offset = ((size - inner) // 2, (size - inner) // 2)
    canvas.alpha_composite(resized, offset)
    return canvas


def main() -> None:
    original_path = SOURCE_ORIGINAL if SOURCE_ORIGINAL.exists() else SOURCE
    original = Image.open(original_path).convert("RGBA")
    foreground = fit_icon(original, 432)
    foreground.save(SOURCE)
    for folder, size in SIZES.items():
        icon = fit_icon(original, size)
        for name in ("ic_launcher.png", "ic_launcher_round.png"):
            icon.save(RES / folder / name)
    print(f"Regenerated launcher icons with scale={SCALE}")


if __name__ == "__main__":
    main()
