package useCase

import kotlinx.coroutines.flow.flow
import model.DailyBoard
import model.Images
import repository.DailyBoardRepository
import javax.inject.Inject

class DailyBoardUseCase @Inject constructor(private val dailyBoardRepository: DailyBoardRepository){
    suspend fun increaseDailyBoardLikeUseCase(dailyBoard: DailyBoard) = flow{
        emit(dailyBoardRepository.increaseDailyBoardLike(dailyBoard))
    }

    suspend fun increaseDailyBoardDisLike(dailyBoard: DailyBoard) = flow{
        emit(dailyBoardRepository.increaseDailyBoardDisLike(dailyBoard))
    }

    suspend fun getDailyBoards() = flow {
        emit(dailyBoardRepository.getDailyBoards())
    }

    suspend fun postDailyBoardRepository(contents: String, uploadImagesUri: ArrayList<Images>) = flow {
        emit(dailyBoardRepository.postBoard(contents, uploadImagesUri))
    }
}