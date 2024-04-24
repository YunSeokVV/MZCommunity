package repository

import android.net.Uri

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Response
import model.User
import util.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun updateProfile(nickName: String, profile: Uri): Response<Boolean>
    suspend fun getUserProfile(): User
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStoreRef: FirebaseFirestore
) : UserRepository {


    override suspend fun updateProfile(nickName: String, profile: Uri) =
        withContext(Dispatchers.IO) {
            try {
                Logger.v(nickName)
                val choosenImg =
                    storage.reference.child("user_profile_image/" + FirebaseAuth.auth.uid.toString() + ".jpg")
                choosenImg.putFile(profile).await()

                fireStoreRef.collection("MZUsers").document(FirebaseAuth.auth.uid.toString())
                    .update(
                        "nickName",nickName
                    ).await()
                Response.Success(true)
            } catch (e: Exception) {
                Logger.v(e.message.toString())
                Response.Failure(e)
            }
        }

    override suspend fun getUserProfile(): User {
        val profile: Uri =
            storage.reference.child("user_profile_image/" + FirebaseAuth.auth.uid.toString() + ".jpg").downloadUrl.await()

        val snapShot = fireStoreRef.collection("MZUsers").document(FirebaseAuth.auth.uid.toString()).get().await()
        val nickName = snapShot.get("nickName") as? String ?: "알 수 없는 사용자"
        val user = User(profile, nickName)
        return user
    }
}