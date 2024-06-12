package domain.login

import data.model.LoginInfo
import data.model.Response

interface LoginRepository {
    suspend fun getSavedUserLoginInfo(): Response<LoginInfo>
}