package domain.loading

import data.model.DailyBoard
import data.model.LoginedUser

interface LoadingRepository {
    suspend fun getUserProfile(): LoginedUser

    suspend fun getDailyBoards(): List<DailyBoard>
}