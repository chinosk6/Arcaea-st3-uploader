package io.github.chinosk.arcst3upload.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.robv.android.xposed.XposedBridge
import java.io.File
import java.io.FileInputStream


object JsonParser {
    inline fun <reified T> parseJson (jsonStr: String): T? {
        return try {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
            jsonAdapter.fromJson(jsonStr)
        } catch (e: Exception) {
            XposedBridge.log("parseJson <${T::class.java.name}> error: $e")
            null
        }
    }

    inline fun <reified T> parseJson (jsonMap: Map<*, *>): T? {
        return try {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
            jsonAdapter.fromJsonValue(jsonMap)
        } catch (e: Exception) {
            XposedBridge.log("parseJsonFromJsonValue <${T::class.java.name}> error: $e")
            null
        }
    }

    inline fun <reified T> parseJson (file: File): T? {
        return try {
            if (file.exists()) {
                val inputStream = FileInputStream(file)
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                return parseJson(jsonString)
            }
            return null
        }
        catch (e: Exception) {
            XposedBridge.log("parseJson from file (${file.name}) <${T::class.java.name}> error: $e")
            null
        }
    }

    inline fun <reified T> dumpJson (json: T, indent: Int = 0): String? {
        return try {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
            jsonAdapter.indent(" ".repeat(indent)).toJson(json)
        } catch (e: Exception) {
            XposedBridge.log("dumpJson <${T::class.java.name}> error: $e")
            null
        }
    }

    inline fun <reified T> dumpJson (json: T, file: File, indent: Int = 0) {
        try {
            val jsonStr = dumpJson(json, indent) ?: return
            if (!file.exists()) {
                file.createNewFile()
            }
            file.writeText(jsonStr)
        }
        catch (e: Exception) {
            XposedBridge.log("dump json to file ${file.name} failed: $e")
        }
    }

}