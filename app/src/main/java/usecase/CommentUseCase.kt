package usecase

import kotlinx.coroutines.flow.flow
import repository.CommentRepostiroy
import javax.inject.Inject

class CommentUseCase @Inject constructor(private val commentRepostiroy: CommentRepostiroy) {
    suspend fun postComment(contents: String, parentUID: String, collectionName: String) = flow {
        emit(commentRepostiroy.postComment(contents, parentUID, collectionName))
    }

    suspend fun postReply(
        contents: String,
        parentUID: String,
        nestedCommentCollection: String,
        commentName: String
    ) = flow {
        emit(commentRepostiroy.postReply(contents, parentUID, nestedCommentCollection, commentName))
    }

    suspend fun getComments(parentUID: String, collectionName: String) = flow {
        emit(commentRepostiroy.getComments(parentUID, collectionName))
    }

    suspend fun getNestedComments(parentUID: String, nestedCommentName : String) = flow {
        emit(commentRepostiroy.getNestedComments(parentUID, nestedCommentName))
    }
}