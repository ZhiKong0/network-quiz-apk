import argparse
import json
import re
from collections import Counter
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
QUESTIONS_PATH = ROOT / "app" / "src" / "main" / "assets" / "xi_thought_questions.json"
REPORT_PATH = ROOT / "explanation_work" / "xi_highlight_refinement_report.md"

BANNED_PHRASES = [
    "对应哪一个规范表述",
    "判断方式是先定题干落点",
    "其他选项即使常见",
    "正好补上题干",
    "属于本题要找的",
    "方向相关，但层级不对",
    "不是单纯考词语熟悉度",
    "先想它问的是目标、原则、路径、主体还是制度",
    "相关概念",
    "中的关键词",
    "定义、地位或作用",
    "不是只记一个孤立词",
    "这一概念要放在",
    "通常用于回答本章某个具体限定语",
]

CHAPTER_THEME = {
    "导论": "习近平新时代中国特色社会主义思想的形成、主体内容、世界观方法论和历史地位",
    "第01讲": "新时代坚持和发展中国特色社会主义的历史方位、主要矛盾和道路自信",
    "第02讲": "中国式现代化的中国特色、本质要求和战略安排",
    "第03讲": "党的全面领导、党的建设和全面从严治党",
    "第04讲": "以人民为中心、人民当家作主和全过程人民民主",
    "第05讲": "全面深化改革开放的方向、动力和制度完善",
    "第06讲": "高质量发展、新发展理念和现代化经济体系",
    "第07讲": "教育、科技、人才在现代化建设中的基础性战略支撑",
    "第08讲": "全面依法治国、中国特色社会主义法治体系和法治中国建设",
    "第09讲": "文化自信、社会主义核心价值观和社会主义文化强国",
    "第10讲": "民生建设、社会治理和共同富裕",
    "第11讲": "生态文明、绿色发展和人与自然和谐共生",
    "第12讲": "总体国家安全观、强军目标和国家安全体系",
    "第13讲": "祖国统一、中国特色大国外交和人类命运共同体",
}

