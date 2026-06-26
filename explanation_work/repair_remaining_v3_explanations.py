from __future__ import annotations

import json
import re
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
QUESTIONS_PATH = ROOT / "app" / "src" / "main" / "assets" / "questions.json"
OUTPUT_DIR = ROOT / "explanation_work" / "agent_outputs"
CH4_OUT = OUTPUT_DIR / "v3_chapter4_rewrite.json"
CH57_OUT = OUTPUT_DIR / "v3_chapter5_7_rewrite.json"

BAN = [
    "同类判断题最爱",
    "抓手仍是",
    "本质都是先看",
    "不要只凭眼熟",
    "这类题常换成",
    "别被看起来顺口的表述带偏",
    "只凭眼熟",
]


def load_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def clean(text: str) -> str:
    value = str(text or "").replace("\r\n", "\n").replace("\r", "\n")
    for token in BAN:
        value = value.replace(token, "")
    value = re.sub(r"[ \t]+\n", "\n", value)
    value = re.sub(r"\n{3,}", "\n\n", value)
    return value.strip()


def one_line(text: str, limit: int = 80) -> str:
    value = re.sub(r"\s+", " ", str(text or "")).strip()
    return value if len(value) <= limit else value[: limit - 1] + "…"


def strip_option_text(text: str) -> str:
    return re.sub(r"^[A-Z]\.\s*", "", str(text or "").strip())


def line_value(text: str, labels: list[str]) -> str:
    for raw in str(text or "").replace("\r\n", "\n").split("\n"):
        line = raw.strip()
        for label in labels:
            if line.startswith(label):
                return clean(line[len(label):].strip())
    return ""


def existing_option_lines(text: str) -> dict[str, str]:
    lines: dict[str, str] = {}
    for raw in str(text or "").replace("\r\n", "\n").split("\n"):
        line = raw.strip()
        m = re.match(r"^-\s*([A-ZTF])(?:（[^）]*）)?\s*[：:]\s*(.+)$", line)
        if not m:
            continue
        key, body = m.group(1), clean(m.group(2))
        body = re.sub(r"^(对|错)[。；;，,]\s*", "", body)
        body = re.sub(r"^(应选|不选)[。；;，,]\s*", "", body)
        lines[key] = body
    return lines


def answer_keys(question: dict) -> set[str]:
    qtype = question.get("type")
    answer = question.get("answer")
    if qtype == "tf":
        return {"T"} if answer == "TRUE" else {"F"}
    if qtype == "single":
        return {str(answer)}
    if qtype == "multi":
        return set(str(answer))
    return set()


def answer_display(question: dict) -> str:
    qtype = question.get("type")
    answer = question.get("answer")
    if qtype == "tf":
        return "正确" if answer == "TRUE" else "错误"
    if qtype in {"single", "multi"}:
        chosen = answer_keys(question)
        parts = []
        for opt in question.get("options", []):
            if opt["key"] in chosen:
                parts.append(f"{opt['key']}. {strip_option_text(opt['text'])}")
        return "；".join(parts)
    if isinstance(answer, list):
        return "；".join(str(x) for x in answer)
    return str(answer)


