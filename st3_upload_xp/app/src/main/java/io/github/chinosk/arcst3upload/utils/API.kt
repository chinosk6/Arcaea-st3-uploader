package io.github.chinosk.arcst3upload.utils

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException


object API {
    val JSON: MediaType = "application/json".toMediaType()
    private val client = OkHttpClient()

    fun postJson(url: String, json: String, callback: Callback) {
        val body: RequestBody = json.toRequestBody(JSON)
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun postForm(url: String, formBody: MultipartBody, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()
        client.newCall(request).enqueue(callback)
    }


    fun get(url: String, headers: Headers?, callback: Callback) {
        var request = Request.Builder()
            .url(url)
            .get()

        if (headers != null) {
            request = request.headers(headers)
        }
        client.newCall(request.build()).enqueue(callback)
    }

    private fun extractSidFromCookie(cookieString: String): String? {
        val regex = "sid=([^;]+)".toRegex()
        val matchResult = regex.find(cookieString)
        return matchResult?.groups?.get(1)?.value
    }

    object ArcWebAPI {
        fun login(email: String, password: String, onGetCookieSid: (sid: String?) -> Unit) {
            postJson("https://webapi.lowiro.com/auth/login",
                "{\"email\":\"$email\",\"password\":\"$password\"}",
                object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onGetCookieSid(null)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.code != 200) {
                            onGetCookieSid(null)
                            return
                        }
                        val setCookies = response.headers("set-cookie")
                        for (i in setCookies) {
                            val sid = extractSidFromCookie(i)
                            if (sid != null) {
                                onGetCookieSid(sid)
                                return
                            }
                        }
                        onGetCookieSid(null)
                    }
                })
        }

        fun userMe(sid: String, onResponse: (userMeData: String?, errorData: IOException?) -> Unit) {
            val sendSid: String = if (!sid.startsWith("sid=")) {
                "sid=$sid"
            } else {
                sid
            }
            val headers = Headers.Builder()
                .add("Cookie", sendSid)
                .build()

            get("https://webapi.lowiro.com/webapi/user/me", headers,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onResponse(null, e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        onResponse(response.body!!.string(), null)
                    }

                })
        }
    }

    object ChieriAPI {
        fun uploadSt3(meData: String, st3File: File, syncSid: String?, onResult: (success: Boolean, message: String) -> Unit) {
            var postForm = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("me_data", null,
                    meData.toRequestBody("application/json".toMediaType())
                )
                .addFormDataPart("st3", st3File.name, st3File.asRequestBody())

            if (syncSid != null) {
                postForm = postForm.addFormDataPart("sync_sid", null, syncSid.toRequestBody())
            }

            postForm("https://www.chinosk6.cn/arcscore/upload/st3", postForm.build(),
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onResult(false, e.toString())
                    }

                    override fun onResponse(call: Call, response: Response) {
                        onResult(response.code == 200, response.body!!.string())
                    }

                })
        }
    }

}