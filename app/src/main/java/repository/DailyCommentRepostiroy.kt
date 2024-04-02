package repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Response
import util.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

interface DailyCommentRepostiroy {

    suspend fun postComment(contents: String): Response<Boolean>
}

@Singleton
class DailyCommentRepostiroyImpl @Inject constructor(private val fireStoreRef: FirebaseFirestore) :
    DailyCommentRepostiroy {
    override suspend fun postComment(contents: String): Response<Boolean> = withContext(Dispatchers.IO) {
        try {
            val fireStore = fireStoreRef
            val dailyBoardComment = hashMapOf(
                "commentContents" to contents,
                "writerUID" to FirebaseAuth.auth.uid
            )

            fireStore.collection("dailyBoardComment").add(dailyBoardComment).addOnSuccessListener {
                Response.Success(true)
            }.addOnFailureListener {
                Response.Success(false)
            }.await()

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }
}