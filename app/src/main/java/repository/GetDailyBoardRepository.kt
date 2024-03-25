package repository

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
import model.Response
import util.Util
import javax.inject.Inject
import javax.inject.Singleton
import model.Response.Failure
import model.Response.Success

interface GetDailyBoardRepositry {
    //suspend fun getDailyBoard(): List<DailyPosting>
    suspend fun getDailyBoard(): List<DailyBoard>
}

@Singleton
class GetDailyBoardRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStoreRef: FirebaseFirestore
) :
    GetDailyBoardRepositry {
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