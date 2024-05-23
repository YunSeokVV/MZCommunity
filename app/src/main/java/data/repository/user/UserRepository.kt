package data.repository.user

import android.content.Context
import android.net.Uri

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import data.model.Response
import domain.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.LoginedUser
import util.FirebaseAuth
import util.Util
import javax.inject.Inject
import javax.inject.Singleton



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

        val snapShot =
            fireStoreRef.collection("MZUsers").document(FirebaseAuth.auth.uid.toString()).get()
                .await()

        val defaultProfile: String = Util.getUnknownProfileImage()
        val profile = snapShot.getString("profileURL") ?: defaultProfile
        val nickName = snapShot.get("nickName") as? String ?: "알 수 없는 사용자"
        return LoginedUser(profile, nickName)
    }
}