from __future__ import annotations

import json
import re
from collections import Counter, defaultdict
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
QUESTIONS = ROOT / "app" / "src" / "main" / "assets" / "data_structures_questions.json"
REPORT = ROOT / "explanation_work" / "data_structures_explanation_refine_report.md"


def clean_text(value: str) -> str:
    text = (value or "").replace("\r\n", "\n").replace("\r", "\n")
    text = re.sub(r"\s+", " ", text)
    return text.strip()


def option_text(q: dict, key: str) -> str:
    for option in q.get("options", []):
        if option.get("key") == key:
            return clean_text(option.get("text", ""))
    return ""


def answer_label(q: dict) -> str:
    answer = str(q.get("answer", ""))
    if q.get("type") == "tf":
        return "T（正确）" if answer == "TRUE" else "F（错误）"
    return answer


def type_title(q: dict) -> str:
    return q.get("typeName") or q.get("type") or "题目"


def build_explanation(quick: str, detail: str) -> str:
    return f"【快速做题】\n{quick.strip()}\n\n【知识点详解】\n{detail.strip()}"


def with_options(q: dict, reason: str, wrong_hint: str = "") -> str:
    if not q.get("options") or q.get("type") == "tf":
        return reason
    answer = str(q.get("answer", ""))
    lines = [reason, "", "选项辨析："]
    for option in q["options"]:
        key = option.get("key", "")
        text = clean_text(option.get("text", ""))
        if key in answer:
            lines.append(f"- {key}：{text}。正确，和上面的定义或推导一致。")
        else:
            hint = wrong_hint or "不符合本题条件，属于相近概念或干扰结论。"
            lines.append(f"- {key}：{text}。不选，{hint}")
    return "\n".join(lines)


