package domain.comment

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostReplyUsecase @Inject constructor(private val commentRepository: CommentRepository) {
    suspend operator fun invoke(
        contents: String,
        parentUID: String,
        nestedCommentCollection: String,
        commentName: String
    ) = flow {
        emit(commentRepository.postReply(contents, parentUID, nestedCommentCollection, commentName))
    }
}