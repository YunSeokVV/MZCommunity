package repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
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

//interface GetDailyBoardRepositry {
//    suspend fun getDailyBoard(): List<DailyBoard>
//}

interface DailyBoardRepository {
    suspend fun postBoard(contents: String, uploadImagesUri: ArrayList<Images>): Response<Boolean>
    suspend fun getDailyBoard(): List<DailyBoard>
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
                Logger.v("task is successful")
                Response.Success(true)
            } else {
                Logger.v("task is failed")
                Response.Success(false)
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getDailyBoard(): List<DailyBoard> = withContext(Dispatchers.IO) {
        var dailyBoards = ArrayList<DailyBoard>()

        fireStoreRef.collection("dailyBoard").get().addOnSuccessListener { result ->
            for (document in result) {
                val dailyBoardCollection = DailyboardCollection(
                    Util.parsingFireStoreDocument(document, "boardContents"),
                    Util.parsingFireStoreDocument(document, "disLike").toInt(),
                    Util.parsingFireStoreDocument(document, "like").toInt(),
                    Util.parsingFireStoreDocument(document, "writerUID")
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

                        val userNickName = userNicknameSnapshot.get("nickName").toString()
                        val boardContents = dailyBoardCollection.boardContents
                        val like = dailyBoardCollection.like
                        val disLike = dailyBoardCollection.disLike

                        val dailyBoard = DailyBoard(
                            writerProfileUri,
                            userNickName,
                            boardContents,
                            boardImages,
                            like,
                            disLike
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

}