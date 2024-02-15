# Arcaea-st3-uploader
 - Upload your arcaea local st3 data to Chieri Bot server.

## arc_res_edit
 - 删除 Arcaea 包内多余的资源，用于生成独立传分客户端

## st3_upload_xp
 - Xposed 模块，用于上传 st3


# 传分端制作
 - 下列步骤仅用于介绍共存版 Arcaea 传分端制作，用于未 Root 且不想使用 LSPatch 修改原版客户端的用户。
 - 版本更新后，我会制作新的传分端，可在 QQ 群内获取，或者前往 [网盘分流（密码5e2w）](https://wwm.lanzouq.com/b01g0jfgd) 下载。

## 步骤
 - 1. 下载 [apk-editor-studio](https://github.com/kefir500/apk-editor-studio)
 - 2. 在 apk-editor-studio 内打开 Arcaea apk
 - 3. 选择 工具 - 克隆APK，将包名修改为 `moe.low.chinosk.arc`
 - 4. 根据你的需要修改应用程序标题
 - 5. 进入 `arc_res_edit` 文件夹，运行 `arc_res_edit.py` (Python环境准备不再赘述)
 - 6. 输入 apk-editor-studio 释放的 apk 临时根目录路径，回车等待资源替换
 - 7. 替换完成后，在 apk-editor-studio 内点击保存 APK，将修改后的 APK 发送到手机
 - 8. 手机上记得安装 `Arc st3 upload` XP 模块和 [LSPatch](https://github.com/LSPosed/LSPatch)
 - 9. 在 LSPatch 中，点击 管理 - 新建修补 - 从储存目录选择APK，选择修改后的 Arcaea APK
 - 10. 点击 `便携模式`，嵌入 `Arc st3 upload` 模块即可
 