package domain.user

import android.net.Uri
import data.model.Response
import data.model.LoginedUser

interface UserRepository {
    suspend fun updateProfile(nickName: String, profile: Uri): Response<Boolean>
    suspend fun getUserProfile(): LoginedUser
}