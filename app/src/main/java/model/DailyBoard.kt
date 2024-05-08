package model

import android.net.Uri
import com.google.firebase.storage.ListResult
import java.io.Serializable


data class DailyBoard(
    val writerNickname: String,
    val writerProfileUri: Uri,
    val boardContents: String,
    val files: List<Uri>,
    val disLike: Int,
    val like: Int,
    val boardUID: String,
    val favourability: String,
    val viewType : Int
) : Serializable