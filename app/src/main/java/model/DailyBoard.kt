package model

import android.net.Uri
import com.google.firebase.storage.ListResult


data class DailyBoard(val writerProfileUri: Uri, val writerNickname: String, val boardContents: String, val images: List<Uri>, val disLike: Int, val like: Int)