def tf_explanation(q: dict) -> tuple[str, str]:
    stem = clean_text(q["stem"])
    is_true = q["answer"] == "TRUE"
    ans = answer_label(q)

    def finish(core: str, detail: str) -> tuple[str, str]:
        verdict = "所以本题正确。" if is_true else "所以本题错误。"
        quick = f"答案：{ans}。\n{core} {verdict}"
        return quick, detail

    if "链表中逻辑上相邻" in stem:
        return finish(
            "链表只保证结点通过指针形成前后关系，不要求这些结点在内存中连续存放。",
            "顺序表的特点是物理连续，因此可以按下标直接算地址；链表的特点是每个结点保存数据域和指针域，逻辑上的下一个元素由指针指出。逻辑相邻不等于物理相邻，这正是链表便于插入、删除的原因。",
        )
    if "链表是一种随机存取" in stem:
        return finish(
            "链表不能像数组那样用下标直接定位第 i 个结点，必须从头结点沿指针逐个走过去。",
            "随机存取要求能在 O(1) 时间按序号访问元素，顺序表可以做到，链表通常做不到。链表的优势在于已经找到位置后改指针插入或删除，不在于随机访问。",
        )
    if "频繁" in stem and "插入" in stem and "删除" in stem and ("链式" in stem or "顺序" in stem):
        prefer_link = "宜采用链式实现" in stem or "选择顺序存储结构更好" not in stem
        return finish(
            "中间频繁插入、删除时，顺序表要移动大量后续元素；链表定位后主要改指针。",
            "顺序存储适合按下标访问和尾部追加；链式存储适合插入、删除多的场景。若题干说这种场景更适合顺序存储，就是把两种存储结构的优势说反了。",
        )
    if "队列" in stem and "溢出" in stem:
        return finish(
            "顺序栈和顺序队列都借助固定数组保存元素，数组容量满时再入栈或入队都会发生上溢。",
            "顺序存储结构的空间一般预先分配，栈顶或队尾继续推进前要判断是否还有空位。链式结构通常更容易扩展，但顺序结构必须处理容量上限。",
        )
    if "单链表" in stem and "访问结点" in stem:
        return finish(
            "单链表访问第 N 个结点要从头顺着 next 走，时间是 O(N)；如果已经给定插入位置的前驱，增加结点只改指针，是 O(1)。",
            "这道题把访问和增加的复杂度写反了。链表不擅长按序号访问，但擅长在已定位位置插入或删除；顺序表则相反，按下标访问快，中间增删要搬移元素。",
        )
    if "顺序存储" in stem and "删除第一个元素" in stem:
        return finish(
            "顺序表删除第一个元素后，后面 N-1 个元素都要前移；在最后插入只需放到末尾。",
            "顺序表的代价来自搬移元素。删除首元素是 O(N)，尾部插入在容量足够时是 O(1)。题干说成 O(1) 和 O(N)，正好反了。",
        )

    if "二叉搜索树" in stem or "二叉排序树" in stem or "二叉查找树" in stem:
        if "中根遍历" in stem or "中序遍历" in stem:
            return finish(
                "二叉搜索树满足左小右大，中序遍历按“左子树-根-右子树”访问，结果会从小到大排列。",
                "BST 的中序遍历天然得到升序序列；前序先访问根，后序最后访问根，都不会保证递增或递减。判断 BST 遍历题时，只有中序和有序序列直接绑定。",
            )
        if "前序遍历" in stem or "后序遍历" in stem:
            return finish(
                "BST 的有序输出来自中序遍历，不是前序或后序遍历。",
                "前序是根-左-右，后序是左-右-根，它们保留的是树的结构访问顺序；只有中序把所有小于根的元素放在根前、大于根的元素放在根后，因此才形成递增序列。",
            )
        if "最大元素" in stem:
            return finish(
                "BST 的最大值在一路向右能走到的最后一个结点；如果根没有右子树，最大值就可能是根本身。",
                "“一定位于根的右子树”说得太绝对。最大结点的共同特征是没有右孩子，而不是一定出现在某个固定子树中。",
            )
        if "最小元素" in stem:
            return finish(
                "BST 的最小值在一路向左能走到的最后一个结点；如果根没有左子树，最小值就可能是根本身。",
                "最小结点的共同特征是没有左孩子。题目中的“有可能不位于根的左子树”成立，因为单结点树或没有左子树的 BST 中，最小值就是根。",
            )
        if "新结点总是作为树叶" in stem:
            return finish(
                "BST 插入时从根开始比较，向左或向右走到空指针位置才挂上新结点，所以新结点刚插入时一定是叶子。",
                "插入不会把新结点塞到已有结点中间，而是在查找失败的位置生成一个新叶子。以后继续插入别的结点时，它才可能不再是叶子。",
            )
        if "删除一个结点" in stem and "左右子树" in stem:
            return finish(
                "BST 删除有两个孩子的结点时，常用前驱或后继替换：左子树最大值或右子树最小值。",
                "左子树最大值仍小于被删结点右侧所有值，右子树最小值仍大于被删结点左侧所有值，用它们替换可以保持 BST 的左小右大性质。",
            )

    if "二叉树" in stem:
        if "先序遍历" in stem and "第一个" in stem:
            return finish(
                "先序遍历的顺序是根-左-右，因此第一个被访问的结点就是根结点。",
                "三种遍历的根位置不同：先序根在最前，中序根在左右子树之间，后序根在最后。题干只要问第一个或最后一个，就要先看是哪种遍历。",
            )
        if "后序遍历" in stem and "最后" in stem:
            return finish(
                "后序遍历的顺序是左-右-根，因此最后一个元素一定是根结点。",
                "后序先处理完左右子树，再访问根。这个性质常用于从后序序列中确定整棵树或子树的根。",
            )
        if "先序遍历和后序遍历顺序相反" in stem:
            return finish(
                "若每个结点至多只有一个孩子，先序会沿链从上到下访问，后序会从下到上访问，两者正好相反。",
                "一旦某个结点同时有左右孩子，先序中的左右子树顺序和后序中的左右子树结束顺序不会整体完全相反。因此这种现象对应的是没有分叉的二叉树链。",
            )
        if "完全二叉树" in stem and "没有左孩子" in stem:
            return finish(
                "完全二叉树按层从左到右填充。一个结点如果没有左孩子，就不可能还有右孩子，所以它只能是叶子。",
                "完全二叉树不允许某层右边有结点而左边空着。孩子也按左先右后的顺序出现，因此没有左孩子就说明下面没有孩子。",
            )
        if "顺序存储结构和链式存储结构" in stem:
            return finish(
                "二叉树既可以用数组按层次编号存放，也可以用左右孩子指针链接存放。",
                "顺序存储适合完全二叉树，因为下标关系清楚；普通二叉树常用链式存储，因为空孩子较多时数组会浪费空间。",
            )
        if "只能用二叉链表" in stem:
            return finish(
                "二叉链表只是二叉树的一种链式表示，二叉树还可以顺序存储、三叉链表等方式表示。",
                "数据结构的逻辑结构和存储结构不是一回事。同一棵二叉树可以选择不同存储方式，不能说只能用某一种表示。",
            )

    if "树" in stem:
        if "同一层" in stem and "兄弟" in stem:
            return finish(
                "兄弟结点要求拥有同一个双亲，只在同一层还不够。",
                "同层结点可能属于不同父结点的子树，它们只是层号相同，不一定是兄弟。兄弟关系看双亲是否相同，不看是否同层。",
            )
        if "同一双亲" in stem and "兄弟" in stem:
            return finish(
                "兄弟结点的定义就是同一个双亲结点的孩子结点。",
                "树结构中的关系以父子层次为核心：双亲、孩子、兄弟分别描述相邻层和同父结点之间的关系。",
            )
        if "子树的个数允许为0" in stem:
            return finish(
                "叶子结点没有孩子，因此以它为根的子树个数可以是 0。",
                "树中结点的度就是它拥有的子树个数。叶子结点的度为 0，所以“子树个数允许为 0”是成立的。",
            )
        if "元素个数不允许为0" in stem:
            return finish(
                "数据结构中允许讨论空树，空树的结点数就是 0。",
                "空树常作为递归定义和算法边界条件出现，比如插入第一个结点前的 BST 就是空树。因此说树的元素个数不允许为 0 是错的。",
            )
        if "每个结点最多只有一个直接前驱" in stem:
            return finish(
                "除根结点没有直接前驱外，其余结点都只有一个双亲，所以最多只有一个直接前驱。",
                "树不是图中的任意连接结构。一个结点如果有多个直接前驱，就会破坏树的层次父子关系，变成更一般的图结构。",
            )

    if "Python" in stem or "类" in stem or "__init__" in stem or "self" in stem or "列表" in stem or "元组" in stem:
        if "缩进" in stem:
            return finish(
                "Python 用缩进确定代码块，方法定义必须缩进到类体内部，不能和 class 行平齐。",
                "类定义第一行是 class，类中的属性和方法属于类体。方法首行若不缩进，就会被解释为类外函数，而不是这个类的成员方法。",
            )
        if "构造方法" in stem or "__init__" in stem or "_ _init_ _" in stem:
            return finish(
                "Python 创建对象时会自动调用 `__init__`，它负责给新对象初始化属性。",
                "`__init__` 不是普通随便命名的方法；双下划线名字固定。调用类名创建对象时，解释器先创建实例，再把实例作为 self 传给 `__init__` 完成初始化。",
            )
        if "self" in stem:
            if "必须是self" in stem:
                return finish(
                    "`self` 只是约定俗成的形参名，不是 Python 关键字；第一个参数可以改名，但不建议改。",
                    "实例方法的第一个形参接收当前对象，调用 `obj.method()` 时解释器会自动传入 obj。考试中要分清两件事：第一个参数的位置是必须的，名字写成 self 是强约定但不是语法硬规定。",
                )
            return finish(
                "实例方法第一个形参通常写 `self`，调用方法时不需要手动传这个参数，解释器会自动绑定当前对象。",
                "`self` 让方法能访问当前对象的属性和其他方法。定义时要写，调用时不写，这是 Python 面向对象最容易混淆的一点。",
            )
        if "私有属性" in stem or "两个下划线" in stem:
            return finish(
                "Python 类中以两个下划线开头的属性会触发名称改写，通常用来表示私有属性，并通过 `self.__name` 这类形式访问。",
                "双下划线不是绝对安全机制，但能避免外部直接用原名访问，也能降低子类属性重名冲突。题目考的是命名规则：开头两个下划线。",
            )
        if "继承" in stem or "子类" in stem or "父类" in stem:
            return finish(
                "继承的意义是让子类复用父类已有属性和方法，同时子类还可以继续增加自己的属性和方法。",
                "继承不是简单复制代码，也不是限制子类扩展。子类先拥有父类能力，再根据需要覆盖或新增成员，所以说子类不能添加自己的内容是错的。",
            )
        if "列表中所有元素必须" in stem:
            return finish(
                "Python 列表可以同时保存不同类型的对象，不要求所有元素类型一致。",
                "列表保存的是对象引用，元素可以是整数、字符串、列表、对象等任意类型。很多静态语言数组要求同类型，但 Python list 没有这个限制。",
            )
        if "元组" in stem and ("不可变" in stem or "修改" in stem):
            return finish(
                "元组 tuple 是不可变序列，创建后不能通过下标修改其中的元素。",
                "列表 list 可变，支持追加、删除、按下标改值；元组 tuple 主要用于固定数据组合。把列表的可变操作套到元组上会报错。",
            )
        if "列表、元组、字符串" in stem and "有序序列" in stem:
            return finish(
                "列表、元组、字符串都保留元素先后顺序，能按下标访问，所以属于有序序列。",
                "有序不是指数值排好大小，而是元素有稳定位置。`s[0]`、`t[1]`、`lst[2]` 这类下标访问依赖的就是有序性。",
            )
        if "方法本质" in stem:
            return finish(
                "方法本质上是定义在类中的函数，只是调用时会自动绑定对象或类作为第一个参数。",
                "普通函数和方法都封装一段可执行逻辑；方法多了所属对象这个上下文，因此可以通过 self 读写对象状态。",
            )

    if "时间复杂度" in stem or "算法" in stem or "查找" in stem or "排序" in stem or "复杂度" in stem:
        if "一定比" in stem:
            return finish(
                "大 O 描述的是规模 n 趋向很大时的增长趋势，不保证任意实际输入下谁一定更快。",
                "O(nlogn) 的增长阶低于 O(n^3)，大规模时通常更有优势；但实际运行还受常数、实现、硬件、输入规模影响，所以“实际执行时一定更快”过于绝对。",
            )
        if "二分查找" in stem or "二分法查找" in stem:
            if "无序" in stem:
                return finish(
                    "二分查找每次用中间元素排除一半范围，前提是元素已经有序；无序表不能判断该去左半边还是右半边。",
                    "二分查找适合有序顺序表。若数据无序，只能先排序再二分，或者改用顺序查找、散列查找等方法。",
                )
            if "有序" in stem:
                return finish(
                    "二分查找的前提就是有序表，顺序存储又能 O(1) 取中间元素，因此可以使用二分查找。",
                    "每轮比较中间元素后排除一半搜索区间，所以时间复杂度是 O(log n)。没有有序性就不能排除一半。",
                )
        if "时间复杂度越高" in stem:
            return finish(
                "时间复杂度越高，增长越快，通常表示算法效率越低。",
                "复杂度衡量输入规模变大时代价如何增长。O(n^2) 一般比 O(n) 增长快，O(n^3) 又比 O(n^2) 更慢，不能把“高复杂度”理解成“高效率”。",
            )
        if "时间复杂度和空间复杂度" in stem:
            return finish(
                "算法分析主要看运行时间和额外空间两类资源消耗。",
                "时间复杂度回答算法随输入规模变大要执行多少步，空间复杂度回答要占用多少存储。两者共同决定算法是否适合实际问题。",
            )
        if "平均情况复杂度" in stem:
            return finish(
                "平均情况复杂度描述所有可能输入按概率加权后的平均运行效率。",
                "它不同于最好情况和最坏情况。最好情况只看最顺利输入，最坏情况看代价最大输入，平均情况更接近整体输入分布下的预期表现。",
            )
        if "最好情况复杂度" in stem:
            return finish(
                "最好情况复杂度表示在最理想输入下算法能达到的最高效率。",
                "例如顺序查找第一个元素，比较一次就成功，这是最好情况；但分析算法不能只看最好情况，还要关注平均和最坏情况。",
            )
        if "快速排序" in stem:
            return finish(
                "普通快速排序若枢轴选择不当，输入已经有序或逆序时可能每次只分出一个元素，反而接近最坏情况。",
                "快排最省时间通常来自划分比较均衡，每层处理 n 个元素、层数约 log n。若划分极不均衡，递归深度变成 n，时间会退化到 O(n^2)。",
            )
        if "n!" in stem:
            return finish(
                "`n!` 是 1×2×...×n，而 `n^n` 是 n 连乘 n 次；每一项都不超过 n，所以 `n!` 的增长阶低于 `n^n`。",
                "可以把两者逐项比较：`n! = 1×2×...×n`，`n^n = n×n×...×n`。除最后一项外，前面每项都比 n 小，因此 n! 明显更低阶。",
            )

    return finish(
        f"本题判断的是“{stem}”这句话是否符合定义。",
        f"这题要把概念本身说完整：{q.get('knowledge', q.get('chapter', '相关知识点'))}。结论为 {ans}，不是因为文字像不像教材表述，而是因为题干中的条件和定义是否完全一致。",
    )


