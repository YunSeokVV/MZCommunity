package domain.comment

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetNestedCommentsUsecase @Inject constructor(private val commentRepository: CommentRepository) {
    suspend operator fun invoke(parentUID: String, nestedCommentName: String) = flow {
        emit(commentRepository.getNestedComments(parentUID, nestedCommentName))
    }
}