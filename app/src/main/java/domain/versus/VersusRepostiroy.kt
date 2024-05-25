package domain.versus

import data.model.Response
import data.model.VersusBoard

interface VersusRepostiroy {
    suspend fun postVersusBoard(
        boardTitle: String,
        opinion1: String,
        opinion2: String,
        writerUID: String
    ): Response<Boolean>

    suspend fun getRandomVersusBoard() : Response<VersusBoard>
    suspend fun voteOpinion(opinion1Vote : Boolean, versusBoardUID : String) : Response<Boolean>
}