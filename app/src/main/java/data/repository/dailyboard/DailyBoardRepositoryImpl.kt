package data.repository.dailyboard

import android.content.Context
import android.net.Uri
import com.example.mzcommunity.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import data.model.Response
import domain.dailyboard.DailyBoardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import data.model.DailyBoard
import data.model.DailyBoardViewType
import data.model.DailyboardCollection
import data.model.File
import data.model.UserFavourability
import util.FirebaseAuth
import util.Util.Companion.parsingDailyBoardFiles
import util.Util.Companion.getUnknownProfileImage
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

@Singleton
class DailyBoardRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStoreRef: FirebaseFirestore,
    @ApplicationContext private val appContext: Context
) :
    DailyBoardRepository {
    override suspend fun postBoard(
        contents: String,
        uploadFileUri: List<File>,
        viewType: DailyBoardViewType
    ): Response<Boolean> = withContext(Dispatchers.IO) {
        try {
            val fireStore = fireStoreRef
            val board = hashMapOf(
                "boardContents" to contents,
                "like" to 0,
                "disLike" to 0,
                "writerUID" to FirebaseAuth.auth.uid,
                "viewType" to viewType.ordinal
            )
            val urls = mutableListOf<String>()
            val documentReference = fireStore.collection("dailyBoard").add(board).await()

            uploadFileUri.mapIndexed { idx, it ->
                val choosenImg = storage.reference.child("board/${documentReference.id}/${idx}")
                choosenImg.putFile(Uri.parse(it.uri)).await()
                val imageUrls = choosenImg.downloadUrl.await().toString()
                urls.add(imageUrls)
            }

            fireStoreRef.collection("dailyBoard").document(documentReference.id)
                .update("fileURL", urls).await()

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }


    override suspend fun getRandomDailyBoards(): List<DailyBoard> = withContext(Dispatchers.IO) {
        var randomedDailyBoards = ArrayList<DailyBoard>()
        val result = fireStoreRef.collection("dailyBoard").get().await()
        result.documents.forEach {document->
            val dailyBoardCollection = getDailyBoardCollection(document)
            try {
                runBlocking {
                    withContext(Dispatchers.IO){
                        val userInfoSnapshot =
                            fireStoreRef.collection("MZUsers")
                                .document(dailyBoardCollection.writerUID).get().await()

                        val boardUID = document.id
                        val userNickName =
                            userInfoSnapshot.getString("nickName") ?: "알 수 없는 사용자"
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

                        randomedDailyBoards.add(dailyBoard)
                    }

                }
            } catch (e: Exception) {
                Logger.v(e.message.toString())
            }
        }

        randomedDailyBoards.shuffle()
        // 랜덤으로 6개의 일상 게시글을 추출한다
        randomedDailyBoards = ArrayList(randomedDailyBoards.subList(0, 6))
        Logger.v(randomedDailyBoards.toString())
        return@withContext randomedDailyBoards
    }

    override suspend fun getDailyBoard(documentId: String): DailyBoard =
        suspendCancellableCoroutine { continuation ->
            fireStoreRef.collection("dailyBoard").document(documentId).get()
                .addOnSuccessListener { result ->
                    val dailyBoardCollection = getDailyBoardCollection(result)

                    try {
                        runBlocking {
                            val userInfoSnapshot =
                                fireStoreRef.collection("MZUsers")
                                    .document(dailyBoardCollection.writerUID).get().await()

                            val boardUID = result.id
                            val userNickName =
                                userInfoSnapshot.getString("nickName") ?: "알 수 없는 사용자"
                            val boardContents = dailyBoardCollection.boardContents
                            val like = dailyBoardCollection.like
                            val disLike = dailyBoardCollection.disLike
                            val userFavourability = dailyBoardCollection.favourability
                            val favourStr = UserFavourability.fromValue(userFavourability)


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

                            continuation.resume(dailyBoard, null)
                        }
                    } catch (e: Exception) {
                        Logger.v(e.message.toString())
                        continuation.resumeWithException(Exception())
                    }

                }
        }

    override suspend fun increaseFavourability(
        dailyBoard: DailyBoard,
        isLike: Boolean
    ): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val fireStore = fireStoreRef
                val documentReference =
                    fireStore.collection("dailyBoard").document(dailyBoard.boardUID)

                // 사용자의 게시글에 대한 호감도가 보통이였던 경우
                if (dailyBoard.favourability == UserFavourability.USUAL) {
                    if (isLike) {
                        documentReference.update("like", FieldValue.increment(1)).await()
                        documentReference.set(setUserFavour("like"), SetOptions.merge()).await()
                    } else {
                        documentReference.update("disLike", FieldValue.increment(1)).await()
                        documentReference.set(setUserFavour("disLike"), SetOptions.merge()).await()
                    }


                    // 사용자의 게시글에 대한 호감도가 좋아요였던 경우
                } else if (dailyBoard.favourability == UserFavourability.LIKE) {
                    if (dailyBoard.like != 0)
                        documentReference.update("like", FieldValue.increment(-1)).await()

                    if (isLike) {
                        documentReference.set(setUserFavour("usual"), SetOptions.merge()).await()
                    } else {
                        documentReference.update("disLike", FieldValue.increment(1)).await()
                        documentReference.set(setUserFavour("disLike"), SetOptions.merge()).await()
                    }

                    // 사용자의 게시글에 대한 호감도가 싫어요였던 경우
                } else if (dailyBoard.favourability == UserFavourability.DISLIKE) {
                    if (isLike)
                        documentReference.update("like", FieldValue.increment(1)).await()
                    if (dailyBoard.disLike != 0)
                        documentReference.update("disLike", FieldValue.increment(-1)).await()
                    if (isLike)
                        documentReference.set(setUserFavour("like"), SetOptions.merge()).await()
                    else
                        documentReference.set(setUserFavour("usual"), SetOptions.merge()).await()
                }

                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }

    fun setUserFavour(userFavour: String): HashMap<String, HashMap<String?, String>> {
        val favour = hashMapOf(
            FirebaseAuth.auth.uid to userFavour
        )

        val userFavourability = hashMapOf(
            "userFavourability" to favour
        )

        return userFavourability
    }
    fun getDailyBoardCollection(document: DocumentSnapshot): DailyboardCollection {
        val boardContents = parsingFireStoreDocument(document, "boardContents")
        val disLike = parsingFireStoreDocument(document, "disLike").toInt()
        val like = parsingFireStoreDocument(document, "like").toInt()
        val writerUID = parsingFireStoreDocument(document, "writerUID")
        val favourability = parsingFireStoreDocument(document, "userFavourability")
        val files = parsingDailyBoardFiles(document, "fileURL")
        val viewTypeInt = parsingFireStoreDocument(document, "viewType").toInt()
        val viewType = DailyBoardViewType.fromValue(viewTypeInt) ?: DailyBoardViewType.TEXT

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