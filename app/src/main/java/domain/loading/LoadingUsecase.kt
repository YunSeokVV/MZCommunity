package domain.loading

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoadingUsecase @Inject constructor(private val loadingRepostiroy : LoadingRepository){

    suspend fun getUserProfile() = flow{
        emit(loadingRepostiroy.getUserProfile())
    }

    suspend fun getDailyBoards() = flow {
        emit(loadingRepostiroy.getDailyBoards())
    }
}