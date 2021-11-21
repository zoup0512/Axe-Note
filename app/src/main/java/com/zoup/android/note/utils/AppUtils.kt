package com.zoup.android.note.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import java.io.*
import java.util.*
import kotlin.random.Random.Default.nextInt
import android.provider.MediaStore

import android.provider.DocumentsContract

import android.content.ContentUris
import android.content.Intent
import android.database.Cursor

import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.zoup.android.note.BuildConfig
import com.zoup.android.note.R


object AppUtils {

    @SuppressLint("Range")
    fun uriToFileName(uri: Uri, context: Context): String {
        return when (uri.scheme) {
            ContentResolver.SCHEME_FILE -> uri.toFile().name
            ContentResolver.SCHEME_CONTENT -> {
                val cursor = context.contentResolver.query(uri, null, null, null, null, null)
                cursor?.let {
                    it.moveToFirst()
                    val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor.close()
                    displayName
                } ?: "${System.currentTimeMillis()}.${
                    MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(context.contentResolver.getType(uri))
                }}"

            }
            else -> "${System.currentTimeMillis()}.${
                MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(context.contentResolver.getType(uri))
            }}"
        }
    }

    fun copyFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bis = BufferedInputStream(inputStream)
        val fileName = uriToFileName(uri, context)
        val destFile = File(context.filesDir, fileName)
        val fos = FileOutputStream(destFile)
        var bos = BufferedOutputStream(fos)
        var buffer = ByteArray(1024)
        var len = 0
        while (true) {
            len = bis.read(buffer)
            if (len == -1) break
            bos.write(buffer, 0, len)
        }
        bis.close()
        fos.close()
        return destFile
    }

    fun InputStream.readFile(): String {
        try {
            val scanner = Scanner(this).useDelimiter("\\^")
            if (scanner.hasNext()) {
                return scanner.next()
            }
            return ""
        } finally {
            try {
                close()
            } catch (e: IOException) {

            }
        }
    }

    fun BufferedReader.readFile(): String {
        try {
            val resultStringBuilder = StringBuilder()
            while (this.readLine() != null) {
                resultStringBuilder.append(this.readLine())
            }
            return resultStringBuilder.toString()
        } finally {
            close()
        }
    }

    fun String.showToast(context: Context) {
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun uriToFileQ(context: Context, uri: Uri): File? =
        if (uri.scheme == ContentResolver.SCHEME_FILE)
            File(requireNotNull(uri.path))
        else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件保存到沙盒
            val contentResolver = context.contentResolver
            val displayName = uriToFileName(uri,context)
            val ios = contentResolver.openInputStream(uri)
            if (ios != null) {
                File("${context.cacheDir.absolutePath}/$displayName")
                    .apply {
                        val fos = FileOutputStream(this)
                        FileUtils.copy(ios, fos)
                        fos.close()
                        ios.close()
                    }
            } else null
        } else null

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.provider"

    fun getMimeType(url: String): String {
        val ext = MimeTypeMap.getFileExtensionFromUrl(url)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: "text/plain"
    }

    fun getFilesList(selectedItem: File): List<File> {
        val rawFilesList = selectedItem.listFiles()?.filter { !it.isHidden }

        return if (selectedItem == Environment.getExternalStorageDirectory()) {
            rawFilesList?.toList() ?: listOf()
        } else {
            listOf(selectedItem.parentFile) + (rawFilesList?.toList() ?: listOf())
        }
    }

    fun renderParentLink(activity: AppCompatActivity): String {
        return activity.getString(R.string.go_parent_label)
    }

    fun renderItem(activity: AppCompatActivity, file: File): String {
        return if (file.isDirectory) {
            activity.getString(R.string.folder_item, file.name)
        } else {
            activity.getString(R.string.file_item, file.name)
        }
    }


    fun openFile(activity: AppCompatActivity, selectedItem: File) {
        // Get URI and MIME type of file
        val uri = FileProvider.getUriForFile(activity.applicationContext, AUTHORITY, selectedItem)
        val mime: String = getMimeType(uri.toString())

        // Open file with user selected app
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, mime)
        return activity.startActivity(intent)
    }
}