# v2.10.48

- 新增“习近平新时代中国特色社会主义思想概论”课程：首页可直接切换进入。
- 从 QQCLI 收到的 `习思想2026版练习题(3)(1).docx` 导入 441 题：单选 269、多选 142、大题 30。
- 每题写入答案、快速做题解析和知识点详解；大题可点“查看参考答案”并自评加入错题本。
- 新增 `tools/import_xi_thought_course.py`，后续可从同类 Word 题库重新生成 `xi_thought_questions.json`。

- Auto release after fix: Quiz: add temporary pinch zoom
- Auto release after fix: Signal: polish transparent formula images
- 信号与系统 40 道题全部改为 MathJax 高清图片渲染：题干、单选选项、答案解析都不再依赖原生 TextView 猜公式。
- 单选题保留点击选项即提交的交互，但选项内容改用图片显示，避免公式字号过大、根号/上下标显示不完整。
- 大题参考答案改用完整答案解析图，Q36 等复杂 Z 变换题已重新渲染，公式、分式、表格都能正常显示。
- 新增 `tools/render_signal_system_images.js`，后续修改信号与系统题库后可以一键重渲染全部公式图片。
- 保留在线更新兼容资产：`exam-prep-handbook.apk`、`review-baodian.apk`、`exam-prep-handbook-update.json`、`network_quiz_update.json`。
