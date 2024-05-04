package model

data class DailyboardCollection(
    val boardContents: String,
    val disLike: Int,
    val like: Int,
    val writerUID: String,
    val favourability: String,
    val files : List<String>,
    val viewType : Int
)
