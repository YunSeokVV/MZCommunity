package repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import database.LoginUserDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.LoginInfo
import javax.inject.Inject
import javax.inject.Singleton
import model.Response

import util.FirebaseAuth

interface SignUpActivityRepository {
    suspend fun setUserNickname(nickName: String): Response<Boolean>

    suspend fun saveUserLoginInfo(nickName: String, passwd: String, context : Context): Response<Boolean>
}

@Singleton
class SignUpActivityRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    SignUpActivityRepository {
    override suspend fun setUserNickname(nickName: String): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val nickName = hashMapOf(
                    "nickName" to nickName
                )

                firestore.collection("MZUsers").document(FirebaseAuth.auth.uid.toString())
                    .set(nickName).addOnSuccessListener {
                }

                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }

    override suspend fun saveUserLoginInfo(nickName: String, passwd: String, context : Context): Response<Boolean> = withContext(Dispatchers.IO){
        try {
            val loginInfo = LoginInfo(0, nickName, passwd, "email")
            LoginUserDB.getInstance(context)?.getEventsDao()?.insertUserInfo(loginInfo)
            Response.Success(true)
        } catch (e : Exception){
            Response.Failure(e)
        }
    }


}