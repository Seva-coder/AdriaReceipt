package mne.seva.mnereceipt.data.storage

import android.net.Uri

interface FileRepository {

    suspend fun writeTextToFileUri(uri: Uri, text: String)

}