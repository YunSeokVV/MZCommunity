package useCase

import kotlinx.coroutines.flow.flow
import model.Images
import repository.PostDailyBoardRepository
import javax.inject.Inject

class PostDailyBoardUseCase @Inject constructor(private val postDailyBoardRepository: PostDailyBoardRepository) {
    suspend operator fun invoke(contents: String, uploadImagesUri: ArrayList<Images>) = flow {
        emit(postDailyBoardRepository.postBoard(contents, uploadImagesUri))
    }

}