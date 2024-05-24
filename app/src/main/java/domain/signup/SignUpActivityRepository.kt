package domain.signup

import android.content.Context
import data.model.Response

interface SignUpActivityRepository {
    suspend fun setUserNickname(nickName: String): Response<Boolean>

    suspend fun saveUserLoginInfo(nickName: String, passwd: String): Response<Boolean>
}