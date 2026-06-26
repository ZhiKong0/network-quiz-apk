from __future__ import annotations

import json
import re
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
QUESTIONS_PATH = ROOT / "app" / "src" / "main" / "assets" / "questions.json"
OUTPUT_DIR = ROOT / "explanation_work" / "agent_outputs"
BACKUP_PATH = ROOT / "explanation_work" / "questions.before_v3_rewrite_merge.json"
REPORT_PATH = ROOT / "explanation_work" / "v3_rewrite_merge_report.md"

AGENT_FILES = [
    OUTPUT_DIR / "v3_chapter1_rewrite.json",
    OUTPUT_DIR / "v3_chapter2_rewrite.json",
    OUTPUT_DIR / "v3_chapter3_rewrite.json",
    OUTPUT_DIR / "v3_chapter4_rewrite.json",
    OUTPUT_DIR / "v3_chapter5_7_rewrite.json",
]

BAD_MARKERS = ["???", "\ufffd"]
DETAIL_HEADERS = ["核心知识点：", "题目变形：", "知识拓展："]


def load_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def clean_text(text: str) -> str:
    return "\n".join(line.rstrip() for line in str(text).replace("\r\n", "\n").replace("\r", "\n").strip().split("\n")).strip()


def build_explanation(quick: str, detail: str) -> str:
    return f"【快速做题】\n{clean_text(quick)}\n\n【知识点详解】\n{clean_text(detail)}"


def contains_bad_marker(text: str) -> bool:
    return any(marker in str(text) for marker in BAD_MARKERS)


def looks_like_table(text: str) -> bool:
    raw = str(text)
    return bool(re.search(r"(?m)^\|.+\|\s*$", raw)) and bool(
        re.search(r"(?m)^\|\s*:?-{3,}:?(?:\s*\|\s*:?-{3,}:?)+\s*\|\s*$", raw)
    )


def option_marks(question: dict) -> list[str]:
    qtype = str(question.get("type") or "")
    options = question.get("options") or []
    if qtype == "tf":
        marks = []
        for opt in options:
            text = str(opt.get("text") or "")
            head = text.split(".", 1)[0].strip()
            if head:
                marks.append(head)
        return marks or ["T", "F"]
    return [str(opt.get("key") or "").strip() for opt in options if str(opt.get("key") or "").strip()]


def has_option_line(text: str, mark: str) -> bool:
    if not mark:
        return False
    pattern = rf"(?m)^\s*-\s*{re.escape(mark)}\s*[：:]"
    return re.search(pattern, str(text)) is not None


def validate_payload(question: dict, quick: str, detail: str, source_name: str) -> list[str]:
    label = question["label"]
    problems: list[str] = []
    if not clean_text(quick):
        problems.append("empty quickExplanation")
    if not clean_text(detail):
        problems.append("empty knowledgeDetail")
    if contains_bad_marker(quick):
        problems.append("quickExplanation contains corrupted marker")
    if contains_bad_marker(detail):
        problems.append("knowledgeDetail contains corrupted marker")
    for header in DETAIL_HEADERS:
        if header not in detail:
            problems.append(f"knowledgeDetail missing {header}")
    if str(question.get("type")) in {"tf", "single", "multi"}:
        missing_marks = [mark for mark in option_marks(question) if not has_option_line(quick, mark)]
        if missing_marks:
            problems.append("quickExplanation missing option lines: " + ", ".join(missing_marks))
    if problems:
        return [f"{label} from {source_name}: {problem}" for problem in problems]
    return []


def main() -> None:
    questions = load_json(QUESTIONS_PATH)
    if not BACKUP_PATH.exists():
        BACKUP_PATH.write_text(
            json.dumps(questions, ensure_ascii=False, indent=2),
            encoding="utf-8",
        )

    merged: dict[str, dict] = {}
    source_counts: dict[str, int] = {}
    missing_files: list[str] = []
    source_errors: list[str] = []

    by_label = {q["label"]: q for q in questions}

    for path in AGENT_FILES:
        if not path.exists():
            missing_files.append(path.name)
            continue
        data = load_json(path)
        source_counts[path.name] = len(data)
        for item in data:
            label = item["label"]
            if label not in by_label:
                source_errors.append(f"{label} from {path.name}: label not found in questions.json")
                continue
            quick = clean_text(item.get("quickExplanation", ""))
            detail = clean_text(item.get("knowledgeDetail", ""))
            source_errors.extend(validate_payload(by_label[label], quick, detail, path.name))
            merged[label] = {
                "quickExplanation": quick,
                "knowledgeDetail": detail,
                "source": path.name,
            }

    if source_errors:
        raise RuntimeError("invalid agent output:\n" + "\n".join(source_errors[:80]))

    updated = 0
    untouched: list[str] = []
    table_count = 0
    write_errors: list[str] = []

    for q in questions:
        payload = merged.get(q["label"])
        if not payload:
            untouched.append(q["label"])
            continue
        q["quickExplanation"] = payload["quickExplanation"]
        q["knowledgeDetail"] = payload["knowledgeDetail"]
        q["explanation"] = build_explanation(q["quickExplanation"], q["knowledgeDetail"])
        write_errors.extend(
            validate_payload(q, q["quickExplanation"], q["knowledgeDetail"], payload["source"])
        )
        if looks_like_table(q["quickExplanation"]) or looks_like_table(q["knowledgeDetail"]):
            table_count += 1
        updated += 1

    if missing_files:
        raise RuntimeError("missing agent outputs: " + ", ".join(missing_files))
    if untouched:
        raise RuntimeError("missing merged payload for labels: " + ", ".join(untouched[:60]))
    if write_errors:
        raise RuntimeError("refusing to write invalid questions:\n" + "\n".join(write_errors[:80]))

    QUESTIONS_PATH.write_text(
        json.dumps(questions, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    lines = [
        "# v3 Rewrite Merge Report",
        "",
        f"- total_questions: {len(questions)}",
        f"- replaced_questions: {updated}",
        f"- untouched_questions: {len(untouched)}",
        f"- missing_outputs: {', '.join(missing_files) if missing_files else 'none'}",
        f"- source_validation_errors: {len(source_errors)}",
        f"- write_validation_errors: {len(write_errors)}",
        f"- questions_with_markdown_tables: {table_count}",
        "",
        "## Agent File Counts",
        "",
    ]
    for name, count in source_counts.items():
        lines.append(f"- {name}: {count}")
    lines.extend(["", "## Untouched Labels", ""])
    lines.append(", ".join(untouched) if untouched else "none")

    REPORT_PATH.write_text("\n".join(lines) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
