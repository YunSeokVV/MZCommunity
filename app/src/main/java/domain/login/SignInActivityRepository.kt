package domain.login

import android.content.Context
import data.model.Response
import model.LoginInfo

interface SignInActivityRepository {
    suspend fun getSavedUserLoginInfo(context: Context): Response<LoginInfo>
}