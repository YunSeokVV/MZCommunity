package domain.signup

import data.model.Response

interface SignUpRepository {
    suspend fun setUserNickname(nickName: String): Response<Boolean>
    suspend fun saveUserLoginInfo(nickName: String, passwd: String): Response<Boolean>
}