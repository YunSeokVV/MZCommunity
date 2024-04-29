package model

import android.net.Uri
import com.google.firebase.storage.ListResult


data class DailyBoard(
    val writerProfileUri: Uri,
    val writerNickname: String,
    val boardContents: String,
    val files: List<Uri>,
    val disLike: Int,
    val like: Int,
    val boardUID: String,
    val favourability: String
)
