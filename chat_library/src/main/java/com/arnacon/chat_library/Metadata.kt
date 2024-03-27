package com.arnacon.chat_library

import org.json.JSONObject
import java.io.File

class Metadata {

    fun textMetadata(user: String, text: String): String {
        val contentJson = JSONObject().apply {
            put("sender", user)
            put("text", text)
        }
        return contentJson.toString()

    }

    fun fileMetadata(user: String, file: File, filename: String, cid: String): JSONObject {
        return JSONObject().apply {
            put ("sender", user)
            put("filename", filename)
            put("filetype", file.extension)
            put("filesize", file.length())
            put("cid", cid)  // CID of the file
        }
    }
}