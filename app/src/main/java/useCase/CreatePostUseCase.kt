package useCase

import kotlinx.coroutines.flow.flow
import model.Images
import repository.BoardRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(private val boardRepository: BoardRepository) {
    suspend operator fun invoke(contents: String, uploadImagesUri: ArrayList<Images>) = flow {
        emit(boardRepository.postBoard(contents, uploadImagesUri))
    }

}