CONCEPT_BANK = {
    "实现中华民族伟大复兴": "实现中华民族伟大复兴是近代以来中国人民最伟大的梦想，也是中国共产党长期奋斗的历史主题；它回答的是“最终要实现什么目标”。",
    "中国式现代化": "中国式现代化是中国共产党领导的社会主义现代化，是全面推进中华民族伟大复兴的道路和方式；它不是照搬西方现代化模式。",
    "中国特色社会主义": "中国特色社会主义是改革开放以来党的全部理论和实践的主题，回答的是中国走什么道路、坚持什么制度和方向。",
    "“六个必须坚持”": "“六个必须坚持”集中体现习近平新时代中国特色社会主义思想的世界观和方法论，包括人民至上、自信自立、守正创新、问题导向、系统观念、胸怀天下。",
    "六个必须坚持": "“六个必须坚持”集中体现习近平新时代中国特色社会主义思想的世界观和方法论，包括人民至上、自信自立、守正创新、问题导向、系统观念、胸怀天下。",
    "“三个务必”": "“三个务必”是党的二十大提出的政治要求：务必不忘初心、牢记使命，务必谦虚谨慎、艰苦奋斗，务必敢于斗争、善于斗争。",
    "三个务必": "“三个务必”是党的二十大提出的政治要求：务必不忘初心、牢记使命，务必谦虚谨慎、艰苦奋斗，务必敢于斗争、善于斗争。",
    "“五个必由之路”": "“五个必由之路”总结新时代成功经验，强调坚持党的全面领导、中国特色社会主义、团结奋斗、贯彻新发展理念、全面从严治党。",
    "五个必由之路": "“五个必由之路”总结新时代成功经验，强调坚持党的全面领导、中国特色社会主义、团结奋斗、贯彻新发展理念、全面从严治党。",
    "“十个明确”": "“十个明确”是习近平新时代中国特色社会主义思想的主体内容，集中体现主要观点和基本精神，起到“四梁八柱”的统摄作用。",
    "十个明确": "“十个明确”是习近平新时代中国特色社会主义思想的主体内容，集中体现主要观点和基本精神，起到“四梁八柱”的统摄作用。",
    "“十三个方面”": "“十三个方面成就”概括新时代党和国家事业取得的历史性成就、发生的历史性变革，是成就维度，不是思想主体内容。",
    "十三个方面": "“十三个方面成就”概括新时代党和国家事业取得的历史性成就、发生的历史性变革，是成就维度，不是思想主体内容。",
    "“十四个坚持”": "“十四个坚持”是新时代坚持和发展中国特色社会主义的基本方略，更偏向实践层面的路线和要求。",
    "十四个坚持": "“十四个坚持”是新时代坚持和发展中国特色社会主义的基本方略，更偏向实践层面的路线和要求。",
    "人民至上": "人民至上是根本价值立场，强调发展为了人民、发展依靠人民、发展成果由人民共享。",
    "社会主义": "社会主义强调生产资料公有、共同富裕和人民主体地位，是制度属性层面的概念。",
    "人民": "人民是历史的创造者，是决定党和国家前途命运的根本力量；相关题目常考“主体、根基、价值归宿”。",
    "以人民为中心": "以人民为中心是发展思想和价值取向，要求把人民对美好生活的向往作为奋斗目标。",
    "人民当家作主": "人民当家作主是社会主义民主政治的本质和核心，回答“国家权力属于谁、由谁来管理国家事务”。",
    "全过程人民民主": "全过程人民民主把民主选举、民主协商、民主决策、民主管理、民主监督贯通起来，是全链条、全方位、全覆盖的民主。",
    "党的领导": "党的领导是中国特色社会主义最本质的特征和最大优势，也是推进各项事业的根本保证。",
    "中国共产党领导": "中国共产党领导是中国特色社会主义最本质的特征，是中国特色社会主义制度的最大优势。",
    "“两个确立”": "“两个确立”指确立习近平同志党中央的核心、全党的核心地位，确立习近平新时代中国特色社会主义思想的指导地位。",
    "两个确立": "“两个确立”指确立习近平同志党中央的核心、全党的核心地位，确立习近平新时代中国特色社会主义思想的指导地位。",
    "“两个维护”": "“两个维护”指坚决维护习近平总书记党中央的核心、全党的核心地位，坚决维护党中央权威和集中统一领导。",
    "两个维护": "“两个维护”指坚决维护习近平总书记党中央的核心、全党的核心地位，坚决维护党中央权威和集中统一领导。",
    "“四个自信”": "“四个自信”是道路自信、理论自信、制度自信、文化自信，强调坚持中国特色社会主义的信心来源。",
    "四个自信": "“四个自信”是道路自信、理论自信、制度自信、文化自信，强调坚持中国特色社会主义的信心来源。",
    "党的十八大": "党的十八大标志中国特色社会主义进入新时代相关实践的开启，但把习近平新时代中国特色社会主义思想确立为长期指导思想是在党的十九大。",
    "党的十九大": "党的十九大把习近平新时代中国特色社会主义思想确立为党必须长期坚持的指导思想并写入党章。",
    "党的二十大": "党的二十大围绕全面建设社会主义现代化国家、全面推进中华民族伟大复兴作出战略部署。",
    "党的十九届六中全会": "党的十九届六中全会通过第三个历史决议，突出总结党的百年奋斗重大成就和历史经验。",
    "党的十八届三中全会": "党的十八届三中全会重点部署全面深化改革，也首次提出推进法治中国建设的目标任务。",
    "全面从严治党": "全面从严治党是新时代党的建设鲜明主题，核心是把严的基调、严的措施、严的氛围长期坚持下去。",
    "反腐败": "反腐败是最彻底的自我革命，关系党能否跳出治乱兴衰历史周期率。",
    "民主集中制": "民主集中制是党的根本组织原则和领导制度，强调充分发扬民主与正确集中相统一。",
    "改革开放": "改革开放是决定当代中国命运的关键一招，是坚持和发展中国特色社会主义、实现中华民族伟大复兴的必由之路。",
    "全面深化改革开放": "全面深化改革开放重在破除体制机制障碍、推进国家治理体系和治理能力现代化。",
    "高质量发展": "高质量发展是全面建设社会主义现代化国家的首要任务，强调质量、效率、动力变革，而不是单纯追求速度。",
    "新发展理念": "新发展理念包括创新、协调、绿色、开放、共享，是引领高质量发展的指挥棒。",
    "创新": "创新是引领发展的第一动力，在现代化建设全局中居于核心位置。",
    "科技、教育、人才同步发展": "教育、科技、人才是全面建设社会主义现代化国家的基础性、战略性支撑，三者共同支撑高质量发展。",
    "科技": "科技自立自强是国家强盛之基、安全之要，是高质量发展的关键支撑。",
    "教育": "教育是国之大计、党之大计，是基础性、先导性事业。",
    "人才": "人才是第一资源，是创新活动中最活跃、最积极的因素。",
    "依法治国": "全面依法治国是国家治理的一场深刻革命，核心是建设中国特色社会主义法治体系、建设社会主义法治国家。",
    "建设中国特色社会主义法治体系": "建设中国特色社会主义法治体系是全面依法治国的总抓手。",
    "建设社会主义法治国家": "建设社会主义法治国家是全面依法治国的重要目标。",
    "宪法": "宪法是国家的根本法，是治国安邦的总章程。",
    "公正司法": "公正司法是维护社会公平正义的最后一道防线，常与科学立法、严格执法、全民守法并列区分。",
    "党内法规体系": "党内法规体系纳入中国特色社会主义法治体系，体现依规治党和依法治国有机统一。",
    "科学立法": "科学立法是良法善治的前提，强调立法要符合规律、反映人民意志、解决实际问题。",
    "严格执法": "严格执法要求行政机关依法全面履职，是法治政府建设的关键环节。",
    "全民守法": "全民守法强调全体社会成员尊法学法守法用法，是法治社会建设的基础。",
    "文化自信": "文化自信是更基础、更广泛、更深厚的自信，是道路自信、理论自信、制度自信的文化根基。",
    "中华优秀传统文化": "中华优秀传统文化是中华民族的突出优势，是中国特色社会主义植根的文化沃土。",
    "社会主义核心价值观": "社会主义核心价值观凝结全体人民共同价值追求，是凝魂聚气、强基固本的基础工程。",
    "增进民生福祉": "增进民生福祉是发展的根本目的，民生题常围绕就业、收入、教育、医疗、社保、住房等展开。",
    "共同富裕": "共同富裕是社会主义的本质要求，是中国式现代化的重要特征，不等于同步富裕或平均主义。",
    "国家安全": "国家安全是民族复兴的根基，社会稳定是国家强盛的前提。",
    "政治安全": "政治安全是国家安全的根本，核心是政权安全和制度安全。",
    "总体国家安全观": "总体国家安全观强调统筹发展和安全，构建大安全格局。",
    "党对人民军队的绝对领导": "党对人民军队的绝对领导是人民军队的建军之本、强军之魂。",
    "生态文明": "生态文明建设处理人与自然关系，强调绿水青山就是金山银山。",
    "一国两制": "“一国两制”是在一个中国前提下解决香港、澳门、台湾问题的制度安排。",
    "推动构建人类命运共同体": "推动构建人类命运共同体是新时代中国外交的鲜明旗帜，主张各国命运相连、合作共赢。",
    "人类命运共同体": "人类命运共同体强调持久和平、普遍安全、共同繁荣、开放包容、清洁美丽。",
    "一带一路": "“一带一路”是开放合作平台，核心在互联互通、政策沟通、设施联通、贸易畅通、资金融通、民心相通。",
    "推动中国与世界携手并进": "推动中国与世界携手并进强调中国发展同世界发展相互联系，属于胸怀天下和开放合作的视角。",
    "跨越中等收入陷阱": "跨越中等收入陷阱是发展阶段和经济质量问题，重点在转变发展方式、提升创新能力和共同富裕。",
    "公正合理": "公正合理强调全球治理规则和秩序要体现公平正义，反对霸权和强权。",
    "互商互谅": "互商互谅强调通过协商沟通处理国际分歧，体现共商精神。",
    "同舟共济": "同舟共济强调面对全球性挑战各国命运相连，需要合作应对。",
    "互利共赢": "互利共赢强调合作不能是一方获利，而要兼顾各方发展利益。",
}


