package domain.dailyboard

import kotlinx.coroutines.flow.flow
import data.model.DailyBoard
import data.model.DailyBoardViewType
import data.model.File
import javax.inject.Inject

class DailyBoardUseCase @Inject constructor(private val dailyBoardRepository: DailyBoardRepository){
    suspend fun increaseFavourability(dailyBoard: DailyBoard, isLike : Boolean) = flow{
        emit(dailyBoardRepository.increaseFavourability(dailyBoard, isLike))
    }

    suspend fun getRandomDailyBoards() = flow {
        emit(dailyBoardRepository.getRandomDailyBoards())
    }

    suspend fun getDailyBoard(documentId : String) = flow {
        emit(dailyBoardRepository.getDailyBoard(documentId))
    }

    suspend fun postDailyBoardRepository(contents: String, uploadFileUri: List<File>, viewType : DailyBoardViewType) = flow {
        emit(dailyBoardRepository.postBoard(contents, uploadFileUri, viewType))
    }
}