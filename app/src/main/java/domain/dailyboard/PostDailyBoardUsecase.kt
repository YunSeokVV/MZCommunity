package domain.dailyboard

import data.model.DailyBoardViewType
import data.model.File
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostDailyBoardUsecase @Inject constructor(private val dailyBoardRepository: DailyBoardRepository){
    suspend operator fun invoke(contents: String, uploadFileUri: List<File>, viewType : DailyBoardViewType) = flow {
        emit(dailyBoardRepository.postBoard(contents, uploadFileUri, viewType))
    }
}