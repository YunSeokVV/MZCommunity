package domain.comment

import data.model.Comment
import data.model.Response


interface CommentRepository {

    suspend fun postComment(
        contents: String,
        parentUID: String,
        collectionName: String
    ): Response<Boolean>

    suspend fun postReply(
        contents: String,
        parentUID: String,
        nestedCommentCollection: String,
        commentName: String
    ): Response<Boolean>

    suspend fun getComments(
        parentUID: String,
        collectionName: String
    ): List<Comment>

    suspend fun getMoreComments(
        parentUID: String,
        collectionName: String
    ): List<Comment>

    suspend fun getNestedComments(parentUID: String, nestedCommentName: String): List<Comment>
}