def single_explanation(q: dict) -> tuple[str, str]:
    stem = clean_text(q["stem"])
    answer = str(q["answer"])
    correct = option_text(q, answer)
    ans = f"{answer}（{correct}）" if correct else answer

    def finish(reason: str, detail: str, wrong_hint: str = "") -> tuple[str, str]:
        quick = with_options(q, f"答案：{ans}。\n{reason}", wrong_hint)
        return quick, detail

    if "append" in stem:
        detail = "Python 列表的 `append(x)` 会把 x 作为一个整体追加到列表末尾，并且原地修改列表。若 x 是普通元素，就多一个普通元素；若 x 本身是列表，就把这个列表作为嵌套元素追加进去。`append` 方法的返回值是 None，但题目问的是列表执行后的值。"
        if "[" in correct and "[[" in correct or "['cc', 'dd']" in correct or "['c','d']" in correct:
            reason = "append 不会把参数列表拆开合并，而是把整个参数作为一个新元素放到末尾。"
        else:
            reason = "append('c') 把字符串 'c' 作为新元素追加到原列表末尾。"
        return finish(reason, detail, "要么把 append 的返回值 None 当成列表，要么误以为 append 会自动展开参数。")
    if "pop" in stem:
        return finish(
            "`pop()` 默认删除并返回列表最后一个元素；连续执行两次，先得到 7，再得到 6。",
            "对列表 `[1,2,3,4,5,6,7]` 执行 `pop()`，第一次弹出末尾 7，列表变成 `[1,2,3,4,5,6]`；第二次再弹出末尾 6。所以 a=7，b=6。`remove` 是按值删除，`del` 是语句，`cut` 不是 Python 列表方法。",
            "没有体现“默认删除末尾元素”这一规则。",
        )
    if "删除列表中最后一个元素" in stem:
        return finish(
            "`pop` 是列表方法，默认删除并返回最后一个元素。",
            "`lst.pop()` 不传下标时等价于弹出最后一个元素；`remove(x)` 是删除第一个值等于 x 的元素；`del lst[i]` 是删除指定下标元素的语句；`cut` 不是标准列表方法。",
            "它不是“默认删除最后一个元素”的列表方法。",
        )
    if "加到列表" in stem or "末尾" in stem and "lst" in stem:
        return finish(
            "给列表末尾追加单个元素使用 `lst.append(5)`。",
            "`append` 是 Python list 的标准追加方法，作用是原地把参数放到末尾。`add` 更像集合 set 的方法，`addEnd`、`addLast` 不是 Python list 的内置方法。",
            "不是 Python 列表追加元素的标准写法。",
        )
    if "下标范围" in stem:
        return finish(
            "Python 序列下标从 0 开始，10 个元素的合法下标是 0 到 9。",
            "长度为 n 的 Python 列表、字符串等序列，下标范围是 `0..n-1`。因此 data 有 10 个元素时，最后一个下标不是 10，而是 9。",
            "混淆了元素个数和最大下标，或者用了从 1 开始编号。",
        )
    if "不属于有序序列" in stem:
        return finish(
            "集合 set 不保证元素按插入顺序或下标顺序访问，因此不属于有序序列。",
            "列表、元组、字符串都有稳定的元素次序，可以按下标访问；集合强调成员是否存在，不强调第几个元素，通常不能用下标取元素。",
            "列表、元组、字符串都是有序序列，只有集合不按位置组织元素。",
        )

    if "队列" in stem:
        if "先进先出" in stem or "一种" in stem:
            return finish(
                "队列的核心规则是 FIFO：先进入队列的元素先被删除。",
                "队列只允许在队尾入队，在队头出队。它和栈相反：栈是后进先出，队列是先进先出。题目问队列特性时，不要被“只能插入/只能删除”这种片面说法干扰。",
                "不符合队列“队尾入、队头出、先进先出”的定义。",
            )
        if "不是队列基本运算" in stem:
            return finish(
                "队列的基本运算包括入队、出队、取队头、判空；不能删除任意第 i 个元素。",
                "队列是受限线性表，删除只能发生在队头。如果能删除第 i 个元素，就破坏了先进先出的限制，变成普通线性表操作。",
                "属于队列允许的基本操作。",
            )
    if "栈" in stem and "一端" in stem:
        return finish(
            "栈只允许在同一端进行插入和删除，这一端叫栈顶。",
            "栈的限制是“同端进出”，因此形成后进先出 LIFO。队列是队尾进、队头出；循环栈、循环队列是具体实现形式，不是这条定义的名称。",
            "没有体现“同一端插入和删除”的栈定义。",
        )
    if "线性结构" in stem or "非线性" in stem:
        if "非线性" in stem:
            return finish(
                "树是分支层次结构，一个结点可以有多个孩子，所以是非线性结构。",
                "线性结构中的元素前后排成一条线，除首尾外每个元素通常只有一个直接前驱和一个直接后继；栈、队列、字符串都属于线性结构，树和图属于非线性结构。",
                "它们的逻辑关系仍是一对一的线性次序。",
            )
        return finish(
            "队列中的元素按先后次序排成一条线，属于线性结构。",
            "线性结构强调元素之间主要是一对一的前后关系。队列、栈、线性表、字符串都是线性结构；树是层次分支，图是多对多关系，集合不强调线性前后关系。",
            "不是典型的一对一线性前后关系。",
        )
    if "链表存储结构和顺序存储结构相比" in stem:
        return finish(
            "链表的优势是插入、删除方便，因为定位后改指针即可，不需要搬移一大片元素。",
            "顺序表支持随机存取且存储密度高，但中间插入删除要移动元素；链表每个结点额外保存指针，不能随机存取，却适合频繁增删。",
            "要么是顺序表的优势，要么不是链表相对顺序表的主要优点。",
        )
    if "线性表在" in stem and "链式存储" in stem:
        return finish(
            "线性表需要经常插入或删除元素时更适合链式存储。",
            "链式存储的结点不要求连续，插入、删除时主要调整前驱和后继指针；顺序存储适合随机访问和元素位置稳定的场景。",
            "不是选择链式存储的关键原因。",
        )
    if "链接存储" in stem and "逻辑关系" in stem:
        return finish(
            "链式存储用指针保存结点之间的前后关系。",
            "链表中结点的物理地址可以分散，真正把它们连成逻辑序列的是指针域。存储位置连续性属于顺序存储，不属于链式存储。",
            "不能直接表示链式结点之间的连接关系。",
        )
    if "顺序存储结构和链式存储结构" in stem and "Ⅰ" in stem:
        return finish(
            "Ⅱ 和 Ⅳ 正确：链式结构能灵活表示多种逻辑关系；两种结构都可以顺序访问。",
            "Ⅰ错在没有绝对优劣，选择取决于访问和增删需求。Ⅲ错在频繁插入删除时链式结构更合适。Ⅳ正确，因为顺序表可按下标顺序访问，链表也可沿指针顺序访问。",
            "包含了“顺序结构绝对更优”或“频繁增删顺序结构更优”这类错误判断。",
        )

    if "深度为" in stem and "二叉树" in stem:
        m = re.search(r"深度为(\d+)", stem)
        depth = int(m.group(1)) if m else None
        max_nodes = 2 ** depth - 1 if depth else None
        return finish(
            f"深度为 {depth} 的二叉树最多每层都满，结点总数为 2^{depth}-1={max_nodes}。" if depth else "二叉树最大结点数按满二叉树计算。",
            "若根为第 1 层，深度为 h 的二叉树最多有 `1+2+4+...+2^(h-1)=2^h-1` 个结点。题目问“至多/最多”，就按每层都放满计算。",
            "不是深度为 h 的二叉树最大结点数公式。",
        )
    if "满二叉树" in stem and "127" in stem:
        return finish(
            "满二叉树总节点数 127=2^7-1，叶子数是最后一层结点数 2^6=64。",
            "满二叉树每一层都满。若总节点数为 2^h-1，则叶子结点数为 2^(h-1)，也等于 (n+1)/2，所以 (127+1)/2=64。",
            "不符合满二叉树叶子数公式。",
        )
    if "完全二叉树" in stem and "高度为8" in stem:
        return finish(
            "高度为 8 的完全二叉树至少前 7 层全满，第 8 层至少有 1 个结点；此时叶子最少为第 7 层除去有孩子的那个结点后的 63 个，加第 8 层 1 个，共 64。",
            "完全二叉树高度固定时，要让叶子最少，就让最后一层尽量少，只放最左边一个结点。第 7 层原本有 64 个结点，其中 1 个有孩子，剩下 63 个仍是叶子，再加最后一层那个孩子，叶子数为 64。",
            "没有按完全二叉树最后一层从左到右填充来计算。",
        )
    if "树最适合" in stem:
        return finish(
            "树最适合表示分支层次关系，例如目录、组织结构、家族谱系。",
            "树的核心是根、孩子、双亲、子树这些层次关系。若只是有序或无序数据，用线性表或集合即可；若元素之间无联系，也不需要树结构。",
            "没有体现树的分支层次结构。",
        )
    if "遍历序列" in stem or "中序遍历图示" in stem:
        if "先序" in stem:
            rule = "先序遍历按根-左-右访问，所以序列第一个一定是根，再依次进入左子树和右子树。"
        elif "后序" in stem:
            rule = "后序遍历按左-右-根访问，所以序列最后一个一定是根，左右子树都访问完才访问根。"
        else:
            rule = "中序遍历按左-根-右访问，先把左子树走完，再访问根，最后访问右子树。"
        return finish(
            rule + f"按图中结点顺序推下来就是 {correct}。",
            "二叉树遍历题不能按字母大小猜答案，要按访问根的时机走：先序根在前，中序根在中间，后序根在后。每遇到一棵子树，都递归使用同一条规则。",
            "访问根结点的位置不符合本题所问遍历方式。",
        )
    if "先序遍历与中序遍历结果相同" in stem:
        return finish(
            "先序是根-左-右，中序是左-根-右；两者相同意味着每个根前面都不能有左子树。",
            "只要某个结点有左子树，中序就会先输出左子树，再输出该结点；先序却先输出该结点，二者立刻不同。因此所有结点都无左子树时，先序和中序才可能一致。",
            "仍会让某些结点在先序和中序中的位置不同。",
        )

    if "二叉搜索树" in stem or "二叉排序树" in stem or "二叉查找树" in stem:
        if "中序遍历" in stem and ("序列" in stem or "排序" in stem):
            return finish(
                "二叉搜索树的中序遍历一定得到从小到大的序列。",
                "BST 满足左子树值小于根、右子树值大于等于根；中序遍历正好先输出左边小值，再输出根，再输出右边大值。因此选项中只有严格升序序列可能正确。",
                "不是从小到大的中序结果，或访问顺序不是中序。",
            )
        if "左子树中结点数目" in stem:
            nums = re.findall(r"\d+", stem)
            root = nums[0] if nums else "根"
            return finish(
                f"按输入顺序建 BST，第一个插入的 {root} 是根；之后比根小的元素进入左子树，比根大的进入右子树。",
                "建立二叉排序树时，插入顺序决定形状。判断左子树结点数，先看根，再把后续每个数与根比较；小于根的都会落在根的左侧子树中，再在左子树内部继续比较定位。",
                "没有按第一个元素为根、后续元素逐个比较插入来统计。",
            )
        if "左子树上所有结点" in stem:
            return finish(
                "二叉排序树定义要求左子树所有结点关键字都小于根结点。",
                "本题采用的定义是左小右大：左子树 < 根，右子树 >= 根。注意不是只看左孩子，而是左子树中的所有结点都要满足这个关系。",
                "不符合左子树关键字小于根的定义。",
            )
        if "二叉排序树是指" in stem:
            return finish(
                "二叉排序树要求每个结点都满足：左子树所有关键字小于该结点，右子树所有关键字大于或等于该结点。",
                "BST 的定义是递归的，根满足，左右子树也必须各自是二叉排序树。它不是平衡树，高度差不一定为 1 或不超过 1。",
                "把 BST 和平衡二叉树混淆，或者只说“有序”但没有给出左右子树和根的大小关系。",
            )
        if "最大值" in stem:
            return finish(
                "BST 最大值一定在一路向右走到头的结点，因此它不可能再有右孩子。",
                "若最大结点还有右孩子，那么右孩子的值会比它更大，矛盾。它可以有左孩子，因为左孩子仍然小于它。",
                "最大结点只保证右指针为空，不保证左指针也为空。",
            )
        if "输入顺序建立" in stem or "形状取决于" in stem:
            return finish(
                "同一组关键字以不同输入次序插入 BST，会得到不同形状。",
                "BST 插入是逐个进行的，第一个元素成为根，后续元素沿比较路径落到不同空位置。因此形状由输入次序决定，不由计算机软硬件或存储结构决定。",
                "不是决定 BST 插入形状的直接因素。",
            )
        if "查找结点的平均时间复杂度" in stem:
            return finish(
                "普通 BST 的平均查找复杂度与树高有关，平均情况下约为 O(log n)。",
                "查找时每比较一次就向左或向右走一层；若树较均衡，高度约 log n。若退化成链表，最坏可到 O(n)，但题目问平均情况，一般选 O(log n)。",
                "没有反映 BST 平均按树高查找的特点。",
            )
        if "插入元素46" in stem or "删除结点49" in stem:
            return finish(
                "插入 46 时按 BST 比较路径向下走，最后会落在 45 的右侧，所以双亲是 45；删除 49 后可用后继 47 接到相应位置，使 61 的左孩子为 47。",
                "BST 插入靠逐次比较定位父结点；删除有两个孩子的结点时，用左子树最大值或右子树最小值替换，才能保持中序序列仍然有序。",
                "不符合 46 的插入路径，或删除后没有保持 BST 的有序关系。",
            )
        if "删除结点19" in stem:
            return finish(
                "按题设用 17 接到 31 的左侧后，原来 17 子树中的 15 仍在 17 的左侧，结合图中连接关系可得 15 的双亲为 11。",
                "BST 删除题要同时保持左右大小关系和原有子树连接。用前驱/后继替换时，被移动结点原来的孩子还要重新挂回合适位置，不能只看被删结点一个点。",
                "没有正确追踪删除替换后 15 所在子树的父子关系。",
            )
        if "不同的二叉排序树" in stem or "结果不同" in stem or "与众不同" in stem:
            return finish(
                "判断插入序列是否得到同一棵 BST，要从第一个元素作根开始，逐个插入比较左右路径；有一个元素落点不同，树形就不同。",
                "BST 的形状不是由集合本身唯一决定，而是由插入顺序决定。做这类题时，先确定根，再分别比较左子树、右子树内部的插入先后，找出和其他选项分叉位置不同的一项。",
                "按插入路径得到的左右子树结构与多数选项一致，不是本题的不同项。",
            )
        if "有可能是一棵二叉搜索树的中序遍历序列" in stem:
            return finish(
                "BST 的中序遍历必须是从小到大的序列，选项中只有该序列满足单调递增。",
                "中序遍历 BST 时，所有较小值先于根输出，所有较大值后于根输出，所以整体应递增。若序列中出现先增后降，就不可能是 BST 的中序结果。",
                "不是整体递增序列，不能作为 BST 的中序遍历结果。",
            )

    if "时间复杂度" in stem or "算法" in stem:
        if "while" in stem and ("(y+1)" in stem or "≥(y+1)" in stem):
            return finish(
                "循环条件相当于 `(y+1)^2 <= n`，y 从 0 增到约 √n，因此循环次数是 Θ(√n)。",
                "每次循环 y 增 1，直到 y^2 接近 n。能执行的最大 y 大约是 √n，所以运行次数不是 n，也不是 log n，而是平方根级别。",
                "没有根据循环终止条件推出 y 只增长到 √n。",
            )
        if "for ( i=0; i<n" in stem and "j<m" in stem:
            return finish(
                "外层循环 n 次，内层循环 m 次，总执行次数是 n×m，所以复杂度是 O(mn)。",
                "嵌套循环的总次数通常是外层次数乘以内层次数。这里每个 i 都完整跑 m 次 j，因此不是 n^2 或 m^2，除非题目说明 m 与 n 相同。",
                "没有同时计算 n 和 m 两个循环规模。",
            )
        if "for( j=0; j<n" in stem or "s+=B" in stem:
            return finish(
                "双重循环都跑 n 次，核心语句执行 n×n 次，所以复杂度是 O(n^2)。",
                "忽略常数和低阶项，只保留增长最快的主项。两层 n 规模循环相乘就是平方级。",
                "没有体现两层 n 次循环相乘。",
            )
        if "return n * (n + 1) / 2" in stem:
            return finish(
                "函数只做固定次数的算术运算，没有循环或递归，时间复杂度是 O(1)。",
                "复杂度看执行步数如何随 n 增长。公式里出现 n 不代表循环 n 次；这段代码无论 n 多大，都只是乘法、加法、除法和返回。",
                "把公式中的 n 误当成执行次数。",
            )
        if "i=i*3" in stem or "i=i * 3" in stem:
            return finish(
                "i 每轮乘 3，从 1 增到超过 n 需要约 log3 n 轮。",
                "乘法增长对应对数复杂度：第 k 轮后 i=3^k，令 3^k > n，得到 k > log3 n。底数不同只差常数，但选项给出 log3 n 时应选它。",
                "把指数式增长误看成线性增长或常数。",
            )
        if "j<=n-i" in stem:
            return finish(
                "内层次数依次为 n-1、n-2、...、1，总和是 n(n-1)/2，所以复杂度是 O(n^2)。",
                "这种三角形循环虽然不是每次都跑 n 次，但总次数仍然是平方级。大 O 忽略 1/2 和低阶项，保留 n^2。",
                "没有把递减内层循环求和。",
            )
        if "x=90" in stem and "y=100" in stem:
            return finish(
                "x、y 初值都是固定常数，循环次数最多也是固定数量，不随 N 增长，所以是 O(1)。",
                "复杂度只看输入规模增长时代价是否增长。这里没有使用可变规模 n，循环再怎么走也只围绕 90、100 这些常数变化。",
                "把固定常数循环误判为随 N 增长。",
            )
        if "算法分析的目的" in stem:
            return finish(
                "算法分析的目的就是评价效率，找出时间或空间代价大的地方以便改进。",
                "算法正确只是前提；在正确基础上才比较哪个算法更快、更省空间。易懂性和文档性属于工程质量，不是复杂度分析的主要目的。",
                "不是算法效率分析要回答的核心问题。",
            )
        if "算法分析的前提" in stem:
            return finish(
                "分析算法效率之前，算法必须先是正确的；错误算法再快也没有意义。",
                "复杂度分析默认算法能解决问题，然后比较资源消耗。如果输出关系都不正确，谈时间复杂度和空间复杂度就失去基础。",
                "不是进行效率分析的首要前提。",
            )

    return finish(
        f"正确项是 {correct}，它直接符合“{q.get('knowledge', q.get('chapter', '本题知识点'))}”的定义或推导。",
        f"本题属于 {q.get('chapter', '数据结构')}。要真正判断选项，需要把概念落到题目条件上：先确定题目问的是定义、操作规则、遍历顺序还是复杂度，再检查选项是否满足这个规则。",
    )