def default_reason(question: dict) -> str:
    knowledge = question.get("knowledge", "")
    stem = one_line(question.get("stem", ""), 54)
    if "路由器" in knowledge or "路由" in knowledge:
        return f"题干核心是“{stem}”。网络层题先判断转发依据：路由器看 IP 地址和路由表，交换机看 MAC 地址，运输层才看端口。"
    if "IP 地址" in knowledge or "CIDR" in knowledge:
        return f"题干核心是“{stem}”。IP 地址题要先分清网络号、主机号、前缀长度和特殊地址，不能只看地址形式。"
    if "ARP" in knowledge or "ICMP" in knowledge or "IP 数据报" in knowledge:
        return f"题干核心是“{stem}”。这类题要分清 IP 数据报字段、ARP 地址解析、ICMP 差错/控制报文、IGMP 多播成员管理各自的职责。"
    if "TCP" in knowledge and "拥塞" not in knowledge:
        return f"题干核心是“{stem}”。TCP 题围绕连接、可靠传输、字节流、序号确认和窗口控制来判断。"
    if "UDP" in knowledge or "端口" in knowledge or "运输层服务" in knowledge:
        return f"题干核心是“{stem}”。运输层服务题要区分端到端、进程到进程、端口复用/分用，以及 TCP 与 UDP 的边界。"
    if "拥塞" in knowledge:
        return f"题干核心是“{stem}”。拥塞控制看慢开始、拥塞避免、快重传、快恢复和拥塞窗口变化。"
    if "DNS" in knowledge:
        return f"题干核心是“{stem}”。DNS 题要分清域名层次、递归/迭代查询、根域名服务器和本地域名服务器的作用。"
    if "FTP" in knowledge:
        return f"题干核心是“{stem}”。FTP 题最关键是控制连接和数据连接分开，控制连接常用 21 端口。"
    if "HTTP" in knowledge or "Web" in knowledge or "URL" in knowledge:
        return f"题干核心是“{stem}”。Web 题要区分 URL、HTTP、HTML、浏览器/服务器交互和无状态连接。"
    if "电子邮件" in knowledge:
        return f"题干核心是“{stem}”。邮件题要区分 SMTP 负责发送，POP3/IMAP 负责读取，MIME 负责扩展邮件内容类型。"
    if "DHCP" in knowledge or "SNMP" in knowledge:
        return f"题干核心是“{stem}”。应用层协议题要把协议用途和端口/对象模型对应起来。"
    if "密码" in knowledge or "数字签名" in knowledge:
        return f"题干核心是“{stem}”。安全题要分清加密、摘要、报文鉴别和数字签名解决的问题不同。"
    if "防火墙" in knowledge or "VPN" in knowledge:
        return f"题干核心是“{stem}”。防火墙和 VPN 题要分清过滤位置、代理网关、隧道加密和攻击类型。"
    return f"题干核心是“{stem}”。判断时要把题目关键词和本题知识点对应起来。"


def option_reason(question: dict, key: str, old_body: str) -> str:
    chosen = key in answer_keys(question)
    status = "选" if chosen else "不选"
    body = clean(old_body)
    if body:
        return f"- {key}：{status}。{body}"
    opt_text = ""
    for opt in question.get("options", []):
        if opt.get("key") == key or (question.get("type") == "tf" and strip_option_text(opt.get("text", "")).startswith(key)):
            opt_text = strip_option_text(opt.get("text", ""))
            break
    reason = default_reason(question)
    if chosen:
        return f"- {key}：选。{opt_text}与本题结论一致，{reason}"
    return f"- {key}：不选。{opt_text}与本题结论不一致，{reason}"


def build_quick(question: dict, old: dict) -> str:
    old_quick = old.get("quickExplanation", "")
    reason = line_value(old_quick, ["理由：", "原因：", "依据："]) or default_reason(question)
    trap = line_value(old_quick, ["易错：", "容易错在："]) or trap_for(question)
    qtype = question.get("type")
    lines: list[str] = []
    if qtype == "tf":
        lines.append(f"本题判断：{answer_display(question)}。")
        lines.append(f"理由：{reason}")
        lines.append("选项判断：")
        old_lines = existing_option_lines(old_quick)
        for key in ["T", "F"]:
            lines.append(option_reason(question, key, old_lines.get(key, "")))
        lines.append(f"易错：{trap}")
        return "\n".join(lines)
    if qtype in {"single", "multi"}:
        lines.append(f"本题答案：{answer_display(question)}。")
        lines.append(f"理由：{reason}")
        lines.append("选项判断：")
        old_lines = existing_option_lines(old_quick)
        for opt in question.get("options", []):
            key = opt["key"]
            lines.append(option_reason(question, key, old_lines.get(key, "")))
        lines.append(f"易错：{trap}")
        return "\n".join(lines)
    if qtype == "blank":
        answers = question.get("answer")
        if not isinstance(answers, list):
            answers = [answers]
        lines.append(f"本题填：{answer_display(question)}。")
        lines.append(f"理由：{reason}")
        lines.append("填空判断：")
        for idx, ans in enumerate(answers, 1):
            lines.append(f"- 第{idx}空：填“{ans}”。{blank_reason(question, idx, str(ans))}")
        lines.append(f"易错：{trap}")
        return "\n".join(lines)
    return clean(old_quick) or default_reason(question)


