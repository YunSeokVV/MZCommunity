package usecase

import kotlinx.coroutines.flow.flow
import model.DailyBoard
import model.File
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

    suspend fun getDailyBoard(documentId : String) = flow {
        emit(dailyBoardRepository.getDailyBoard(documentId))
    }

    suspend fun postDailyBoardRepository(contents: String, uploadFileUri: List<File>) = flow {
        emit(dailyBoardRepository.postBoard(contents, uploadFileUri))
    }
}