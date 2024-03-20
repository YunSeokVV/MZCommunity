package useCase

import kotlinx.coroutines.flow.flow
import model.Images
import repository.GetDailyBoardRepositry
import javax.inject.Inject


class GetDailyBoardUseCase @Inject constructor(private val getDailyBoardRepositry: GetDailyBoardRepositry) {

    suspend operator fun invoke() = flow {
        emit(getDailyBoardRepositry.getDailyBoard())
    }

}