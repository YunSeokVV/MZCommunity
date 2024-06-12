package domain.loading

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDailyBoardsUsecase @Inject constructor(private val loadingRepostiroy: LoadingRepository) {
    suspend operator fun invoke() = flow {
        emit(loadingRepostiroy.getDailyBoards())
    }
}