def compact(text, limit=92):
    text = re.sub(r"\s+", " ", str(text or "")).strip()
    return text if len(text) <= limit else text[: limit - 1].rstrip("，。；、：,. ") + "…"


def clean_option_text(text):
    return re.sub(r"^[A-Z][\.\u3001\uff0e]\s*", "", str(text or "")).strip()


def answer_options(question):
    keys = set(str(question["answer"]))
    return [opt for opt in question.get("options", []) if opt["key"] in keys]


def answer_phrase(question):
    if question["type"] not in ("single", "multi"):
        return str(question.get("answer", ""))
    return "、".join(clean_option_text(opt["text"]) for opt in answer_options(question))


def chapter_theme(chapter):
    for prefix, theme in CHAPTER_THEME.items():
        if str(chapter).startswith(prefix):
            return theme
    return str(chapter)


def concept_description(phrase, chapter="", stem=""):
    phrase = str(phrase or "").strip(" 。；，")
    normalized = phrase.replace("“", "").replace("”", "").replace('"', "")
    if "；" in phrase or "、" in phrase or "?" in phrase:
        parts = [p.strip() for p in re.split(r"[；、?]+", phrase) if p.strip()]
        if len(parts) >= 2:
            described = []
            for part in parts[:6]:
                item = concept_description(part, chapter, stem)
                described.append(item.rstrip("。"))
            return "；".join(described) + "。"
    for key, value in CONCEPT_BANK.items():
        if key in phrase or key.replace("“", "").replace("”", "") == normalized:
            return value
    if re.fullmatch(r"\d{4}年.*|\d+年|\d+月|\d+日", phrase):
        return f"{phrase}是时间节点类考点，重点不是展开论述，而是把事件、会议或目标完成时间对应准确。"
    if "会议" in phrase or "全会" in phrase or "党代会" in phrase:
        return f"{phrase}是会议节点类考点，复习时要把它和“首次提出、作出部署、形成决议、明确目标”等关键词绑定。"
    if "坚持" in phrase:
        return f"{phrase}是“{chapter_theme(chapter)}”中的原则性表述，答题时要说明它规范的是立场、方向或方法。"
    if "建设" in phrase or "推进" in phrase or "完善" in phrase:
        return f"{phrase}是“{chapter_theme(chapter)}”中的任务路径，重点在于它回答“怎么推进、建成什么”。"
    if "制度" in phrase or "体系" in phrase:
        return f"{phrase}是“{chapter_theme(chapter)}”中的制度或体系安排，常考它与目标、原则、具体措施的区别。"
    if "发展" in phrase:
        return f"{phrase}是“{chapter_theme(chapter)}”中的发展类考点，复习时要区分发展目标、发展理念、发展动力和发展成果。"
    return f"{phrase}的复习定位：围绕“{chapter_theme(chapter)}”理解，先判断它回答的是目标、原则、路径、主体、制度还是时间节点，再和题干限定语对照。"


