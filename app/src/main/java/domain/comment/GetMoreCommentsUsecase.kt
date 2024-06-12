package domain.comment

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMoreCommentsUsecase @Inject constructor(private val commentRepository: CommentRepository) {
    suspend operator fun invoke(parentUID: String, collectionName: String) =
        flow {
            emit(commentRepository.getMoreComments(parentUID, collectionName))
        }
}