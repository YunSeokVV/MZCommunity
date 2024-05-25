package data.repository.versus

import android.content.Context
import com.example.mzcommunity.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import data.model.Response
import domain.versus.VersusRepostiroy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import data.model.VersusBoard
import util.FirebaseAuth
import util.Util
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class VersusRepostiroyImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    @ApplicationContext private val appContext: Context
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


    override suspend fun getRandomVersusBoard() : Response<VersusBoard>{

        return try {
            val snapshot = firestore.collection("versusBoard").get().await()

            val notVotedList = snapshot.documents.mapNotNull{item ->
                val userVoted = item.get("votedUser") as? Map<String, Any>
                val voted = userVoted?.get(FirebaseAuth.auth.uid) as? Boolean ?: false
                // 사용자가 이전에 투표하지 않은 질문만 보여준다.
                if(!voted){
                    item
                } else{
                    null
                }
            }

            val document = notVotedList[getRanNum(notVotedList.size)]
            val writerUID : String = document.getString("writerUID") ?: appContext.getString(R.string.nothing)
            val writerDocu = firestore.collection("MZUsers").document(writerUID).get().await()

            val defaultProfile: String = Util.getUnknownProfileImage(appContext)

            val nickName = writerDocu.getString("nickName") ?: "알 수 없는 사용자"
            val profileURL = writerDocu.getString("profileURL") ?: defaultProfile
            val boardTitle = document.get("boardTitle") as? String ?: "주제"
            val opinion1 = document.get("opinion1") as? String ?: "의견1"
            val opinion1VoteCount = document.get("opinion1VoteCount") as? Long ?: 0
            val opinion2 = document.get("opinion2") as? String ?: "의견2"
            val opinion2VoteCount = document.get("opinion2VoteCount") as? Long ?: 0

            val userVoted = document.get(writerUID) as? Boolean ?: false
            val versusBoard = VersusBoard(profileURL,nickName,boardTitle,opinion1,opinion1VoteCount, opinion2,opinion2VoteCount,document.id,userVoted)
            Response.Success(versusBoard)
        } catch (e : Exception){
            Logger.v(e.message.toString())
            Response.Failure(e)
        }
    }

    override suspend fun voteOpinion(opinion1Vote: Boolean, versusBoardUID : String): Response<Boolean> = withContext(Dispatchers.IO) {
        try {
            val favour = hashMapOf(
                FirebaseAuth.auth.uid to true
            )

            val userUID = hashMapOf(
                "votedUser" to favour
            )
            val documentReference = firestore.collection("versusBoard").document(versusBoardUID)
            val updateOpinion = if(opinion1Vote) "opinion1VoteCount" else "opinion2VoteCount"
            documentReference.update(updateOpinion, FieldValue.increment(1)).await()
            documentReference.set(userUID, SetOptions.merge()).await()


            Response.Success(true)
        } catch (e: Exception) {
            Logger.v(e.message.toString())
            Response.Failure(e)
        }
    }

    private fun getRanNum(ranNum: Int): Int {
        // 0~ranNum 사이의 랜덤 숫자
        val randomNum = Random().nextInt(ranNum)
        return randomNum
    }

}