CODE_NOTES = {
    "DSP-R6-1": (
        "实现 `Stock` 类时，四个属性要在构造方法里保存到对象自身；价格变化百分比按 `(当前价-昨日价)/昨日价` 计算。",
        "这题考类的封装和 getter/setter。`__init__` 接收代码、名称、昨日价格、今日价格，并用 `self.__code` 等私有属性保存。`getCode()`、`getName()` 返回固定信息；昨日价和今日价既要能读取又要能修改，所以要写 get/set 方法。`getChangePercent()` 返回小数比例，不是百分号字符串，裁判程序会再乘以 100 格式化输出。",
    ),
    "DSP-R6-2": (
        "实现 `Pet` 类时，姓名和年龄作为私有属性保存，set 方法负责修改，get 方法负责返回。",
        "裁判程序会创建 `Pet(name, age)`，再调用 `setName`、`setAge`、`getName`、`getAge`。因此类名、方法名、参数个数都必须和接口一致。私有属性建议写成 `self.__name`、`self.__age`，这样对象状态保存在实例里，不会和局部变量混淆。",
    ),
    "DSP-R6-3": (
        "`mult3(lst)` 接收一个整数列表，遍历每个元素，只把能被 3 整除的数逐行打印。",
        "`x % 3 == 0` 表示 x 除以 3 余数为 0，也就是 3 的倍数。题目要求“输出列表中是 3 的倍数的那些数值”，不是返回新列表，所以函数体里直接 `print(x)`。裁判负责读入并调用函数，答案里不要再写输入逻辑。",
    ),
    "DSP-R6-4": (
        "Fibonacci 的递归边界是 f(1)=1、f(2)=1；n>=3 时返回前两项之和。",
        "递归函数必须先写终止条件，否则会无限递归。这里 `n == 1 or n == 2` 直接返回 1；其余情况按定义 `f(n)=f(n-1)+f(n-2)`。题目保证范围合适，所以朴素递归能通过。",
    ),
    "DSP-R7-1": (
        "输入分两行给出两个整数，所以要调用两次 `int(input())`，最后只输出它们的和。",
        "样例中第一行是 18，第二行是 -48，和为 -30。输出格式要求“一行中输出和值”，不能额外输出说明文字。`input()` 读到的是字符串，参与加法前必须转成 int，否则会变成字符串拼接。",
    ),
    "DSP-R7-2": (
        "要求计算 11 到 m 的连续整数和，可以用 `sum(range(11, m+1))`，输出必须写成 `sum = S`。",
        "`range(11, m+1)` 包含 11、12、...、m，因为 range 右端不包含，所以要写 m+1。格式中的等号两边有空格，少空格也可能判错。",
    ),
    "DSP-R7-3": (
        "同一行输入 a、b、c，用 `split()` 拆成三个整数，按公式输出 `b*b-4*a*c`。",
        "题目不是求方程根，只是计算判别式表达式。样例 3、4、5 代入得到 4*4-4*3*5=16-60=-44。输出只要这个整数值。",
    ),
    "DSP-R7-4": (
        "第 i 项的分母是第 i 个奇数 `2*i-1`，累加前 N 项后保留 6 位小数。",
        "序列是 1、1/3、1/5、...，分母每次加 2。用 `range(1, n+1)` 让 i 从 1 到 n，项为 `1/(2*i-1)`。输出格式必须是 `sum = %.6f` 这种 6 位小数。",
    ),
    "DSP-R7-5": (
        "阶乘递归的边界是 0! = 1、1! = 1；负数没有阶乘，按题意输出提示。",
        "`fact(n)` 对 n>1 返回 `n*fact(n-1)`，直到 n 变成 1 或 0 停止。主程序先读入 n，若 n<0 输出 `Invalid input`；否则输出 `n! = 结果`，格式要和题目一致。",
    ),
    "DSP-R7-6": (
        "分段函数在 x=0 时结果为 0，否则结果为 1/x，输入和结果都保留一位小数。",
        "这题容易错在 x=0 时直接算 `1/x` 导致除零错误。先判断 x 是否等于 0，再计算。格式 `f({x:.1f}) = {result:.1f}` 保证 10 输出成 10.0，0 输出成 0.0。",
    ),
    "DSP-R7-7": (
        "电费分三种情况：负数无效，0 到 50 度按 0.53，超过 50 度时超出部分按 0.58。",
        "阶梯计费不是全部电量都按 0.58。超过 50 时费用为 `50*0.53 + (x-50)*0.58`。输出保留两位小数；负输入直接输出 `Invalid Value!`。",
    ),
    "DSP-R7-8": (
        "n 为 0 或 1 时使用单数 `boy`，n 大于 1 时使用复数 `boys`。",
        "题目考条件分支和固定英文格式。输出中有句点，格式是 `{n} indian boy(s).`。0 在题目中特别规定也用 `boy`，不能按英语习惯擅自改成复数。",
    ),
    "DSP-R7-9": (
        "根据 x、y 的正负判断象限；只要有一个坐标为 0，就输出“不属于任何象限”。",
        "第一象限 x>0,y>0；第二象限 x<0,y>0；第三象限 x<0,y<0；第四象限 x>0,y<0。输入形式若是 `3,4`，用 `eval(input())` 可直接得到两个数。",
    ),
    "DSP-R7-10": (
        "水费按 15 吨分段：x<=15 时 `4*x/3`，超过 15 时 `2.5*x-17.5`。",
        "第二段公式已经把前 15 吨和超出部分合并化简好了，直接套用即可。输出要求两位小数，所以用 `print(f\"{y:.2f}\")`。",
    ),
    "DSP-R7-11": (
        "回文判断可以把字符串和反转字符串 `s[::-1]` 比较，相同输出 Yes，否则输出 No。",
        "切片 `[::-1]` 表示从后往前取完整字符串。题目还要求第一行原样输出输入字符串，第二行才输出判断结果。空格和符号也是字符串的一部分，不能删除。",
    ),
    "DSP-R7-12": (
        "变位词只要求字符组成相同、顺序可以不同，把两个字符串排序后比较即可。",
        "`sorted(s)` 会得到字符按顺序排列后的列表。若两个排序结果相等，说明每个字符出现次数都相同，因此是变位词。大小写不同的字符按题目原样处理，不要自动转小写。",
    ),
    "DSP-R7-13": (
        "括号匹配用栈：左括号入栈，右括号必须和栈顶左括号配成同类一对。",
        "遇到 `(`、`[`、`{` 就压栈；遇到右括号时，如果栈空或栈顶不是对应左括号，就立即不合法。扫描结束后栈也必须为空，否则还有左括号没闭合。",
    ),
    "DSP-R7-14": (
        "本题和括号匹配相同，但要先去掉输入中的空格，再用栈检查闭合顺序。",
        "空格不是括号，题干说明 Alan 可能多输入空格，所以先 `replace(' ', '')`。之后仍按左括号入栈、右括号匹配栈顶、结束栈空的规则判断。",
    ),
    "DSP-R7-15": (
        "要求利用栈判断三类括号是否正确配对，最后按题目格式输出 yes 或 no。",
        "栈能保存尚未匹配的左括号。新的右括号必须匹配最近的那个左括号，这正是“后进先出”。任何类型不一致、右括号过早出现、最后栈不空，都是不匹配。",
    ),
}


