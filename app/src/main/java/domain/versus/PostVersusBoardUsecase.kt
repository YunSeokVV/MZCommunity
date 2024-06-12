package domain.versus

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostVersusBoardUsecase @Inject constructor(private val versusRepository: VersusRepostiroy) {
    suspend operator fun invoke(
        boardTitle: String,
        opinion1: String,
        opinion2: String,
        writerUID: String
    ) = flow {
        emit(versusRepository.postVersusBoard(boardTitle, opinion1, opinion2, writerUID))
    }
}