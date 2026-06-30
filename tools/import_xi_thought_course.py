import argparse
import json
import re
from collections import Counter, defaultdict
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
ASSETS_DIR = ROOT / "app" / "src" / "main" / "assets"
DEFAULT_SOURCE_DIR = Path("E:/Learning/QQCLI/runtime/incoming-files")
OUTPUT = ASSETS_DIR / "xi_thought_questions.json"
REPORT = ROOT / "explanation_work" / "xi_thought_import_report.md"
SUMMARY = ROOT / "tmp" / "xi_thought_import_summary.json"

TYPE_NAME = {
    "single": "单选题",
    "multi": "多选题",
    "essay": "大题",
}

TYPE_PREFIX = {
    "single": "单",
    "multi": "多",
    "essay": "简",
}


def normalize_text(value):
    text = str(value or "")
    text = text.replace("\xa0", " ").replace("\u3000", " ").replace("\t", " ")
    text = text.replace("✦", "")
    text = re.sub(r"\s+", " ", text)
    return text.strip()


def clean_lines(document):
    return [line for line in (normalize_text(p.text) for p in document.paragraphs) if line]


def find_source_docx(source):
    if source:
        path = Path(source)
        if not path.exists():
            raise SystemExit(f"Source DOCX not found: {path}")
        return path

    if not DEFAULT_SOURCE_DIR.exists():
        raise SystemExit(f"Default source directory not found: {DEFAULT_SOURCE_DIR}")
    candidates = [
        p for p in DEFAULT_SOURCE_DIR.iterdir()
        if p.suffix.lower() == ".docx" and "已作答" not in p.name
    ]
    if not candidates:
        raise SystemExit(f"No source DOCX found in {DEFAULT_SOURCE_DIR}")
    return max(candidates, key=lambda p: p.stat().st_mtime)


def is_chapter_heading(line):
    return bool(
        line.startswith("导论")
        or re.match(r"^第\d+讲\s+", line)
        or re.match(r"^第[０-９]+讲\s+", line)
    )


def is_single_heading(line):
    return line.startswith("（一）") and "单选题" in line


def is_multi_heading(line):
    return line.startswith("（二）") and "多选题" in line


def is_short_heading(line):
    return line.startswith("二、") and "简答题" in line


def is_subchapter_heading(line):
    return bool(re.match(r"^第[一二三四五六七八九十0-9０-９]+章\s*", line))


def is_answer_heading(line):
    return line.startswith("参考答案")


def is_next_section_start(line):
    return (
        is_chapter_heading(line)
        or is_single_heading(line)
        or is_multi_heading(line)
        or is_short_heading(line)
        or is_subchapter_heading(line)
        or line.startswith("一、选择题")
    )


QUESTION_RE = re.compile(r"^(\d+)[\.．、]*\s*(.+)$")
OPTION_RE = re.compile(r"^([A-D])[\.\uff0e．、]\s*(.+)$")
SHORT_QUESTION_RE = re.compile(r"^(\d+)[\.．、]\s*(.+)$")


def parse_choice_blocks(lines):
    questions = []
    current = None
    for line in lines:
        option_match = OPTION_RE.match(line)
        question_match = QUESTION_RE.match(line)
        if question_match and not option_match:
            if current:
                questions.append(current)
            current = {
                "num": int(question_match.group(1)),
                "stem": normalize_text(question_match.group(2)),
                "options": [],
            }
            continue

        if current and option_match:
            key = option_match.group(1)
            text = normalize_text(option_match.group(2))
            current["options"].append({"key": key, "text": f"{key}. {text}"})
            continue

        if current:
            if current["options"]:
                current["options"][-1]["text"] = normalize_text(current["options"][-1]["text"] + " " + line)
            else:
                current["stem"] = normalize_text(current["stem"] + " " + line)

    if current:
        questions.append(current)
    return questions


