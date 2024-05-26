package data.repository.loading

import android.content.Context
import com.example.mzcommunity.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import domain.loading.LoadingRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import data.model.DailyBoard
import data.model.DailyBoardViewType
import data.model.DailyboardCollection
import data.model.LoginedUser
import data.model.UserFavourability
import util.FirebaseAuth
import util.Util
import util.Util.Companion.getUnknownProfileImage
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LoadingRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStoreRef: FirebaseFirestore,
    @ApplicationContext private val appContext: Context
) : LoadingRepository {


    override suspend fun getUserProfile(): LoginedUser {

        val snapShot =
            fireStoreRef.collection("MZUsers").document(FirebaseAuth.auth.uid.toString()).get()
                .await()

        val defaultProfile: String = getUnknownProfileImage(appContext)

        val profile = snapShot.getString("profileURL") ?: defaultProfile
        val nickName = snapShot.get("nickName") as? String ?: "알 수 없는 사용자"
        return LoginedUser(profile, nickName)
    }

    override suspend fun getDailyBoards(): List<DailyBoard> =
        suspendCancellableCoroutine { continuation ->
            var dailyBoards = ArrayList<DailyBoard>()

            try {
                fireStoreRef.collection("dailyBoard").limit(6).get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val dailyBoardCollection = getDailyBoardCollection(document)

                            runBlocking {
                                val userInfoSnapshot =
                                    fireStoreRef.collection("MZUsers")
                                        .document(dailyBoardCollection.writerUID).get().await()
                                val userNickName =
                                    userInfoSnapshot.getString("nickName") ?: "알 수 없는 사용자"
                                val boardUID = document.id

                                val boardContents = dailyBoardCollection.boardContents
                                val like = dailyBoardCollection.like
                                val disLike = dailyBoardCollection.disLike
                                val userFavourability = dailyBoardCollection.favourability
                                val viewType = dailyBoardCollection.viewType

                                val defaultProfile: String = getUnknownProfileImage(appContext)
                                val dailyBoard = DailyBoard(
                                    userNickName,
                                    userInfoSnapshot.getString("profileURL") ?: defaultProfile,
                                    boardContents,
                                    dailyBoardCollection.files,
                                    disLike,
                                    like,
                                    boardUID,
                                    userFavourability,
                                    viewType
                                )
                                dailyBoards.add(dailyBoard)
                            }

                        }
                        continuation.resume(dailyBoards, null)
                    }
            } catch (e: Exception) {
                Logger.v(e.message.toString())
            }

        }

    fun getDailyBoardCollection(document: DocumentSnapshot): DailyboardCollection {
        val boardContents = parsingFireStoreDocument(document, "boardContents")
        val disLike = parsingFireStoreDocument(document, "disLike").toInt()
        val like = parsingFireStoreDocument(document, "like").toInt()
        val writerUID = parsingFireStoreDocument(document, "writerUID")
        val favourability = parsingFireStoreDocument(document, "userFavourability")
        val files = Util.parsingDailyBoardFiles(document, "fileURL")
        val viewTypeInt = parsingFireStoreDocument(document, "viewType").toInt()
        val viewType = DailyBoardViewType.fromValue(viewTypeInt) ?: throw IllegalArgumentException("Invalid viewType value")

        return DailyboardCollection(
            boardContents,
            disLike,
            like,
            writerUID,
            UserFavourability.fromValue(favourability) ?: UserFavourability.USUAL,
            files,
            viewType
        )
    }

    private fun parsingFireStoreDocument(documentSnapshot: DocumentSnapshot, key: String): String {
        var result: String
        if (key == "disLike") {
            result = (documentSnapshot.get(key) as? Long ?: 0).toString()
        } else if (key == "like") {
            result = (documentSnapshot.get(key) as? Long ?: 0).toString()
        } else if (key == "writerUID") {
            result = documentSnapshot.get(key) as? String ?: "noWriterUID"
        } else if (key == "boardContents") {
            result = documentSnapshot.get(key) as? String ?: "noBoardContents"
        } else if (key == "userFavourability") {
            val userFavour = documentSnapshot.get(key) as? Map<String, Any>
            result = (userFavour?.get(FirebaseAuth.auth.uid.toString()) ?: "usual").toString()
        } else if (key == "viewType") {
            result = (documentSnapshot.get(key) as? Long ?: 0).toString()
        } else {
            result = documentSnapshot.get(key) as? String ?: appContext.getString(R.string.nothing)
        }
        return result
    }
}