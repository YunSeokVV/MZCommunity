package domain.loading

import android.content.Context
import model.DailyBoard
import model.LoginedUser

interface LoadingRepository {
    suspend fun getUserProfile(context: Context): LoginedUser

    suspend fun getDailyBoards(): List<DailyBoard>
}