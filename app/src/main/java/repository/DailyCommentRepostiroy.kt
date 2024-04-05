package repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Comment
import model.Response
import util.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

interface DailyCommentRepostiroy {

    suspend fun postDailyComment(contents: String, parentUID : String): Response<Boolean>

    suspend fun postReply(contents: String, parentUID: String) : Response<Boolean>

    suspend fun getDailyComments(parentUID : String): List<Comment>
}

@Singleton
class DailyCommentRepostiroyImpl @Inject constructor(
    private val fireStoreRef: FirebaseFirestore,
    private val storage: FirebaseStorage,
) :
    DailyCommentRepostiroy {
    override suspend fun postDailyComment(contents: String, parentUID : String): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val fireStore = fireStoreRef
                val dailyBoardComment = hashMapOf(
                    "commentContents" to contents,
                    "writerUID" to FirebaseAuth.auth.uid,
                    "parentUID" to parentUID
                )

                fireStore.collection("dailyBoardComment").add(dailyBoardComment)
                    .addOnSuccessListener {
                        Response.Success(true)
                    }.addOnFailureListener {
                        Response.Success(false)
                    }.await()

                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }

    override suspend fun postReply(contents: String, parentUID: String): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val fireStore = fireStoreRef
                val dailyBoardComment = hashMapOf(
                    "writerUID" to FirebaseAuth.auth.uid,
                    "commentContents" to contents,
                    "parentUID" to parentUID
                )

                fireStore.collection("nestedComment").add(dailyBoardComment)
                    .addOnSuccessListener {
                        Response.Success(true)
                    }.addOnFailureListener {
                        Response.Success(false)
                    }.await()

                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }

    override suspend fun getDailyComments(parentUID : String): List<Comment> = withContext(Dispatchers.IO) {
        var comments = mutableListOf<Comment>()
        val fireStore = fireStoreRef
        try {
            fireStore.collection("dailyBoardComment").whereEqualTo("parentUID", parentUID).get().addOnSuccessListener {documents ->
                documents.forEach {
                    runBlocking {
                        val profile: Uri = storage.reference.child("user_profile_image/" + it.get("writerUID") + ".jpg").downloadUrl.await()
                        val userDoc = fireStore.collection("MZUsers").document(it.get("writerUID").toString()).get().await()
                        val nickName = userDoc.get("nickName").toString() ?: "알 수 없는 사용자"
                        val comment = Comment(profile, nickName, it.get("commentContents").toString())
                        comments.add(comment)
                    }
                }
            }.await()


        } catch (e : Exception){
            Logger.v(e.message.toString())
        }
        return@withContext comments
    }
}