# v2.10.45

- 信号与系统 40 道题全部改为 MathJax 高清图片渲染：题干、单选选项、答案解析都不再依赖原生 TextView 猜公式。
- 单选题保留点击选项即提交的交互，但选项内容改用图片显示，避免公式字号过大、根号/上下标显示不完整。
- 大题参考答案改用完整答案解析图，Q36 等复杂 Z 变换题已重新渲染，公式、分式、表格都能正常显示。
- 新增 `tools/render_signal_system_images.js`，后续修改信号与系统题库后可以一键重渲染全部公式图片。
- 保留在线更新兼容资产：`exam-prep-handbook.apk`、`review-baodian.apk`、`exam-prep-handbook-update.json`、`network_quiz_update.json`。
