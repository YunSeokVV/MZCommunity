package domain.comment

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostCommentUsecase @Inject constructor(private val commentRepository: CommentRepository) {
    suspend operator fun invoke(contents: String, parentUID: String, collectionName: String) = flow {
        emit(commentRepository.postComment(contents, parentUID, collectionName))
    }
}