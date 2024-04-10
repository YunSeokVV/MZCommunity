package repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Response
import util.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

interface VersusRepostiroy {
    suspend fun postVersusBoard(
        boardTitle: String,
        opinion1: String,
        opinion2: String,
        writerUID: String
    ): Response<Boolean>
}

@Singleton
class VersusRepostiroyImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : VersusRepostiroy {
    override suspend fun postVersusBoard(
        boardTitle: String,
        opinion1: String,
        opinion2: String,
        writerUID: String
    ): Response<Boolean> = withContext(Dispatchers.IO) {
        try {
            val versusBoard = hashMapOf(
                "boardTitle" to boardTitle,
                "opinion1" to opinion1,
                "opinion1VoteCount" to 0,
                "opinion2" to opinion2,
                "opinion2VoteCount" to 0,
                "writerUID" to FirebaseAuth.auth.uid
            )
            firestore.collection("versusBoard").add(versusBoard).await()
            Response.Success(true)
        } catch (e: Exception) {
            Logger.v(e.message.toString())
            Response.Failure(e)
        }
    }


}