def parse_answer_map(lines):
    answer_text = "".join(normalize_text(line).upper() for line in lines)
    answer_text = answer_text.replace("参考答案", "")
    answer_text = re.sub(r"[\s:：,，;；]+", "", answer_text)

    answers = {}
    consumed = [False] * len(answer_text)

    for match in re.finditer(r"(\d+)-(\d+)\.?([A-D]+)", answer_text):
        start = int(match.group(1))
        end = int(match.group(2))
        letters = match.group(3)
        if end >= start and len(letters) == end - start + 1:
            for offset, number in enumerate(range(start, end + 1)):
                answers[number] = letters[offset]
            for index in range(match.start(), match.end()):
                consumed[index] = True

    remaining = "".join(" " if consumed[index] else char for index, char in enumerate(answer_text))
    for match in re.finditer(r"(\d+)[\.．、]?([A-D]+)", remaining):
        answers[int(match.group(1))] = match.group(2)
    for match in re.finditer(r"(\d+)([A-D]+)", remaining):
        answers.setdefault(int(match.group(1)), match.group(2))
    return answers


def strip_number(line):
    match = QUESTION_RE.match(line)
    return normalize_text(match.group(2) if match else line)


def option_plain_text(option):
    return re.sub(r"^[A-D][\.\uff0e．、]\s*", "", option["text"]).strip()


def chapter_topic(chapter):
    topic = re.sub(r"^第\d+讲\s*", "", chapter)
    topic = re.sub(r"^第[０-９]+讲\s*", "", topic)
    return topic.strip() or chapter


def compact(value, limit=72):
    text = normalize_text(value)
    if len(text) <= limit:
        return text
    return text[: limit - 1].rstrip("，。；、 ") + "…"


def stem_focus(stem):
    text = re.sub(r"（\s*）|\(\s*\)|____+", "空格", stem)
    return compact(text, 64)


def selected_option_texts(question, answer):
    selected = set(answer)
    return [
        (option["key"], option_plain_text(option))
        for option in question["options"]
        if option["key"] in selected
    ]


def derive_knowledge(chapter, stem, correct_text):
    topic = chapter_topic(chapter)
    if correct_text:
        candidate = correct_text[0][1]
        return compact(f"{topic}：{candidate}", 40)
    return compact(topic, 40)


def choice_explanation(question, answer, chapter, question_type):
    answer = "".join(sorted(set(answer), key="ABCD".index))
    correct_texts = selected_option_texts(question, answer)
    answer_label = "、".join(key for key, _ in correct_texts)
    answer_phrases = "；".join(f"{key}. {text}" for key, text in correct_texts)
    if not answer_phrases:
        answer_phrases = answer

    if question_type == "multi":
        reason = (
            f"本题理由：题干关键词是“{stem_focus(question['stem'])}”。参考答案为 {answer_label}，"
            f"对应要点是：{compact(answer_phrases, 90)}。多选题要把所有符合题干限定的要点一次选全，"
            "同时排除不在参考答案组合里的相近表述。"
        )
    else:
        reason = (
            f"本题理由：题干关键词是“{stem_focus(question['stem'])}”。参考答案为 {answer_label}，"
            f"对应“{compact(answer_phrases, 80)}”。做这类题要把题干问法和教材标准表述一一对应。"
        )

    option_lines = ["选项判断："]
    selected = set(answer)
    for option in question["options"]:
        key = option["key"]
        text = option_plain_text(option)
        if key in selected:
            verdict = "选" if question_type == "multi" else "应选"
            option_lines.append(f"- {key}：{verdict}。{text} 是本题参考答案锁定的表述。")
        else:
            option_lines.append(
                f"- {key}：不选。{text} 与题干所问的标准答案不对应，容易和本章相近概念混淆。"
            )

    quick = reason + "\n\n" + "\n".join(option_lines)
    detail = "\n".join([
        "核心知识框架：",
        f"- 本题考点：{chapter_topic(chapter)}。",
        f"- 题干落点：{question['stem']}",
        f"- 答案出口：{answer_label}，即 {answer_phrases}。",
        "- 复习抓手：先抓题干中的限定词，再回到教材中的标准表述；不要只凭熟悉词语选相近项。",
        "",
        "易混辨析表：",
        "| 项目 | 怎么判断 |",
        "| --- | --- |",
        f"| 正确答案 | {answer_phrases} 与题干设问直接对应 |",
        "| 干扰项 | 可能也是本章相关概念，但不是本题问法下的标准出口 |",
        "",
        "做题抓手：",
        "- 先看题干问的是“是什么、为什么、怎样做”中的哪一种。",
        "- 再把关键词和选项中的规范表述配对。",
        "- 多选题要核对答案组合，少选和多选都会失分。",
    ])
    return quick, detail, f"【快速做题】\n{quick}\n\n【知识点详解】\n{detail}"


