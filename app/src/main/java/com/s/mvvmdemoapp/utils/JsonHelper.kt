package com.s.mvvmdemoapp.utils

import android.content.Context
import java.io.IOException
import java.nio.charset.Charset


class JsonHelper {
    companion object{
        fun getJsonFromAssets(
            context: Context,
            fileName: String?
        ): String? {
            val jsonString: String
            jsonString = try {
                val inputStream = context.assets.open(fileName?:"")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charset.forName("UTF-8"))
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            return jsonString
        }
    }
}