def code_explanation(q: dict) -> tuple[str, str]:
    answer = str(q.get("answer", "")).strip()
    quick, detail = CODE_NOTES.get(q["label"], (
        "这道程序题要按题目输入、处理、输出三步写代码，并严格匹配输出格式。",
        "程序题的关键不是把样例抄一遍，而是把题目条件转成变量和分支/循环。读入类型、边界情况、保留小数和固定文字都可能影响判分。",
    ))
    quick = f"参考答案：\n```python\n{answer}\n```\n\n{quick}"
    return quick, detail


def concept_extension(q: dict) -> str:
    stem = clean_text(q.get("stem", ""))
    knowledge = clean_text(q.get("knowledge", ""))
    chapter = clean_text(q.get("chapter", ""))
    text = stem + " " + knowledge + " " + chapter
    if "链表" in text or "顺序" in text or "队列" in text or "栈" in text:
        return "补充概念：线性结构的难点在于区分“逻辑关系”和“存储方式”。顺序表靠连续地址支持快速下标访问；链表靠指针维持前后关系，适合增删；栈限制同端进出，队列限制队尾进、队头出。"
    if "二叉搜索树" in text or "二叉排序树" in text or "BST" in text:
        return "补充概念：二叉搜索树的核心不是树长什么样，而是每个结点都满足左子树小于根、右子树大于或等于根。中序遍历会把这种大小关系摊平成升序序列。"
    if "二叉树" in text or "树" in text:
        return "补充概念：树表示层次关系，普通二叉树每个结点最多两个孩子；先序、中序、后序的区别只在访问根的位置，分别是根在前、根在中间、根在最后。"
    if "复杂度" in text or "查找" in text or "排序" in text or "算法" in text:
        return "补充概念：复杂度看的是输入规模变大时步骤数如何增长，只保留主导增长项。循环次数相乘、递增求和、乘法增长取对数，是这类题最常用的推导来源。"
    if "Python" in text or "列表" in text or "元组" in text or "类" in text or "self" in text:
        if "缩进" in text or "类定义" in text:
            return "补充概念：Python 类体由缩进划定。缩进在 class 下面的函数才是成员方法；如果和 class 平齐，就只是类外普通函数。"
        if "__init__" in text or "构造" in text:
            return "补充概念：`__init__` 在对象创建后自动执行，用来初始化当前实例的属性；它的第一个参数接收当前对象，通常写作 self。"
        if "self" in text or "方法" in text:
            return "补充概念：实例方法定义时要给当前对象留出第一个形参，通常写 self；调用 `对象.方法()` 时，这个实参由解释器自动传入。"
        if "append" in text or "列表" in text:
            return "补充概念：列表是可变序列，`append(x)` 会原地把 x 作为一个整体追加到末尾；如果 x 是列表，就会形成嵌套列表。"
        if "元组" in text:
            return "补充概念：元组和字符串属于不可变序列，元素位置确定但不能按下标改值；列表是可变序列，可以追加、删除和修改元素。"
        return "补充概念：Python 面向对象题要分清类、对象、方法和属性。对象保存自己的状态，方法通过当前对象去读取或修改这些状态。"
    return "补充概念：这类题要把定义中的限制条件逐字落到题干上，只要题干把对象、方向、顺序或前提换掉，结论就会改变。"