def normalize_answer_prefix(line):
    return re.sub(r"^(答|答案要点)[:：]\s*", "", line).strip()


def looks_like_short_question(line):
    match = SHORT_QUESTION_RE.match(line)
    if not match:
        return False
    body = match.group(2)
    if "？" in body or "?" in body:
        return True
    triggers = ("是什么", "有哪些", "哪些方面", "如何理解", "怎样", "为什么", "怎么", "主要内容")
    return any(trigger in body for trigger in triggers)


def parse_short_answers(lines):
    items = []
    current = None
    answer_lines = []

    def finish():
        if not current:
            return
        answer = normalize_text("\n".join(answer_lines))
        if not answer:
            raise SystemExit(f"Short answer question has no answer: {current['stem']}")
        current["answer"] = answer
        items.append(current.copy())

    for line in lines:
        if looks_like_short_question(line):
            finish()
            match = SHORT_QUESTION_RE.match(line)
            current = {
                "num": int(match.group(1)),
                "stem": normalize_text(match.group(2)),
            }
            answer_lines = []
            continue
        if current:
            answer_lines.append(normalize_answer_prefix(line) if not answer_lines else line)

    finish()
    return items


def answer_outline(answer, max_items=5):
    parts = re.split(r"[；;。]\s*", answer)
    picked = [compact(part, 46) for part in parts if normalize_text(part)]
    return picked[:max_items] or [compact(answer, 46)]


def essay_explanation(stem, answer, chapter):
    outline = answer_outline(answer)
    quick = "\n".join([
        f"本题答案：可围绕“{compact(stem, 42)}”展开。",
        f"本题理由：本题考查“{chapter_topic(chapter)}”中的核心问答。参考答案已经给出可直接背诵的要点，复习时要按层次写，不要只写一个总括句。",
        "答题抓手：先写总观点，再按要点分层展开，最后回扣题干中的关键词。",
    ])
    detail_lines = [
        "核心知识框架：",
        f"- 本题考点：{chapter_topic(chapter)}。",
        f"- 题干落点：{stem}",
        "- 答案要点：",
    ]
    detail_lines.extend(f"  - {item}" for item in outline)
    detail_lines.extend([
        "",
        "做题抓手：",
        "- 如果题目问“是什么”，重点写概念和构成要点。",
        "- 如果题目问“为什么”，重点写理论依据、现实依据和意义。",
        "- 如果题目问“怎样”，重点写原则、路径和具体要求。",
    ])
    detail = "\n".join(detail_lines)
    return quick, detail, f"【快速做题】\n{quick}\n\n【知识点详解】\n{detail}"


