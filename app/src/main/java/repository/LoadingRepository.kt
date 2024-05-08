package repository

import android.content.Context
import android.net.Uri
import com.example.mzcommunity.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import model.DailyBoard
import model.DailyboardCollection
import model.LoginedUser
import util.FirebaseAuth
import util.Util
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

interface LoadingRepository{
    suspend fun getUserProfile(context : Context): LoginedUser

    suspend fun getDailyBoards(): List<DailyBoard>
}

@Singleton
class LoadingRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStoreRef: FirebaseFirestore
) : LoadingRepository{


    override suspend fun getUserProfile(context : Context): LoginedUser {

        val profile : Uri = try{
            storage.reference.child("user_profile_image/" + FirebaseAuth.auth.uid.toString() + ".jpg").downloadUrl.await()
        } catch (e :Exception){
            Logger.v(e.message.toString())
            Uri.parse("android.resource://" + context.packageName + "/" + R.drawable.user_profile2)
        }

        val snapShot = fireStoreRef.collection("MZUsers").document(FirebaseAuth.auth.uid.toString()).get().await()
        val nickName = snapShot.get("nickName") as? String ?: "알 수 없는 사용자"
        val loginedUser = LoginedUser(profile, nickName)
        return loginedUser
    }

    override suspend fun getDailyBoards(): List<DailyBoard> = suspendCancellableCoroutine { continuation ->
        var dailyBoards = ArrayList<DailyBoard>()

        fireStoreRef.collection("dailyBoard").get().addOnSuccessListener { result ->
            for (document in result) {
                val dailyBoardCollection = getDailyBoardCollection(document)

                try {
                    runBlocking {
                        val userInfoSnapshot =
                            fireStoreRef.collection("MZUsers")
                                .document(dailyBoardCollection.writerUID).get().await()

                        val files = dailyBoardCollection.files.map {
                            Uri.parse(it)
                        }

                        val boardUID = document.id
                        val userProfile = Uri.parse(userInfoSnapshot.get("profileURL").toString())
                        val userNickName = userInfoSnapshot.get("nickName").toString()
                        val boardContents = dailyBoardCollection.boardContents
                        val like = dailyBoardCollection.like
                        val disLike = dailyBoardCollection.disLike
                        val userFavourability = dailyBoardCollection.favourability
                        val viewType = dailyBoardCollection.viewType

                        val dailyBoard = DailyBoard(
                            userProfile,
                            userNickName,
                            boardContents,
                            files,
                            disLike,
                            like,
                            boardUID,
                            userFavourability,
                            viewType
                        )

                        dailyBoards.add(dailyBoard)
                        continuation.resume(dailyBoards, null)
                    }
                } catch (e: Exception) {
                    Logger.v(e.message.toString())
                    if(continuation.isActive)
                        continuation.resumeWithException(Exception())
                }
            }
        }
    }

    private fun getDailyBoardCollection(result: DocumentSnapshot): DailyboardCollection {
        return DailyboardCollection(
            Util.parsingFireStoreDocument(result, "boardContents"),
            Util.parsingFireStoreDocument(result, "disLike").toInt(),
            Util.parsingFireStoreDocument(result, "like").toInt(),
            Util.parsingFireStoreDocument(result, "writerUID"),
            Util.parsingFireStoreDocument(result, "userFavourability"),
            Util.parsingDailyBoardFiles(result, "fileURL"),
            Util.parsingFireStoreDocument(result, "viewType").toInt()
        )
    }
}