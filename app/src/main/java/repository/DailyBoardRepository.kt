package repository

import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.DailyBoard
import model.DailyboardCollection
import model.Images
import model.Response
import util.FirebaseAuth
import util.Util
import javax.inject.Inject
import javax.inject.Singleton

interface DailyBoardRepository {
    suspend fun postBoard(contents: String, uploadImagesUri: ArrayList<Images>): Response<Boolean>
    suspend fun getDailyBoards(): List<DailyBoard>

    suspend fun getDailyBoard(documentId: String): DailyBoard

    suspend fun increaseDailyBoardLike(dailyBoard: DailyBoard): Response<Boolean>
    suspend fun increaseDailyBoardDisLike(dailyBoard: DailyBoard): Response<Boolean>
}

@Singleton
class DailyBoardRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStoreRef: FirebaseFirestore
) :
    DailyBoardRepository {
    override suspend fun postBoard(
        contents: String,
        uploadImagesUri: ArrayList<Images>
    ): Response<Boolean> = withContext(Dispatchers.IO) {
        try {
            val fireStore = fireStoreRef
            val board = hashMapOf(
                "boardContents" to contents,
                "like" to 0,
                "disLike" to 0,
                "writerUID" to FirebaseAuth.auth.uid
            )
            val documentReference = fireStore.collection("dailyBoard").add(board).await()
            val uploadTasks = uploadImagesUri.mapIndexed { idx, it ->
                val choosenImg =
                    storage.reference.child("board/${documentReference.id}/${idx}.png")
                choosenImg.putFile(Uri.parse(it.uri)).await()
            }
            // 모든 이미지 업로드 작업이 성공적으로 완료되었을 경우
            if (uploadTasks.all { it.task.isSuccessful }) {
                Response.Success(true)
            } else {
                Response.Success(false)
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getDailyBoards(): List<DailyBoard> = withContext(Dispatchers.IO) {
        var dailyBoards = ArrayList<DailyBoard>()

        fireStoreRef.collection("dailyBoard").get().addOnSuccessListener { result ->
            for (document in result) {
                val dailyBoardCollection = DailyboardCollection(
                    Util.parsingFireStoreDocument(document, "boardContents"),
                    Util.parsingFireStoreDocument(document, "disLike").toInt(),
                    Util.parsingFireStoreDocument(document, "like").toInt(),
                    Util.parsingFireStoreDocument(document, "writerUID"),
                    Util.parsingFireStoreDocument(document, "userFavourability"),
                )

                try {
                    runBlocking {
                        val userNicknameSnapshot = async {
                            fireStoreRef.collection("MZUsers")
                                .document(dailyBoardCollection.writerUID).get().await()
                        }.await()

                        val writerProfileUri = async {
                            storage.reference.child("user_profile_image/" + dailyBoardCollection.writerUID + ".jpg").downloadUrl.await()
                        }.await()

                        val boardImagesSnapshot = async {
                            storage.reference.child("board/${document.id}").listAll().await()
                        }.await()

                        val boardImages = boardImagesSnapshot.items.map {
                            it.downloadUrl.await()
                        }

                        val boardUID = async {
                            document.id
                        }.await()

                        val userNickName = userNicknameSnapshot.get("nickName").toString()
                        val boardContents = dailyBoardCollection.boardContents
                        val like = dailyBoardCollection.like
                        val disLike = dailyBoardCollection.disLike
                        val userFavourability = dailyBoardCollection.favourability


                        val dailyBoard = DailyBoard(
                            writerProfileUri,
                            userNickName,
                            boardContents,
                            boardImages,
                            disLike,
                            like,
                            boardUID,
                            userFavourability
                        )

                        dailyBoards.add(dailyBoard)

                    }
                } catch (e: Exception) {
                    Logger.v(e.message.toString())
                }


            }
        }.await()

        return@withContext dailyBoards
    }

    override suspend fun getDailyBoard(documentId: String): DailyBoard =
        withContext(Dispatchers.IO) {
            var dailyBoard: DailyBoard
            try {
                val document =
                    fireStoreRef.collection("dailyBoard").document(documentId).get().await()
                val dailyBoardCollection = DailyboardCollection(
                    Util.parsingFireStoreDocument(document, "boardContents"),
                    Util.parsingFireStoreDocument(document, "disLike").toInt(),
                    Util.parsingFireStoreDocument(document, "like").toInt(),
                    Util.parsingFireStoreDocument(document, "writerUID"),
                    Util.parsingFireStoreDocument(document, "userFavourability"),
                )

                val userNicknameSnapshot =
                    fireStoreRef.collection("MZUsers").document(dailyBoardCollection.writerUID)
                        .get().await()
                val writerProfileUri =
                    storage.reference.child("user_profile_image/" + dailyBoardCollection.writerUID + ".jpg").downloadUrl.await()
                val boardImagesSnapshot =
                    storage.reference.child("board/${document.id}").listAll().await()
                val boardImages = boardImagesSnapshot.items.map {
                    it.downloadUrl.await()
                }

                val boardUID = document.id
                val userNickName = userNicknameSnapshot.get("nickName").toString()
                val boardContents = dailyBoardCollection.boardContents
                val like = dailyBoardCollection.like
                val disLike = dailyBoardCollection.disLike
                val userFavourability = dailyBoardCollection.favourability


                dailyBoard = DailyBoard(
                    writerProfileUri,
                    userNickName,
                    boardContents,
                    boardImages,
                    disLike,
                    like,
                    boardUID,
                    userFavourability
                )


            } catch (e: Exception) {
                Logger.v(e.message.toString())
                dailyBoard = DailyBoard(Uri.parse("nothing"),"nothing","nothing",
                    emptyList(),0,0,"nothing","nothing")
            }

            return@withContext dailyBoard
        }

    override suspend fun increaseDailyBoardLike(dailyBoard: DailyBoard): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val fireStore = fireStoreRef
                val documentReference =
                    fireStore.collection("dailyBoard").document(dailyBoard.boardUID)

                // 사용자의 게시글에 대한 호감도가 보통이였던 경우
                if (dailyBoard.favourability.equals("usual")) {
                    documentReference.update("like", FieldValue.increment(1)).await()
                    documentReference.set(setUserFavour("like"), SetOptions.merge()).await()

                    // 사용자의 게시글에 대한 호감도가 좋아요였던 경우
                } else if (dailyBoard.favourability.equals("like")) {
                    if (dailyBoard.like != 0)
                        documentReference.update("like", FieldValue.increment(-1)).await()
                    documentReference.set(setUserFavour("usual"), SetOptions.merge()).await()

                    // 사용자의 게시글에 대한 호감도가 싫어요였던 경우
                } else if (dailyBoard.favourability.equals("disLike")) {
                    documentReference.update("like", FieldValue.increment(1)).await()
                    if (dailyBoard.disLike != 0)
                        documentReference.update("disLike", FieldValue.increment(-1)).await()
                    documentReference.set(setUserFavour("like"), SetOptions.merge()).await()
                }

                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }

    override suspend fun increaseDailyBoardDisLike(dailyBoard: DailyBoard): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val fireStore = fireStoreRef
                val documentReference =
                    fireStore.collection("dailyBoard").document(dailyBoard.boardUID)

                // 사용자의 게시글에 대한 호감도가 보통이였던 경우
                if (dailyBoard.favourability.equals("usual")) {
                    documentReference.update("disLike", FieldValue.increment(1)).await()
                    documentReference.set(setUserFavour("disLike"), SetOptions.merge()).await()

                    Response.Success(true)
                }
                // 사용자의 게시글에 대한 호감도가 좋아요였던 경우
                else if (dailyBoard.favourability.equals("like")) {
                    if (dailyBoard.like != 0)
                        documentReference.update("like", FieldValue.increment(-1)).await()
                    documentReference.update("disLike", FieldValue.increment(1)).await()
                    documentReference.set(setUserFavour("disLike"), SetOptions.merge()).await()

                    Response.Success(true)
                }
                // 사용자의 게시글에 대한 호감도가 싫어요였던 경우
                else if (dailyBoard.favourability.equals("disLike")) {
                    if (dailyBoard.disLike != 0)
                        documentReference.update("disLike", FieldValue.increment(-1)).await()
                    documentReference.set(setUserFavour("usual"), SetOptions.merge()).await()
                    Response.Success(true)
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
}