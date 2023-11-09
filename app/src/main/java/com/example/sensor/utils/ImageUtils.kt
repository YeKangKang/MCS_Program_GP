package com.example.sensor.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

object ImageUtils {

    fun createImageFile(context: Context): File? {
        val imageFileName = "JPEG_${System.currentTimeMillis()}"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir!!.exists()) {
            storageDir.mkdirs()
        }
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(
                imageFileName,  /* 文件名 */
                ".jpg",  /* 文件扩展名 */
                storageDir /* 存储目录 */
            )
            imageFile.setWritable(true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFile
    }

}