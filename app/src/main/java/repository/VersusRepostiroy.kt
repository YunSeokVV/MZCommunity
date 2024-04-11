package repository

import android.net.Uri
import com.example.mzcommunity.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.DailyBoard
import model.Response
import model.VersusBoard
import util.FirebaseAuth
import util.Util
import javax.inject.Inject
import javax.inject.Singleton

interface VersusRepostiroy {
    suspend fun postVersusBoard(
        boardTitle: String,
        opinion1: String,
        opinion2: String,
        writerUID: String
    ): Response<Boolean>

    suspend fun getRandomVersusBoard() : VersusBoard
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

    override suspend fun getRandomVersusBoard() : VersusBoard = withContext(Dispatchers.IO){
        var versusBoard : VersusBoard
        try {
            val snapshot = firestore.collection("versusBoard").get().await()
            val document = snapshot.documents[Util.getRanNum(snapshot.size())]
            val writerUID : String = document.get("writerUID") as String
            val writerDocu = firestore.collection("MZUsers").document(writerUID).get().await()

            val writerProfileUri = storage.reference.child("user_profile_image/$writerUID.jpg").downloadUrl.await()
            val writerName = writerDocu.get("nickName") as? String ?: "알 수 없는 사용자"
            val boardTitle = document.get("boardTitle") as? String ?: "주제"
            val opinion1 = document.get("opinion1") as? String ?: "의견1"
            val opinion1VoteCount = document.get("opinion1VoteCount") as? Long ?: 0
            val opinion2 = document.get("opinion2") as? String ?: "의견2"
            val opinion2VoteCount = document.get("opinion2VoteCount") as? Long ?: 0
            val userVoted = document.get(writerUID) as? Boolean ?: false
            versusBoard = VersusBoard(writerProfileUri,writerName,boardTitle,opinion1,opinion1VoteCount, opinion2,opinion2VoteCount,document.id,userVoted)

        } catch (e : Exception){
            Logger.v(e.message.toString())
            versusBoard = VersusBoard(Uri.parse("nothing"),"알 수 없는 사용자","주제","의견1",0, "의견2",0,"unKnown",false)
        }
        return@withContext versusBoard
    }

}