package data.repository.login

import android.content.Context
import com.orhanobut.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import data.model.LoginInfo
import data.model.Response
import database.LoginUserDB
import domain.login.LoginRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl @Inject constructor(@ApplicationContext private val appContext : Context) : LoginRepository {
    override suspend fun getSavedUserLoginInfo(): Response<LoginInfo> {
        return try{
            val response = LoginUserDB.getInstance(appContext).getEventsDao().getUserInfo()
            Response.success(response)
        } catch (e : Exception){
            Logger.v(e.message.toString())
            Response.Failure(e)
        }
    }
}