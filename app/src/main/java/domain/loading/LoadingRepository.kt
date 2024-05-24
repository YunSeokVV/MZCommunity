package domain.loading

import android.content.Context
import model.DailyBoard
import model.LoginedUser

interface LoadingRepository {
    suspend fun getUserProfile(): LoginedUser

    suspend fun getDailyBoards(): List<DailyBoard>
}