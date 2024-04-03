package repository

import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import model.Response
import model.Response.Failure
import model.Response.Success
import util.FirebaseAuth

interface LoginActivityRepository {
    suspend fun setUserNickname(nickName: String): Response<Boolean>
}

@Singleton
class LoginActivityRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    LoginActivityRepository {
    override suspend fun setUserNickname(nickName: String): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val nickName = hashMapOf(
                    "nickName" to nickName
                )

                firestore.collection("MZUsers").document(FirebaseAuth.auth.uid.toString()).set(nickName).addOnSuccessListener {
                }

                Success(true)
            } catch (e: Exception) {
                Failure(e)
            }
        }

}