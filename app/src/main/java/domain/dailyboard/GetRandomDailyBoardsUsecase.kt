package domain.dailyboard

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRandomDailyBoardsUsecase @Inject constructor(private val dailyBoardRepository: DailyBoardRepository){
    suspend operator fun invoke() = flow {
        emit(dailyBoardRepository.getRandomDailyBoards())
    }
}