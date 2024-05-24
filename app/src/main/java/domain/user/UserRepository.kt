package domain.user

import android.content.Context
import android.net.Uri
import data.model.Response
import model.LoginedUser

interface UserRepository {
    suspend fun updateProfile(nickName: String, profile: Uri): Response<Boolean>
    suspend fun getUserProfile(): LoginedUser
}