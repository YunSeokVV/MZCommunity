package usecase

import kotlinx.coroutines.flow.flow
import model.Images
import repository.DailyCommentRepostiroy
import javax.inject.Inject

class DailyCommentUseCase @Inject constructor(private val dailyCommentRepostiroy: DailyCommentRepostiroy) {
    suspend fun postDailyComment(contents: String, parentUID : String) = flow {
        emit(dailyCommentRepostiroy.postDailyComment(contents, parentUID))
    }

    suspend fun postReply(contents: String, parentUID: String) = flow{
        emit(dailyCommentRepostiroy.postReply(contents, parentUID))
    }

    suspend fun getDailyComments(parentUID : String) = flow {
        emit(dailyCommentRepostiroy.getDailyComments(parentUID))
    }

    suspend fun getNestedComments(parentUID: String) = flow{
        emit(dailyCommentRepostiroy.getNestedComments(parentUID))
    }
}