const fs = require("fs");
const path = require("path");

const projectRoot = path.resolve(__dirname, "..");
const sourceRoot = path.resolve(projectRoot, "..", "video_knowledge_BV1H4kwYHEcR", "mindmaps");
const cardsPath = path.join(projectRoot, "app", "src", "main", "assets", "chapter_cards.json");
const questionsPath = path.join(projectRoot, "app", "src", "main", "assets", "questions.json");
const overridesPath = path.join(projectRoot, "app", "src", "main", "assets", "chapter_card_overrides.json");

const chapterSources = [
  ["chapter_01_overview.md", "1. 网络基础与体系结构", "第一章总览：网络基础与体系结构极细导图"],
  ["chapter_02_physical_layer.md", "2. 物理层与传输媒体", "第二章总览：物理层与传输媒体极细导图"],
  ["chapter_03_data_link_layer.md", "3. 数据链路层与局域网", "第三章总览：数据链路层与局域网极细导图"],
  ["chapter_04_network_layer.md", "4. 网络层与路由", "第四章总览：网络层与路由极细导图"],
  ["chapter_05_transport_layer.md", "5. 运输层", "第五章总览：运输层极细导图"],
  ["chapter_06_application_layer.md", "6. 应用层", "第六章总览：应用层极细导图"],
  ["chapter_07_network_security.md", "7. 网络安全", "第七章总览：网络安全极细导图"],
];

function readJson(file) {
  return JSON.parse(fs.readFileSync(file, "utf8"));
}

function stripMarkdown(value) {
  return String(value || "")
    .replace(/\*\*/g, "")
    .replace(/__/g, "")
    .replace(/`/g, "")
    .replace(/~~/g, "")
    .replace(/\s+/g, " ")
    .trim();
}

function parseBulletMindMap(markdown) {
  const root = { title: "", children: [] };
  const stack = [{ indent: -1, node: root }];

  for (const raw of markdown.replace(/\r\n/g, "\n").replace(/\r/g, "\n").split("\n")) {
    if (!/^\s*[-*]\s+/.test(raw)) continue;
    const indent = raw.match(/^\s*/)[0].replace(/\t/g, "  ").length;
    const title = stripMarkdown(raw.replace(/^\s*[-*]\s+/, ""));
    if (!title) continue;

    while (stack.length > 1 && indent <= stack[stack.length - 1].indent) {
      stack.pop();
    }
    const node = { title, children: [] };
    stack[stack.length - 1].node.children.push(node);
    stack.push({ indent, node });
  }

  function convert(node) {
    const branchChildren = [];
    const points = [];
    for (const child of node.children || []) {
      if (child.children && child.children.length) {
        branchChildren.push(convert(child));
      } else {
        points.push(child.title);
      }
    }
    return {
      title: node.title,
      summary: points.length ? points[0] : "",
      badge: "",
      points,
      children: branchChildren,
    };
  }

  return root.children.map(convert);
}

function chapterTitleFromMarkdown(markdown) {
  const match = markdown.match(/^##\s+(.+)$/m);
  return match ? stripMarkdown(match[1]) : "";
}

function firstPoint(nodes, title) {
  const node = nodes.find((item) => item.title === title);
  if (!node) return "";
  if (node.points && node.points.length) return node.points.join(" ");
  if (node.children && node.children.length) return node.children.map((item) => item.title).join("；");
  return node.summary || "";
}

function labelsForChapter(cards, chapter) {
  const labels = [];
  const seen = new Set();
  for (const card of cards) {
    if (card.chapter !== chapter) continue;
    for (const label of card.labels || []) {
      if (!seen.has(label)) {
        seen.add(label);
        labels.push(label);
      }
    }
  }
  return labels;
}

function questionCountForChapter(questions, chapter) {
  return questions.filter((q) => q.chapter === chapter).length;
}

function typeDistributionForChapter(questions, chapter) {
  const counts = new Map();
  for (const q of questions) {
    if (q.chapter !== chapter) continue;
    counts.set(q.typeName, (counts.get(q.typeName) || 0) + 1);
  }
  return [...counts.entries()].map(([type, count]) => `${type} ${count}`).join("，");
}

function main() {
  const cards = readJson(cardsPath);
  const questions = readJson(questionsPath);
  const existing = fs.existsSync(overridesPath) ? readJson(overridesPath) : [];
  const preserved = existing.filter((item) => item.mode !== "insert");

  const inserted = chapterSources.map(([file, chapter, knowledge]) => {
    const markdown = fs.readFileSync(path.join(sourceRoot, file), "utf8");
    const nodes = parseBulletMindMap(markdown);
    const chapterTitle = chapterTitleFromMarkdown(markdown);
    const labels = labelsForChapter(cards, chapter);
    const count = questionCountForChapter(questions, chapter);
    const layerHint = firstPoint(nodes, "章节定位") || firstPoint(nodes, "本章定位") || chapterTitle;
    const chapterMap = nodes.map((node) => node.title).join(" → ");

    return {
      mode: "insert",
      chapter,
      knowledge,
      questionCount: count,
      labels,
      typeDistribution: typeDistributionForChapter(questions, chapter),
      layerHint,
      chapterMap,
      frontMarkdown: markdown,
      backMarkdown: "## 使用方式\n- 先点开主分支，看这一章分成哪些知识块。\n- 再逐层展开子树，把概念、流程、易错点放回同一张图里理解。\n- 需要全局视野时进入全屏竖屏，用双指缩小查看全章结构。",
      mindMapTitle: chapterTitle ? `${chapterTitle} · 极细导图` : knowledge,
      mindMapNodes: nodes,
    };
  });

  fs.writeFileSync(overridesPath, JSON.stringify([...inserted, ...preserved], null, 2) + "\n", "utf8");
  console.log(`Imported ${inserted.length} chapter mind maps into ${overridesPath}`);
}

main();
