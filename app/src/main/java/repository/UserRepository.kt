package repository

import android.content.Context
import android.net.Uri
import com.example.mzcommunity.R

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Response
import model.LoginedUser
import util.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun updateProfile(nickName: String, profile: Uri): Response<Boolean>
    suspend fun getUserProfile(context: Context): LoginedUser
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStoreRef: FirebaseFirestore
) : UserRepository {
    override suspend fun updateProfile(nickName: String, profile: Uri) =
        withContext(Dispatchers.IO) {
            runBlocking {
                try {
                    val choosenImg =
                        storage.reference.child("user_profile_image/" + FirebaseAuth.auth.uid.toString() + ".jpg")
                    val uploadTask = choosenImg.putFile(profile).await()
                    val downUrl = uploadTask.storage.downloadUrl.await()
                    val userProfileUrl = downUrl.toString()

                    fireStoreRef.collection("MZUsers").document(FirebaseAuth.auth.uid.toString())
                        .update("nickName",nickName).await()

                    fireStoreRef.collection("MZUsers").document(FirebaseAuth.auth.uid.toString())
                        .update("profileURL",userProfileUrl).await()
                    Response.Success(true)
                } catch (e: Exception) {
                    Logger.v(e.message.toString())
                    Response.Failure(e)
                }
            }

        }

    override suspend fun getUserProfile(context: Context): LoginedUser {

        val profile: Uri = try {
            storage.reference.child("user_profile_image/" + FirebaseAuth.auth.uid.toString() + ".jpg").downloadUrl.await()
        } catch (e: Exception) {
            Logger.v(e.message.toString())
            Uri.parse("android.resource://" + context.packageName + "/" + R.drawable.user_profile2)
        }

        val snapShot =
            fireStoreRef.collection("MZUsers").document(FirebaseAuth.auth.uid.toString()).get()
                .await()
        val nickName = snapShot.get("nickName") as? String ?: "알 수 없는 사용자"
        val loginedUser = LoginedUser(profile.toString(), nickName)
        return loginedUser
    }
}