def trap_for(question: dict) -> str:
    k = question.get("knowledge", "")
    if "路由器" in k or "路由" in k:
        return "把路由器、交换机、网关和运输层端口混在一起。"
    if "IP 地址" in k or "CIDR" in k:
        return "只背地址外形，却没有判断前缀、掩码、网络号和特殊地址范围。"
    if "ARP" in k or "ICMP" in k or "IP 数据报" in k:
        return "把 ARP、ICMP、IGMP 都笼统当成 IP 转发协议。"
    if "TCP" in k:
        return "把 TCP 的可靠字节流和 UDP 的简单报文服务混淆。"
    if "UDP" in k or "端口" in k:
        return "把主机到主机的 IP 地址通信误认为运输层的进程到进程通信。"
    if "DNS" in k:
        return "把递归查询、迭代查询、本地域名服务器和根服务器职责混在一起。"
    if "FTP" in k:
        return "忘记 FTP 有控制连接和数据连接两条连接。"
    if "HTTP" in k or "Web" in k:
        return "把 Web、HTTP、HTML、URL 当成同一个概念。"
    if "电子邮件" in k:
        return "把 SMTP、POP3、IMAP 的方向和作用弄反。"
    if "密码" in k or "防火墙" in k or "VPN" in k:
        return "只记名词，不区分它解决的是保密、鉴别、完整性还是访问控制。"
    return "关键词相近时没有回到本题问的层次和对象。"


def blank_reason(question: dict, idx: int, ans: str) -> str:
    k = question.get("knowledge", "")
    if "OSPF" in question.get("stem", "") and "链路状态" in ans:
        return "OSPF 同步的是链路状态数据库，路由表是用它计算出来的结果。"
    if "VLAN" in question.get("stem", ""):
        return "不同 VLAN 互通需要三层转发能力，常见答案是路由器或三层交换机。"
    if "回环" in question.get("stem", "") or "循环测试" in question.get("stem", ""):
        return "题库口径把 127.0.0.0 作为回环地址段答案，日常主机常用 127.0.0.1。"
    if "端口" in k or "运输层" in k:
        return "运输层用端口号把数据交给正确的应用进程。"
    if "DNS" in k:
        return "DNS 的关键词要按域名层次和查询流程填写。"
    if "FTP" in k:
        return "FTP 的控制连接、数据连接和端口号是固定高频考点。"
    if "电子邮件" in k:
        return "邮件协议按发送、读取、内容扩展来区分。"
    if "防火墙" in k:
        return "这是教材中的标准分类术语，填近义词容易和标准答案不一致。"
    return "这个空考的是固定术语，填法要和题库标准答案保持一致。"


