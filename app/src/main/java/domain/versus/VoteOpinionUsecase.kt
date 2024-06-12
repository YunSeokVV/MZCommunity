package domain.versus

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VoteOpinionUsecase @Inject constructor(private val versusRepository: VersusRepostiroy) {
    suspend operator fun invoke(opinion1Vote: Boolean, versusBoardUID : String) = flow{
        emit(versusRepository.voteOpinion(opinion1Vote, versusBoardUID))
    }
}