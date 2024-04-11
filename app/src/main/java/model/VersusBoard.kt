package model

import android.net.Uri

data class VersusBoard(
    val writerUri : Uri,
    val writerName : String,
    val boardTitle: String,
    val opinion1: String,
    val opinion1Count: Long,
    val opinion2: String,
    val opinion2Count: Long,
    val boardUID : String,
    // 사용자가 게시글에 투표를 했는지 판별해주는 변수
    val userUID : Boolean
)
