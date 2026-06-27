# 备考宝典 APK

离线刷题与复习 Android 应用，目前内置“计算机网络”课程题库。

## 本地构建

```powershell
python .\tools\build_exam_prep_handbook_apk.py
```

构建产物：

- `build\out\exam-prep-handbook.apk`：线上更新主资产
- `build\out\review-baodian.apk`：旧版本兼容别名
- `build\out\备考宝典.apk`：本地中文文件名

## Release 元数据

```powershell
python .\tools\generate_release_metadata.py
```

默认生成：

- `release\exam-prep-handbook-update.json`
- `release\network_quiz_update.json`，旧版本兼容别名

## 发布到 GitHub Release

默认仓库：

`ZhiKong0/exam-prep-handbook-apk`

```powershell
.\tools\publish_github_release.ps1 -RepoSlug ZhiKong0/exam-prep-handbook-apk
```

发布脚本会重新构建 APK，生成元数据，并上传新旧两套兼容资产。

## 兼容说明

Android 包名保留 `com.dz.networkquiz`，provider authority 保留 `com.dz.networkquiz.export`，这样旧手机安装包可以原地升级并保留数据。旧仓库名、旧 APK 资产名、旧元数据名仅作为过渡兼容保留。
