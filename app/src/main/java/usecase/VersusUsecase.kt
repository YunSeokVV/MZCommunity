package usecase

import kotlinx.coroutines.flow.flow
import model.Response
import repository.VersusRepostiroy
import javax.inject.Inject

class VersusUsecase @Inject constructor(private val versusRepository: VersusRepostiroy) {
    suspend fun postVersusBoard(
        boardTitle: String,
        opinion1: String,
        opinion2: String,
        writerUID: String
    ) = flow {
        emit(versusRepository.postVersusBoard(boardTitle, opinion1, opinion2, writerUID))
    }

    suspend fun getRandomVersusBoard(
    ) = flow {
        emit(versusRepository.getRandomVersusBoard())
    }
}