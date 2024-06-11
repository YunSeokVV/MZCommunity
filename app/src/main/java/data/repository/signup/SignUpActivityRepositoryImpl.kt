package data.repository.signup

import android.content.Context
import com.example.mzcommunity.R
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import data.model.Response
import database.LoginUserDB
import domain.signup.SignUpActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import data.model.LoginInfo
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

import util.FirebaseAuth


@Singleton
class SignUpActivityRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val appContext: Context
) :
    SignUpActivityRepository {
    override suspend fun setUserNickname(nickName: String): Response<Boolean> =
        withContext(Dispatchers.IO) {
            runBlocking {
                try {
                    val nickName = hashMapOf(
                        "nickName" to nickName
                    )

                    val existingNickname =
                        firestore.collection("MZUsers").document(FirebaseAuth.auth.uid.toString())
                            .get().await()
                    val result = existingNickname.getString("nickName")
                        ?: appContext.getString(R.string.unknown_user)

                    if (result == appContext.getString(R.string.unknown_user)) {
                        firestore.collection("MZUsers").document(FirebaseAuth.auth.uid.toString())
                            .set(nickName).addOnSuccessListener {
                            }
                    }

                    Response.Success(true)
                } catch (e: Exception) {
                    Response.Failure(e)
                }
            }

        }

    override suspend fun saveUserLoginInfo(nickName: String, passwd: String): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val loginInfo = LoginInfo(0, nickName, passwd, "email")
                LoginUserDB.getInstance(appContext)?.getEventsDao()?.insertUserInfo(loginInfo)
                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }


}