def build_questions(lines):
    questions = []
    sections = []
    type_counters = defaultdict(int)
    current_chapter = "导论 马克思主义中国化时代化的新飞跃"
    index = 0

    def add_question(question_type, chapter, stem, options, answer):
        type_counters[question_type] += 1
        if question_type == "essay":
            quick, detail, explanation = essay_explanation(stem, answer, chapter)
            knowledge = compact(chapter_topic(chapter), 40)
        else:
            quick, detail, explanation = choice_explanation(
                {"stem": stem, "options": options}, answer, chapter, question_type
            )
            knowledge = derive_knowledge(chapter, stem, selected_option_texts({"options": options}, answer))
        questions.append({
            "id": len(questions) + 1,
            "label": f"习-{TYPE_PREFIX[question_type]}-{type_counters[question_type]:03d}",
            "type": question_type,
            "typeName": TYPE_NAME[question_type],
            "stem": stem,
            "options": options,
            "answer": answer,
            "images": [],
            "knowledge": knowledge,
            "chapter": chapter,
            "blankCount": 0,
            "quickExplanation": quick,
            "knowledgeDetail": detail,
            "explanation": explanation,
        })

    while index < len(lines):
        line = lines[index]
        if is_chapter_heading(line):
            current_chapter = line
            index += 1
            continue

        if is_single_heading(line) or is_multi_heading(line):
            question_type = "single" if is_single_heading(line) else "multi"
            heading = line
            index += 1
            raw_questions = []
            while index < len(lines) and not is_answer_heading(lines[index]):
                raw_questions.append(lines[index])
                index += 1
            if index >= len(lines):
                raise SystemExit(f"Missing answer section after {current_chapter} {heading}")
            index += 1
            answer_lines = []
            while index < len(lines) and not is_next_section_start(lines[index]):
                answer_lines.append(lines[index])
                index += 1

            parsed_questions = parse_choice_blocks(raw_questions)
            answers = parse_answer_map(answer_lines)
            missing = [q["num"] for q in parsed_questions if q["num"] not in answers]
            if missing:
                raise SystemExit(f"Missing answers in {current_chapter} {heading}: {missing}")
            if len(parsed_questions) != len(answers):
                raise SystemExit(
                    f"Question/answer count mismatch in {current_chapter} {heading}: "
                    f"{len(parsed_questions)} questions, {len(answers)} answers"
                )
            for parsed in parsed_questions:
                answer = answers[parsed["num"]]
                if question_type == "single" and len(answer) != 1:
                    raise SystemExit(f"Single-choice answer is not single: {current_chapter} Q{parsed['num']} {answer}")
                answer = "".join(sorted(set(answer), key="ABCD".index))
                option_keys = {option["key"] for option in parsed["options"]}
                if any(letter not in option_keys for letter in answer):
                    raise SystemExit(f"Answer outside options: {current_chapter} Q{parsed['num']} {answer}")
                add_question(question_type, current_chapter, parsed["stem"], parsed["options"], answer)
            sections.append({
                "chapter": current_chapter,
                "type": question_type,
                "heading": heading,
                "questions": len(parsed_questions),
            })
            continue

        if is_short_heading(line):
            chapter = current_chapter
            index += 1
            raw_short = []
            while index < len(lines) and not is_next_section_start(lines[index]):
                raw_short.append(lines[index])
                index += 1
            parsed_short = parse_short_answers(raw_short)
            for parsed in parsed_short:
                add_question("essay", chapter, parsed["stem"], [], parsed["answer"])
            sections.append({
                "chapter": chapter,
                "type": "essay",
                "heading": line,
                "questions": len(parsed_short),
            })
            continue

        if is_subchapter_heading(line) or line.startswith("一、选择题"):
            index += 1
            continue

        index += 1

    return questions, sections


