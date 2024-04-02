package usecase

import kotlinx.coroutines.flow.flow
import model.Images
import repository.DailyCommentRepostiroy
import javax.inject.Inject

class DailyCommentUseCase @Inject constructor(private val dailyCommentRepostiroy: DailyCommentRepostiroy) {
    suspend fun postDailyCommentRepository(contents: String) = flow {
        emit(dailyCommentRepostiroy.postComment(contents))
    }
}