def profile(question: dict) -> tuple[str, str, str]:
    k = question.get("knowledge", "")
    if "路由器" in k or "路由" in k:
        return (
            "网络层负责把分组从源主机所在网络送到目的主机所在网络。路由器依据 IP 地址和路由表转发；RIP、OSPF、BGP 负责生成或交换路由信息。",
            "| 对象 | 工作层次/范围 | 判断依据 |\n|---|---|---|\n| 交换机 | 数据链路层 | MAC 地址、同一广播域内转发 |\n| 路由器 | 网络层 | IP 地址、路由表、跨网络转发 |\n| 网关 | 高层或协议转换 | 不同协议体系之间转换 |\n| RIP/OSPF/BGP | 路由协议 | RIP 距离向量，OSPF 链路状态，BGP AS 间路由 |",
            "如果题干把“路由器在运输层”改成“路由器在网络层”，判断会反过来；如果把 RIP 和 OSPF 的算法互换，要判错。",
        )
    if "IP 地址" in k or "CIDR" in k:
        return (
            "IP 地址题要同时看地址范围、网络前缀、主机号和特殊地址。CIDR 用 `/前缀长度` 表示网络部分，VLSM 允许不同子网使用不同长度掩码。",
            "| 项目 | 看什么 | 易错点 |\n|---|---|---|\n| A/B/C 类地址 | 首字节范围与默认掩码 | 把分类地址和 CIDR 混用 |\n| CIDR | 斜线后的前缀长度 | 主机位全 0/全 1 不能随便分配给普通主机 |\n| NAT | 私有地址到公网地址转换 | 不是路由协议 |\n| 特殊地址 | 127/8、255.255.255.255 等 | 常用地址和题库标准写法可能不同 |",
            "如果题目换成求网络地址、广播地址或可用主机数，就按掩码把网络位和主机位分开。",
        )
    if "ARP" in k or "ICMP" in k or "IP 数据报" in k:
        return (
            "网络层相关协议各有分工：IP 负责无连接分组投递，ARP 解析 MAC 地址，ICMP 报告差错和控制信息，IGMP 管理多播组成员。",
            "| 协议/字段 | 作用 | 常见考法 |\n|---|---|---|\n| IP 数据报 | 尽力而为投递 | 首部字段、TTL、分片 |\n| ARP | IP 地址解析成 MAC 地址 | 同一局域网内解析下一跳 MAC |\n| ICMP | 差错报告、控制报文 | ping、目的不可达、超时 |\n| IGMP | 多播组成员管理 | 主机加入/离开多播组 |",
            "如果题干问“地址解析”，通常看 ARP；问 ping 或差错报告，看 ICMP；问多播组，看 IGMP。",
        )
    if "TCP 连接" in k:
        return (
            "TCP 是面向连接、可靠、面向字节流的运输层协议；它通过序号、确认、重传、滑动窗口和连接管理来提供可靠服务。",
            "| 概念 | TCP | UDP |\n|---|---|---|\n| 连接 | 面向连接 | 无连接 |\n| 可靠性 | 确认、重传、序号 | 尽最大努力 |\n| 数据边界 | 字节流 | 保留应用报文边界 |\n| 常见用途 | HTTP、FTP、SMTP 等 | DNS 查询、实时音视频等 |",
            "如果题干把 TCP 说成“无连接、面向报文、不可靠”，通常就是把 UDP 特点套给 TCP。",
        )
    if "UDP" in k or "端口" in k or "运输层服务" in k:
        return (
            "运输层为应用进程提供端到端通信。端口号用于复用和分用；套接字通常由 IP 地址和端口号共同标识通信端点。",
            "| 名词 | 含义 | 本层关键词 |\n|---|---|---|\n| 端口 | 标识主机中的应用进程 | 16 bit、复用/分用 |\n| 套接字 | IP 地址 + 端口号 | 通信端点 |\n| UDP | 无连接、简单报文服务 | 首部小、开销低 |\n| TCP | 可靠字节流 | 连接、确认、窗口 |",
            "如果题目问“主机到主机”，偏网络层；问“进程到进程、端口、复用分用”，偏运输层。",
        )
    if "拥塞" in k:
        return (
            "TCP 拥塞控制根据网络拥塞程度调整拥塞窗口，典型机制包括慢开始、拥塞避免、快重传和快恢复。",
            "| 机制 | 触发/特点 | 窗口变化 |\n|---|---|---|\n| 慢开始 | 连接初期或超时后 | 指数增长到阈值附近 |\n| 拥塞避免 | 超过阈值后 | 线性增长 |\n| 快重传 | 收到重复确认 | 不等超时先重传 |\n| 快恢复 | 快重传后 | 避免回到最小窗口 |",
            "如果题干出现 cwnd、ssthresh、重复确认、超时，就要判断窗口处在哪个阶段。",
        )
    if "DNS" in k:
        return (
            "DNS 把域名解析为 IP 地址，采用层次结构和分布式数据库。查询方式常考递归查询与迭代查询。",
            "| 查询方式 | 谁继续查 | 客户端感受 |\n|---|---|---|\n| 递归查询 | 被问服务器继续代查 | 客户端等最终答案 |\n| 迭代查询 | 被问服务器返回下一步线索 | 查询方自己继续问 |\n| 本地域名服务器 | 用户侧默认查询入口 | 常替主机完成解析 |\n| 根域名服务器 | 顶层入口线索 | 不保存所有域名最终地址 |",
            "如果题目问“本地域名服务器替主机查到底”，多半是递归；问“返回下一服务器地址”，多半是迭代。",
        )
    if "FTP" in k:
        return (
            "FTP 使用控制连接传命令、数据连接传文件数据。控制连接常用 TCP 21 端口，数据连接可主动或被动建立。",
            "| 连接 | 作用 | 常见端口/特点 |\n|---|---|---|\n| 控制连接 | 传命令和响应 | TCP 21，持续存在 |\n| 数据连接 | 传文件内容或目录数据 | 每次传输建立/关闭 |\n| 主动模式 | 服务器主动连客户端数据端口 | 可能受防火墙影响 |\n| 被动模式 | 客户端主动连服务器开放端口 | 更适合穿越防火墙 |",
            "如果题干把 FTP 说成只有一条连接，或把 21 端口说成传输所有文件数据，就要警惕。",
        )
    if "HTTP" in k or "Web" in k or "URL" in k:
        return (
            "Web 应用常围绕 URL 定位资源、HTTP 请求/响应传输资源、HTML 描述页面内容。HTTP 本身是应用层协议。",
            "| 名词 | 作用 | 易错点 |\n|---|---|---|\n| URL | 定位互联网资源 | 不是传输协议 |\n| HTTP | 浏览器和 Web 服务器通信 | 应用层协议、请求/响应 |\n| HTML | 描述网页内容结构 | 不是网络协议 |\n| Cookie | 在无状态 HTTP 上保存状态线索 | 不等于连接保持 |",
            "如果题干问“定位资源”，看 URL；问“浏览器和服务器通信规则”，看 HTTP；问“网页标记语言”，看 HTML。",
        )
    if "DHCP" in k or "SNMP" in k:
        return (
            "DHCP 用于自动分配网络配置，SNMP 用于网络管理，MIB 是被管理对象的信息库。",
            "| 协议/对象 | 主要用途 | 关键词 |\n|---|---|---|\n| DHCP | 自动获取 IP、掩码、网关、DNS | 租约、自动配置 |\n| SNMP | 网络管理站管理设备 | 管理站、代理 |\n| MIB | 管理信息库 | 被管理对象集合 |",
            "如果题干说自动获取 IP 配置，看 DHCP；说管理站、代理、MIB，看 SNMP。",
        )
    if "电子邮件" in k:
        return (
            "电子邮件系统通常用 SMTP 发送邮件，用 POP3 或 IMAP 读取邮件，用 MIME 扩展邮件可携带非 ASCII 文本和附件。",
            "| 协议 | 方向/用途 | 常见关键词 |\n|---|---|---|\n| SMTP | 发送、转发邮件 | 客户端到服务器、服务器间 |\n| POP3 | 下载读取邮件 | 常把邮件取到本地 |\n| IMAP | 在线管理邮件 | 服务器端文件夹同步 |\n| MIME | 扩展邮件内容类型 | 附件、图片、非 ASCII |",
            "如果题干问“发送”，选 SMTP；问“收取”，看 POP3/IMAP；问“附件和多媒体”，看 MIME。",
        )
    if "密码" in k or "数字签名" in k:
        return (
            "网络安全题要分清保密、完整性、鉴别和不可否认。加密解决保密，摘要和 MAC 帮助完整性/鉴别，数字签名可支持不可否认。",
            "| 技术 | 解决什么 | 关键点 |\n|---|---|---|\n| 对称加密 | 保密 | 双方共享同一密钥 |\n| 公钥加密 | 保密或密钥交换 | 公钥/私钥成对 |\n| 报文摘要 | 完整性校验 | 单向散列 |\n| 数字签名 | 鉴别、完整性、不可否认 | 私钥签名、公钥验证 |",
            "如果题干问“防抵赖/不可否认”，通常指数字签名；只做加密并不自动等于签名。",
        )
    if "防火墙" in k or "VPN" in k:
        return (
            "防火墙用于访问控制和安全隔离，常见类型有分组过滤路由器和应用网关/代理服务器；VPN 通过隧道和加密在公网上形成安全逻辑专网。",
            "| 技术 | 位置/方式 | 主要作用 |\n|---|---|---|\n| 分组过滤路由器 | 网络层/传输层字段过滤 | 按 IP、端口、协议放行或拒绝 |\n| 应用网关/代理 | 应用层代理访问 | 深入检查应用协议 |\n| VPN | 隧道 + 加密 | 在公网中建立安全专用通道 |\n| 攻击防护 | 访问控制、鉴别、加密 | 降低未授权访问风险 |",
            "如果题干问防火墙分类，按教材术语写；问远程安全接入或专用通道，多半看 VPN。",
        )
    return (
        f"本题属于“{k}”知识点，关键是把题干对象、协议层次和标准术语对应起来。",
        "| 判断对象 | 应先看 | 常见错误 |\n|---|---|---|\n| 协议 | 所在层次和用途 | 只背名字不看功能 |\n| 地址/端口 | 服务对象 | 把主机通信和进程通信混淆 |\n| 标准术语 | 教材固定写法 | 用近义词替代标准答案 |",
        "如果题干换一种问法，仍要先判断它问的是层次、协议、字段、地址还是设备。"
    )


