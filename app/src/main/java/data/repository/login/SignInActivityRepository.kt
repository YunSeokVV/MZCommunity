package data.repository.login

import android.content.Context
import com.orhanobut.logger.Logger
import data.model.Response
import database.LoginUserDB
import domain.login.SignInActivityRepository
import model.LoginInfo
import javax.inject.Inject
import javax.inject.Singleton




@Singleton
class SignInActivityRepositoryImpl @Inject constructor() : SignInActivityRepository {
    override suspend fun getSavedUserLoginInfo(context: Context): Response<LoginInfo> {
        return try{
            val response = LoginUserDB.getInstance(context).getEventsDao().getUserInfo()
            Response.success(response)
        } catch (e : Exception){
            Logger.v(e.message.toString())
            Response.Failure(e)
        }
    }

}