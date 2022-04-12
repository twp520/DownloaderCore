package com.beeline.downloader.core.download

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.beeline.common.CODE_IO_EXP
import com.beeline.common.CODE_NET_ERROR
import com.beeline.common.CODE_NOT_FOUNT_EXP
import com.beeline.common.UIResult
import com.blankj.utilcode.util.LogUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

/**
 * create by colin
 * 2022/3/10
 */
open class EmptyDownloader : IDownloader {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .build()
    }

    override suspend fun prepareUrl(url: String): UIResult<String> {
        return UIResult.error(CODE_NET_ERROR, "empty implement")
    }

    override suspend fun prepareUrl2List(url: String): UIResult<List<String>> {
        return UIResult.error(CODE_NET_ERROR, "empty implement")
    }

    override fun download(
        context: Context,
        url: String,
        progress: (progress: Int) -> Unit
    ): UIResult<DownloadResult> {
        LogUtils.d("开始下载：url = $url")
        val request = Request.Builder().url(url).get().build()
        try {
            val response = execute(request)
            val body = response.body()
            val contentType = body?.contentType()
            return if (response.isSuccessful && body != null && contentType != null) {
                saveFile(
                    context,
                    url,
                    contentType.type(),
                    contentType.subtype(),
                    body.byteStream(),
                    body.contentLength(),
                    progress
                )
            } else {
                UIResult.error(CODE_NOT_FOUNT_EXP, "Not Login")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return UIResult.error(CODE_NET_ERROR, e.message)
        }
    }


    protected fun execute(request: Request): Response {
        return httpClient.newCall(request).execute()
    }

    private fun saveFile(
        context: Context,
        url: String,
        type: String,
        subType: String,
        inputStream: InputStream,
        total: Long,
        progress: (progress: Int) -> Unit
    ): UIResult<DownloadResult> {
        LogUtils.d("保存文件：$url")
        //存到私有目录
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = extracted(type, subType)
            val contentUri = if (type == "video") {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val insert = context.contentResolver.insert(
                contentUri,
                contentValues.first
            )
            return if (insert != null) {
                val outputStream = context.contentResolver.openOutputStream(insert)
                if (outputStream != null) {
                    // outputStream.write(inputStream.readBytes())
                    writeStream(inputStream, outputStream, total, progress)
                    outputStream.flush()
                    outputStream.close()
                    contentValues.first.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(insert, contentValues.first, null, null)
                    UIResult.content(
                        DownloadResult(
                            insert.toString(),
                            type,
                            contentValues.second,
                            contentValues.third
                        )
                    )
                } else {
                    UIResult.error(CODE_IO_EXP, "save fail")
                }
            } else {
                UIResult.error(CODE_IO_EXP, "save fail")
            }
        } else {
            val dir = if (type == "video") {
                Environment.DIRECTORY_MOVIES
            } else {
                Environment.DIRECTORY_PICTURES
            }
            val timeMillis = System.currentTimeMillis()
            val file = File(dir, "${saveFilePrefix}_$timeMillis.$subType")
            file.createNewFile()
            val fos = file.outputStream()
            // fos.write(inputStream.readBytes())
            writeStream(inputStream, fos, total, progress)
            fos.flush()
            fos.close()
            return UIResult.content(
                DownloadResult(
                    file.absolutePath,
                    type,
                    file.name,
                    timeMillis
                )
            )
        }
    }

    private fun writeStream(
        inputStream: InputStream, outputStream: OutputStream, total: Long,
        progress: (progress: Int) -> Unit
    ) {
        var sum = 0L
        val buffer = ByteArray(1024)
        var len: Int
        while (((inputStream.read(buffer)).also { len = it }) != -1) {
            outputStream.write(buffer, 0, len)
            sum += len
            //获取当前下载量
            progress.invoke((sum.toDouble() / total.toDouble() * 100).toInt())
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected fun extracted(type: String, subType: String): Triple<ContentValues, String, Long> {
        val contentValues = ContentValues()
        // 文件的描述
        val keys = if (type == "video") {
            arrayOf(
                MediaStore.Video.Media.DESCRIPTION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.RELATIVE_PATH,
                Environment.DIRECTORY_MOVIES,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.IS_PENDING
            )
        } else {
            arrayOf(
                MediaStore.Images.Media.DESCRIPTION,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.IS_PENDING
            )
        }
        val timeMillis = System.currentTimeMillis()
        contentValues.put(keys[0], "${saveFilePrefix}_$timeMillis")
        // 设置保存到公共目录下的文件的名称
        val filename = "${saveFilePrefix}_$timeMillis.$subType"
        contentValues.put(
            keys[1],
            filename
        )
        //文件的类型，MIME_TYPE 为image/jpeg避免部分手机相册无法更新
        contentValues.put(keys[2], "$type/$subType")
        contentValues.put(
            keys[3],
            keys[4] + File.separator + saveFilePrefix
        )
        //文件的标题
        contentValues.put(keys[5], "${saveFilePrefix}_$timeMillis")
        contentValues.put(keys[6], timeMillis)
        contentValues.put(keys[7], 1)
        return Triple(contentValues, filename, timeMillis)
    }


    private val saveFilePrefix = "AllVideoSaver"
}