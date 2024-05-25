package domain.login

import data.model.Response
import data.model.LoginInfo

interface SignInActivityRepository {
    suspend fun getSavedUserLoginInfo(): Response<LoginInfo>
}