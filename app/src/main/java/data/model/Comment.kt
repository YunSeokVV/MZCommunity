package data.model

data class Comment(
    val witerUri: String,
    val writerName: String,
    val contents: String,
    val commentUID: String,
    val hasNestedComment: Boolean
)