def build_detail(question: dict, old: dict) -> str:
    reason = line_value(old.get("quickExplanation", ""), ["理由：", "原因：", "依据："]) or default_reason(question)
    core, table, variant = profile(question)
    extension = extension_for(question)
    return clean(
        f"核心知识点：\n{core}\n\n{table}\n\n"
        f"题目变形：\n{variant}\n\n"
        f"知识拓展：\n{reason}\n{extension}"
    )


def extension_for(question: dict) -> str:
    k = question.get("knowledge", "")
    if "路由" in k:
        return "复习时把“谁转发、看什么字段、在哪一层”放在一起背，设备题和路由协议题就不容易串。"
    if "IP 地址" in k:
        return "做 IP 题时把地址写成二进制边界最稳，尤其是子网划分、CIDR 汇聚和可用主机数计算。"
    if "TCP" in k:
        return "TCP 的可靠性是端到端的，不是链路层逐跳可靠；确认号表示期望收到的下一个字节序号。"
    if "DNS" in k:
        return "DNS 使用缓存提高效率，所以实际查询不一定每次都走完整根、顶级、权威服务器链路。"
    if "HTTP" in k or "Web" in k:
        return "HTTP 是应用层协议，底层通常依赖 TCP；HTTPS 是在 HTTP 与 TLS 安全机制结合后的安全访问方式。"
    if "电子邮件" in k:
        return "邮件发送和读取经常不是同一个协议，题目只要出现“发送/接收/附件”，就先定位方向。"
    if "密码" in k or "防火墙" in k or "VPN" in k:
        return "安全题不要只背技术名，要把它对应到保密性、完整性、鉴别、访问控制或不可否认。"
    return "考试常把相邻层功能互换，复习时要把协议和层次一起记。"


def repair_file(path: Path, wanted_chapters: set[str]) -> list[dict]:
    questions = load_json(QUESTIONS_PATH)
    old_items = {x["label"]: x for x in load_json(path)}
    repaired = []
    for q in questions:
        if q["chapter"] not in wanted_chapters:
            continue
        old = old_items.get(q["label"], {})
        repaired.append({
            "label": q["label"],
            "quickExplanation": build_quick(q, old),
            "knowledgeDetail": build_detail(q, old),
        })
    path.write_text(json.dumps(repaired, ensure_ascii=False, indent=2), encoding="utf-8")
    return repaired


def main() -> None:
    questions = load_json(QUESTIONS_PATH)
    chapter_order = []
    for q in questions:
        if q["chapter"] not in chapter_order:
            chapter_order.append(q["chapter"])
    chapter4 = {chapter_order[3]}
    chapter57 = set(chapter_order[4:7])
    ch4 = repair_file(CH4_OUT, chapter4)
    ch57 = repair_file(CH57_OUT, chapter57)
    print(f"chapter4 repaired: {len(ch4)} -> {CH4_OUT}")
    print(f"chapter5_7 repaired: {len(ch57)} -> {CH57_OUT}")


if __name__ == "__main__":
    main()
