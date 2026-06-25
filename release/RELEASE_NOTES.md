# v2.10.8

- Auto release after fix: Updater: delayed auto-check with looser network gate
- Auto release after fix: Updater: auto-check on app launch
- Auto release after fix: Updater: default repo fallback and ready-state copy
- Auto release after fix: Fallback to APK matching when metadata fetch times out
- Auto release after fix: Fail auto release when git push does not succeed
- Auto release after fix: Tighten updater cleanup and fix metadata asset upload
- Auto release after fix: Fix updater cleanup fallback and add auto release hooks
- 新增基于 GitHub Release 的联网检查更新入口。
- 支持应用内下载 APK、调起系统安装流程，并在写入安装会话后自动清理缓存安装包。
- 设置页新增 GitHub 更新仓库配置，支持填写 `owner/repo` 或完整 GitHub 链接。
