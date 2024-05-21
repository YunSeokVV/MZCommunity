package domain.versus

import kotlinx.coroutines.flow.flow
import data.repository.versus.VersusRepostiroy
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

    suspend fun voteOpinion(opinion1Vote: Boolean, versusBoardUID : String) = flow{
        emit(versusRepository.voteOpinion(opinion1Vote, versusBoardUID))
    }
}