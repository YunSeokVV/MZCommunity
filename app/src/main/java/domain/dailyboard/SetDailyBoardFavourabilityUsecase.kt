package domain.dailyboard

import data.model.DailyBoard
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SetDailyBoardFavourabilityUsecase @Inject constructor(private val dailyBoardRepository: DailyBoardRepository){
    suspend operator fun invoke(dailyBoard: DailyBoard, isLike : Boolean) = flow{
        emit(dailyBoardRepository.increaseFavourability(dailyBoard, isLike))
    }
}