# v2.10.64

- 修复 App 内置更新下载源：APK 下载只使用 `ghfast` 镜像，不再尝试 GitHub 原始下载链接。
- 更新元数据读取同样优先固定为 `ghfast` Release 资产镜像，避免 GitHub 直连卡住导致“处理中”。
- 更新 JSON 生成逻辑同步调整，`apkDownloadUrl` 和 `apkDownloadCandidates` 只写入 `ghfast` 镜像地址。
- 旧元数据里如果仍包含 GitHub 原链，App 会在整理下载候选时过滤掉原链，只保留 ghfast 镜像。