def validate_questions(questions):
    labels = [q["label"] for q in questions]
    duplicates = [label for label, count in Counter(labels).items() if count > 1]
    if duplicates:
        raise SystemExit(f"Duplicate labels: {duplicates[:10]}")

    for q in questions:
        for key in ("id", "label", "type", "typeName", "stem", "answer", "chapter", "knowledge", "explanation"):
            if q.get(key) in (None, "", []):
                raise SystemExit(f"Empty required field {key}: {q.get('label')}")
        if "\ufffd" in json.dumps(q, ensure_ascii=False) or "????" in json.dumps(q, ensure_ascii=False):
            raise SystemExit(f"Possible mojibake in {q['label']}")
        if q["type"] in ("single", "multi"):
            option_keys = {option["key"] for option in q["options"]}
            if len(option_keys) != len(q["options"]):
                raise SystemExit(f"Duplicate option key in {q['label']}")
            if any(letter not in option_keys for letter in q["answer"]):
                raise SystemExit(f"Answer outside options in {q['label']}")
            if q["type"] == "single" and len(q["answer"]) != 1:
                raise SystemExit(f"Single-choice answer is not single in {q['label']}")
        if q["type"] == "essay" and q["options"]:
            raise SystemExit(f"Essay should not have options in {q['label']}")


def write_report(source, questions, sections):
    type_counts = Counter(q["type"] for q in questions)
    chapter_counts = Counter(q["chapter"] for q in questions)
    lines = [
        "# 习近平思想课程导入报告",
        "",
        f"- 源文件：`{source}`",
        f"- 输出题库：`{OUTPUT}`",
        f"- 总题量：{len(questions)}",
        f"- 题型分布：单选 {type_counts['single']}，多选 {type_counts['multi']}，大题 {type_counts['essay']}",
        f"- 章节数：{len(chapter_counts)}",
        "",
        "## 章节分布",
        "",
    ]
    for chapter, count in chapter_counts.items():
        lines.append(f"- {chapter}：{count} 题")
    lines.extend(["", "## 区块校验", ""])
    for section in sections:
        lines.append(
            f"- {section['chapter']} / {TYPE_NAME[section['type']]}：{section['questions']} 题"
        )
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text("\n".join(lines) + "\n", encoding="utf-8")


def write_summary(source, questions, sections, intermediate):
    type_counts = Counter(q["type"] for q in questions)
    chapter_counts = Counter(q["chapter"] for q in questions)
    payload = {
        "courseName": "习近平思想",
        "source": str(source),
        "intermediate": str(intermediate),
        "output": str(OUTPUT),
        "questionCount": len(questions),
        "typeCounts": dict(type_counts),
        "chapterCount": len(chapter_counts),
        "chapterCounts": dict(chapter_counts),
        "sections": sections,
    }
    SUMMARY.parent.mkdir(parents=True, exist_ok=True)
    SUMMARY.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main():
    parser = argparse.ArgumentParser(description="Import Xi Thought review DOCX as an Exam Prep Handbook course.")
    parser.add_argument("--source-docx", help="Source DOCX path. Defaults to the latest raw DOCX in QQCLI incoming-files.")
    parser.add_argument("--check", action="store_true", help="Parse and validate without writing the asset file.")
    args = parser.parse_args()

    try:
        from docx import Document
    except ImportError as exc:
        raise SystemExit("python-docx is required to read the source DOCX.") from exc

    source = find_source_docx(args.source_docx)
    document = Document(source)
    lines = clean_lines(document)
    questions, sections = build_questions(lines)
    validate_questions(questions)

    if not args.check:
        ASSETS_DIR.mkdir(parents=True, exist_ok=True)
        intermediate = source.parent / "xi_thought_questions_extracted.json"
        intermediate.write_text(json.dumps(questions, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        OUTPUT.write_text(json.dumps(questions, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        write_report(source, questions, sections)
        write_summary(source, questions, sections, intermediate)

    type_counts = Counter(q["type"] for q in questions)
    chapter_count = len(Counter(q["chapter"] for q in questions))
    print(
        "Imported Xi Thought course:",
        f"{len(questions)} questions,",
        f"single={type_counts['single']},",
        f"multi={type_counts['multi']},",
        f"essay={type_counts['essay']},",
        f"chapters={chapter_count}",
    )
    if args.check:
        print("Check only; no files changed.")


if __name__ == "__main__":
    main()
