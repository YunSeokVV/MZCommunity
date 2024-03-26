package useCase

import kotlinx.coroutines.flow.flow
import model.Images
import repository.DailyBoardRepository
import javax.inject.Inject

class DailyBoardUseCase @Inject constructor(private val dailyBoardRepository: DailyBoardRepository){
    suspend fun getDailyBoard() = flow {
        emit(dailyBoardRepository.getDailyBoard())
    }

    suspend fun postDailyBoardRepository(contents: String, uploadImagesUri: ArrayList<Images>) = flow {
        emit(dailyBoardRepository.postBoard(contents, uploadImagesUri))
    }
}