def refine(q: dict) -> dict:
    q = dict(q)
    if q.get("type") == "tf":
        quick, detail = tf_explanation(q)
    elif q.get("type") == "single":
        quick, detail = single_explanation(q)
    elif q.get("type") == "essay":
        quick, detail = code_explanation(q)
    else:
        quick = q.get("quickExplanation", "")
        detail = q.get("knowledgeDetail", "")
    if len(clean_text(quick)) < 50:
        quick = quick.strip() + "\n" + concept_extension(q)
    if len(clean_text(detail)) < 70:
        detail = detail.strip() + "\n" + concept_extension(q)
    q["quickExplanation"] = quick.strip()
    q["knowledgeDetail"] = detail.strip()
    q["explanation"] = build_explanation(q["quickExplanation"], q["knowledgeDetail"])
    return q


def validate(questions: list[dict]) -> list[str]:
    errors: list[str] = []
    labels = [q["label"] for q in questions]
    if len(labels) != len(set(labels)):
        errors.append("label 重复")
    if [q["id"] for q in questions] != list(range(1, len(questions) + 1)):
        errors.append("id 不连续")
    text = json.dumps(questions, ensure_ascii=False)
    for bad in ["�", "????", "题眼：", "做题抓手", "先定位题眼", "本题入口"]:
        if bad in text:
            errors.append(f"仍包含禁用或乱码片段：{bad}")
    for q in questions:
        label = q["label"]
        if len(q.get("quickExplanation", "")) < 50:
            errors.append(f"{label}: quickExplanation 太短")
        if len(q.get("knowledgeDetail", "")) < 70:
            errors.append(f"{label}: knowledgeDetail 太短")
        if q.get("type") in {"single", "tf"}:
            keys = {o["key"] for o in q.get("options", [])}
            answer = str(q.get("answer", ""))
            if q["type"] == "tf":
                if keys != {"TRUE", "FALSE"} or answer not in keys:
                    errors.append(f"{label}: 判断题选项/答案异常")
            else:
                if answer not in keys:
                    errors.append(f"{label}: 单选答案不在选项中")
        if q.get("type") == "essay" and q.get("options"):
            errors.append(f"{label}: 大题不应有选项")
    return errors