def stem_focus(stem):
    text = re.sub(r"\s+", " ", str(stem or "")).strip()
    text = text.replace("（ ）", "____").replace("( )", "____")
    if "____" in text:
        left, _, right = text.partition("____")
        window = compact((left[-28:] + "____" + right[:44]).strip(" ，。；"), 74)
        if window and window != "____":
            return window
    for marker in ["首次提出", "最后一道防线", "根本", "本质", "核心", "首要", "关键", "最大", "共同梦想", "基本方略", "主体内容"]:
        if marker in text:
            start = max(0, text.index(marker) - 18)
            return compact(text[start:], 74)
    return compact(text, 74)


def wrong_reason(option_text, correct_phrase, question):
    wrong_desc = concept_description(option_text, question["chapter"], question["stem"])
    focus = stem_focus(question["stem"])
    return (
        f"{option_text}考的是：{wrong_desc} "
        f"本题题眼是“{focus}”，答案应落到“{correct_phrase}”。"
    )


def rewrite_choice(question):
    answer = "".join(sorted(set(str(question["answer"])), key="ABCDEFGHIJKLMNOPQRSTUVWXYZ".index))
    question["answer"] = answer
    correct_phrase = answer_phrase(question)
    focus = stem_focus(question["stem"])
    correct_desc = concept_description(correct_phrase, question["chapter"], question["stem"])
    is_multi = question["type"] == "multi"

    quick = [
        f"核心知识点：{correct_desc}",
        f"答题抓手：题干的关键限定是“{focus}”。看到这句话，先想它要考的完整知识点是不是“{correct_phrase}”，再看选项有没有偷换成同章其他说法。"
    ]
    if is_multi:
        quick.append(f"答案逻辑：本题为多选，{answer} 共同构成“{compact(focus, 44)}”所要求的一组并列要点；少选会导致知识点不完整。")
    else:
        quick.append(f"答案逻辑：选 {answer}。因为“{correct_phrase}”正是这句话要考的完整知识点，不只是一个熟悉名词。")
    quick.append("选项辨析：")

    selected = set(answer)
    for opt in question["options"]:
        key = opt["key"]
        option_text = clean_option_text(opt["text"])
        if key in selected:
            quick.append(f"- {key}：应选。{concept_description(option_text, question['chapter'], question['stem'])}")
        else:
            quick.append(f"- {key}：不选。{wrong_reason(option_text, correct_phrase, question)}")

    wrongs = [clean_option_text(opt["text"]) for opt in question["options"] if opt["key"] not in selected]
    detail = [
        "知识点展开：",
        f"- 所属章节：{chapter_theme(question['chapter'])}。",
        f"- 本题要背的完整句意：{compact(question['stem'].replace('（ ）', correct_phrase).replace('( )', correct_phrase), 150)}",
        f"- 正确项定位：{correct_desc}",
        "",
        "考点边界：",
    ]
    if wrongs:
        for text in wrongs[:4]:
            detail.append(f"- {text}：{concept_description(text, question['chapter'], question['stem'])}")
    else:
        detail.append("- 四个选项都是正确项时，重点是把并列结构整体记住，考试常考漏选。")
    detail.extend([
        "",
        "复习抓手：",
        f"- 把“{compact(focus, 34)}”和“{compact(correct_phrase, 48)}”绑定成一组问答。",
        "- 做选择题时先判断题干问的是定义、地位、作用、目标还是路径，再排除只是在同章出现但不回答该限定语的选项。",
    ])

    question["quickExplanation"] = "\n".join(quick)
    question["knowledgeDetail"] = "\n".join(detail)
    question["explanation"] = f"【快速做题】\n{question['quickExplanation']}\n\n【知识点详解】\n{question['knowledgeDetail']}"


