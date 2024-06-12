package domain.versus

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRandomVersusBoardUsecase @Inject constructor(private val versusRepository: VersusRepostiroy) {
    suspend operator fun invoke(
    ) = flow {
        emit(versusRepository.getRandomVersusBoard())
    }
}