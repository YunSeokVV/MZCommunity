package domain.dailyboard

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDailyBoardUsecase @Inject constructor(private val dailyBoardRepository: DailyBoardRepository){
    suspend operator fun invoke(documentId : String) = flow {
        emit(dailyBoardRepository.getDailyBoard(documentId))
    }
}