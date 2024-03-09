package io.github.chinosk.arcst3upload.utils

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import io.github.chinosk.arcst3upload.utils.models.UserMe
import java.io.File


class FileUploader(context: Context) {
    private val context: Context
    private val mainHandler = Handler(Looper.getMainLooper())
    init {
        this.context = context
    }

    private fun showToastOnMainThread(msg: String, length: Int = Toast.LENGTH_SHORT) {
        mainHandler.post {
            Toast.makeText(context, msg, length).show()
        }
    }

    private fun showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(context, msg, length).show()
        } else {
            showToastOnMainThread(msg, length)
        }
    }

    class AlphanumericInputFilter : InputFilter {
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            val regex = "[a-zA-Z0-9|@.]+".toRegex()
            return if (source?.matches(regex) == true) {
                null
            } else {
                ""
            }
        }
    }

    fun showLoginDialog(onConfirm: (username: String, password: String, uploadCookie: Boolean) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("上传数据")

        // 创建一个布局，包含两个 EditText，分别用于输入用户名和密码
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        val descText = TextView(context)
        descText.text = "仅用于同步账号数据，不会将密码上传至服务器。"
        layout.addView(descText)

        val usernameEditText = EditText(context)
        usernameEditText.hint = "User Name"
        usernameEditText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS // 禁止输入法提示
        usernameEditText.filters = arrayOf<InputFilter>(AlphanumericInputFilter()) // 设置输入过滤器
        layout.addView(usernameEditText)

        val passwordEditText = EditText(context)
        passwordEditText.hint = "Password"
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        layout.addView(passwordEditText)

        val uploadCookieCheckBox = CheckBox(context)
        uploadCookieCheckBox.text = "Upload Cookie (上传Cookie用于 /a 指令)"
        uploadCookieCheckBox.isChecked = true
        layout.addView(uploadCookieCheckBox)

        builder.setView(layout)

        // 添加确认按钮，点击时获取输入的用户名和密码
        builder.setPositiveButton("确定") { _, _ ->
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val checkUploadCookie = uploadCookieCheckBox.isChecked
            onConfirm(username, password, checkUploadCookie)
        }

        // 添加取消按钮
        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun getFile(filePath: String): File {
        return File(context.filesDir, filePath)
    }

    fun deleteWebAPISidCache() {
        val cacheFile = getFile("webapi_sid_cache.txt")
        if (cacheFile.exists()) {
            if (cacheFile.delete()) {
                showToast("已清除缓存")
            }
            else {
                showToast("缓存清除失败")
            }
        }
        else {
            showToast("缓存不存在")
        }
    }

    private fun checkCacheWebCookieAndGetMe(onMeData: (meData: String?, cacheFile: File) -> Unit) {
        val cacheFile = getFile("webapi_sid_cache.txt")
        if (!cacheFile.exists()) {
            onMeData(null, cacheFile)
            return
        }
        val sid = cacheFile.readText()
        API.ArcWebAPI.userMe(sid) { userMeData, _ ->
            onMeData(userMeData, cacheFile)
        }
    }

    private fun onGetUserMe(userMeString: String, st3File: File, syncSid: String?) {
        val userMe = JsonParser.parseJson<UserMe>(userMeString)
        if (userMe == null) {
            showToast("解析 user/me 失败")
            return
        }
        if (!userMe.success) {
            showToast("获取 user/me 失败")
            return
        }

        showToast("开始上传 st3 (${userMe.value?.name} ${userMe.value?.user_code})...")

        API.ChieriAPI.uploadSt3(userMeString, st3File, syncSid) { success, message ->
            if (success) {
                showToast("成功: $message")
            }
            else {
                showToast("失败: $message")
            }
        }

        showToast("st3上传成功")
    }

    fun uploadSt3() {
        val st3File = getFile("st3")
        if (st3File.exists()) {
            showToast("检查缓存...")

            checkCacheWebCookieAndGetMe {meData, cacheFile ->
                if (meData != null) {
                    onGetUserMe(meData, st3File, null)
                }
                else {
                    showToast("缓存为空或过期，需要重新登陆。")
                    showLoginDialog { username, password, uploadCookie ->
                        showToast("登录中...")
                        API.ArcWebAPI.login(username, password) { sid ->
                            if (sid != null) {
                                API.ArcWebAPI.userMe(sid) { userMeData, errorData ->
                                    if (errorData != null) {
                                        showToast("请求用户信息失败: $errorData")
                                    }
                                    else if (userMeData != null) {
                                        if (!cacheFile.exists()) {
                                            cacheFile.createNewFile()
                                        }
                                        cacheFile.writeText(sid)
                                        onGetUserMe(userMeData, st3File, if (uploadCookie) sid else null)
                                    }
                                    else {
                                        showToast("user/me 响应为空")
                                    }
                                }
                            }
                            else {
                                showToast("获取用户信息失败，可能是密码输入错误。")
                            }
                        }
                    }
                }
            }
        }
        else {
            showToast("未找到 st3: $st3File", Toast.LENGTH_LONG)
        }


    }

}