def write_report(before: list[dict], after: list[dict]) -> None:
    def duplicate_stats(items: list[dict], field: str) -> tuple[int, list[tuple[str, int]]]:
        counter = Counter(q.get(field, "") for q in items)
        return len(counter), [(text, n) for text, n in counter.most_common(10) if n > 1]

    before_quick, before_quick_dup = duplicate_stats(before, "quickExplanation")
    before_detail, before_detail_dup = duplicate_stats(before, "knowledgeDetail")
    after_quick, after_quick_dup = duplicate_stats(after, "quickExplanation")
    after_detail, after_detail_dup = duplicate_stats(after, "knowledgeDetail")
    by_type = Counter(q["typeName"] for q in after)
    by_chapter = Counter(q["chapter"] for q in after)

    same_stem = defaultdict(list)
    for q in after:
        same_stem[clean_text(q["stem"])].append(q["label"])
    repeated_stems = {stem: labels for stem, labels in same_stem.items() if len(labels) > 1}

    lines = [
        "# 数据结构 239 题解析优化报告",
        "",
        f"- 题量：{len(after)}",
        f"- 题型：{'; '.join(f'{k} {v}' for k, v in by_type.items())}",
        f"- 章节：{'; '.join(f'{k} {v}' for k, v in by_chapter.items())}",
        "",
        "## 去模板化指标",
        "",
        f"- quickExplanation 唯一数：{before_quick} -> {after_quick}",
        f"- knowledgeDetail 唯一数：{before_detail} -> {after_detail}",
        f"- 低于 80 字 quick：{sum(len(q.get('quickExplanation','')) < 80 for q in before)} -> {sum(len(q.get('quickExplanation','')) < 80 for q in after)}",
        f"- 低于 100 字 detail：{sum(len(q.get('knowledgeDetail','')) < 100 for q in before)} -> {sum(len(q.get('knowledgeDetail','')) < 100 for q in after)}",
        f"- 重复题干数：{len(repeated_stems)}（来自 DS6A/DS6B 随机卷重复题，解析允许随同题重复）",
        "",
        "## 优化原则",
        "",
        "- 判断题直接指出题干中成立或错误的概念关系。",
        "- 单选题给出正确项，并列出选项辨析。",
        "- 函数题/编程题逐题说明输入、核心逻辑、边界和输出格式。",
        "- 删除“题眼”“做题抓手”等元提醒式语言。",
        "",
        "## 仍重复最多的 quick（通常是重复题干）",
        "",
    ]
    for text, count in after_quick_dup[:8]:
        lines.append(f"- {count} 次：{clean_text(text)[:120]}")
    lines += ["", "## 仍重复最多的 detail（通常是重复题干）", ""]
    for text, count in after_detail_dup[:8]:
        lines.append(f"- {count} 次：{clean_text(text)[:120]}")
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> None:
    before = json.loads(QUESTIONS.read_text(encoding="utf-8"))
    after = [refine(q) for q in before]
    errors = validate(after)
    if errors:
        for error in errors[:80]:
            print("ERROR", error)
        raise SystemExit(f"validation failed: {len(errors)} errors")
    QUESTIONS.write_text(json.dumps(after, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    write_report(before, after)
    print(f"wrote {QUESTIONS}")
    print(f"wrote {REPORT}")
    print(f"questions={len(after)}")


if __name__ == "__main__":
    main()
