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
import model.DailyBoard
import model.DailyboardCollection
import model.LoginedUser
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

    private fun getDailyBoardCollection(result: DocumentSnapshot): DailyboardCollection {
        return DailyboardCollection(
            parsingFireStoreDocument(result, "boardContents"),
            parsingFireStoreDocument(result, "disLike").toInt(),
            parsingFireStoreDocument(result, "like").toInt(),
            parsingFireStoreDocument(result, "writerUID"),
            parsingFireStoreDocument(result, "userFavourability"),
            Util.parsingDailyBoardFiles(result, "fileURL"),
            parsingFireStoreDocument(result, "viewType").toInt()
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