def split_essay_points(answer):
    raw = str(answer or "").strip()
    parts = [p.strip(" ；。") for p in raw.split("；") if p.strip(" ；。")]
    if len(parts) <= 1:
        parts = [p.strip(" 。") for p in re.split(r"(?<=。)|(?<=；)", raw) if p.strip(" 。；")]
    return parts or [raw]


def rewrite_essay(question):
    points = split_essay_points(question["answer"])
    focus = compact(question["stem"], 70)
    quick = [
        f"核心知识点：本题考“{chapter_theme(question['chapter'])}”的简答框架，重点不是写感想，而是把参考答案里的核心短语分层写全。",
        f"答题抓手：题干问“{focus}”，先写一句总括，再按关键词逐条展开；每一点尽量保留原答案里的核心短语。",
        "答题要点：",
    ]
    for index, point in enumerate(points[:12], 1):
        quick.append(f"{index}. {point}。")
    quick.append("为什么这样答：简答题按得分短语给分，完整性比长篇套话更重要；每个分号前后通常就是一个得分点。")

    detail = [
        "知识点展开：",
        f"- 所属章节：{chapter_theme(question['chapter'])}。",
        f"- 答案结构：共 {len(points)} 个主要点，适合按“总括句 + 条目化要点”作答。",
        "",
        "考点边界：",
    ]
    for index, point in enumerate(points[:12], 1):
        detail.append(f"- 第 {index} 点：{compact(point, 86)}。")
    detail.extend([
        "",
        "复习抓手：",
        "- 先背每一点的关键词，再练习用自己的话补一句解释。",
        "- 考场写不出原句时，宁可写短而准的关键词，也不要用空泛政治口号填篇幅。",
    ])

    question["quickExplanation"] = "\n".join(quick)
    question["knowledgeDetail"] = "\n".join(detail)
    question["explanation"] = f"【快速做题】\n{question['quickExplanation']}\n\n【知识点详解】\n{question['knowledgeDetail']}"


