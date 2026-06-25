# v2.10.16

- Auto release after fix: Updater: fix auto-update network gate and prefer fresh fast metadata
- Auto release after fix: Release hook: ignore generated update metadata when gating auto release
- Auto release after fix: Updater: auto-check on every app open and harden release hygiene
- Auto release after fix: Polish option-wise explanations and updater state for v2.10.11
- 修复多选题答案页的逐项理由展示：每个选项现在都会单独高亮显示对错依据，更容易一眼看懂。
- 继续精修 75 道多选题的“选项判断”文案，减少脚本腔，补强贴题表达。
- 修复更新状态文案：当本机版本比 GitHub latest 更新时，会明确提示“本机版本较新”，不再误导成“无可更新”。
- 统一本地 release 元数据输出，APK 资产名固定为 `review-baodian.apk`，避免中文文件名带来的更新识别问题。
