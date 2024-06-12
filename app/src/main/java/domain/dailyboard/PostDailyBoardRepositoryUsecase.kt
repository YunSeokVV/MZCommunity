package domain.dailyboard

import data.model.DailyBoardViewType
import data.model.File
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostDailyBoardRepositoryUsecase @Inject constructor(private val dailyBoardRepository: DailyBoardRepository){
    suspend fun postDailyBoardRepository(contents: String, uploadFileUri: List<File>, viewType : DailyBoardViewType) = flow {
        emit(dailyBoardRepository.postBoard(contents, uploadFileUri, viewType))
    }
}