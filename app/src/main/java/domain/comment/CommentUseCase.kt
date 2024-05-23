package domain.comment

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommentUseCase @Inject constructor(private val commentRepository: CommentRepository) {
    suspend fun postComment(contents: String, parentUID: String, collectionName: String) = flow {
        emit(commentRepository.postComment(contents, parentUID, collectionName))
    }

    suspend fun postReply(
        contents: String,
        parentUID: String,
        nestedCommentCollection: String,
        commentName: String
    ) = flow {
        emit(commentRepository.postReply(contents, parentUID, nestedCommentCollection, commentName))
    }

    suspend fun getComments(
        parentUID: String,
        collectionName: String,
    ) = flow {
        emit(commentRepository.getComments(parentUID, collectionName))
    }

    suspend fun getMoreComments(parentUID: String, collectionName: String) =
        flow {
            emit(commentRepository.getMoreComments(parentUID, collectionName))
        }

    suspend fun getNestedComments(parentUID: String, nestedCommentName: String) = flow {
        emit(commentRepository.getNestedComments(parentUID, nestedCommentName))
    }
}