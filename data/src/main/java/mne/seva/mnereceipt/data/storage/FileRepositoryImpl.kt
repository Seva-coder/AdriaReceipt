package mne.seva.mnereceipt.data.storage

import android.content.Context
import android.net.Uri
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter


class FileRepositoryImpl(val context: Context) : FileRepository {

    override suspend fun writeTextToFileUri(uri: Uri, text: String) {
        try {
            val outputStream = context.contentResolver.openOutputStream(uri)
            outputStream?.let {
                val bw = BufferedWriter(OutputStreamWriter(it))
                bw.write(text)
                bw.flush()
                bw.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}