package model

import android.net.Uri
import com.google.firebase.storage.ListResult
import java.io.Serializable


data class DailyBoard(
    val writerNickname: String,
    val writerProfileUri: String,
    val boardContents: String,
    val files: List<String>,
    val disLike: Int,
    val like: Int,
    val boardUID: String,
    val favourability: String,
    val viewType : Int
) : Serializable