def validate(questions):
    issues = []
    ids = [q["id"] for q in questions]
    if ids != list(range(1, len(questions) + 1)):
        issues.append("id 不连续")
    labels = [q["label"] for q in questions]
    for label, count in Counter(labels).items():
        if count > 1:
            issues.append(f"重复 label: {label}")
    for q in questions:
        text = q.get("quickExplanation", "") + "\n" + q.get("knowledgeDetail", "")
        for phrase in BANNED_PHRASES:
            if phrase in text:
                issues.append(f"{q['label']} contains banned phrase: {phrase}")
        if "核心知识点：" not in q.get("quickExplanation", ""):
            issues.append(f"{q['label']} missing 核心知识点")
        if "答题抓手：" not in q.get("quickExplanation", ""):
            issues.append(f"{q['label']} missing 答题抓手")
        if q["type"] in ("single", "multi"):
            keys = {opt["key"] for opt in q["options"]}
            if any(k not in keys for k in q["answer"]):
                issues.append(f"{q['label']} answer outside options")
            for opt in q["options"]:
                if f"{opt['key']}：" not in q.get("quickExplanation", ""):
                    issues.append(f"{q['label']} missing option {opt['key']}")
        if "�" in json.dumps(q, ensure_ascii=False) or "????" in json.dumps(q, ensure_ascii=False):
            issues.append(f"{q['label']} mojibake")
    return issues


def write_report(path, before_counts, after_issues, questions):
    samples = ["习-单-001", "习-单-004", "习-单-157", "习-多-142", "习-简-030"]
    by_label = {q["label"]: q for q in questions}
    lines = [
        "# 习近平思想黄色高亮精修报告",
        "",
        "## 修复目标",
        "",
        "- 黄色块不再显示“对应规范表述/层级不对/相关概念”等空话。",
        "- 每题开头固定给出 `核心知识点` 和 `答题抓手`，前者讲完整概念，后者讲如何由题眼锁答案。",
        "- 选择题选项辨析改为“该选项自身知识点 + 为什么不回答本题限定语”。",
        "",
        "## 质检结果",
        "",
        f"- 题量：{len(questions)}",
        f"- 题型：{dict(Counter(q['typeName'] for q in questions))}",
        f"- 修复前模板短语命中：{dict(before_counts)}",
        f"- 修复后问题数：{len(after_issues)}",
    ]
    if after_issues:
        lines.append("")
        lines.append("## 仍需处理")
        lines.extend(f"- {issue}" for issue in after_issues[:80])
    lines.append("")
    lines.append("## 样例")
    for label in samples:
        q = by_label.get(label)
        if not q:
            continue
        lines.extend([
            "",
            f"### {label}",
            "",
            q["stem"],
            "",
            q["quickExplanation"],
        ])
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", default=str(QUESTIONS_PATH))
    parser.add_argument("--output", default=str(QUESTIONS_PATH))
    parser.add_argument("--report", default=str(REPORT_PATH))
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()

    input_path = Path(args.input)
    questions = json.loads(input_path.read_text(encoding="utf-8"))
    before_counts = Counter()
    for q in questions:
        text = q.get("quickExplanation", "") + "\n" + q.get("knowledgeDetail", "")
        for phrase in BANNED_PHRASES:
            if phrase in text:
                before_counts[phrase] += 1

    for q in questions:
        if q["type"] in ("single", "multi"):
            rewrite_choice(q)
        elif q["type"] == "essay":
            rewrite_essay(q)

    issues = validate(questions)
    write_report(Path(args.report), before_counts, issues, questions)
    if issues:
        raise SystemExit("Validation failed: " + "; ".join(issues[:10]))
    if not args.check:
        Path(args.output).write_text(json.dumps(questions, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"questions={len(questions)} before_template_hits={sum(before_counts.values())} after_issues={len(issues)}")
    print(f"report={args.report}")


if __name__ == "__main__":
    main()
