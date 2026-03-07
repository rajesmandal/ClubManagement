package com.sohitechnology.gymstudio.hammer.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageUtil {
    fun compressAndEncodeToBase64(context: Context, bitmap: Bitmap): String? {
        return try {
            // Compress Bitmap to under 200KB
            val tempFile = File.createTempFile("IMG_", ".jpg", context.cacheDir)
            var quality = 90
            var fileSize: Long

            do {
                val outputStream = FileOutputStream(tempFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
                outputStream.close()
                fileSize = tempFile.length()
                if (fileSize > 200 * 1024) {
                    quality -= 10
                }
            } while (fileSize > 200 * 1024 && quality > 10) // 200KB limit, min quality 10

            // Convert compressed file back to Base64
            val compressedBitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
            val byteArrayOutputStream = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            
            // Clean up temp file
            if (tempFile.exists()) tempFile.delete()

            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return try {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            bitmap
        }
    }

    fun getOrientation(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val exif = ExifInterface(input)
                exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }
}
