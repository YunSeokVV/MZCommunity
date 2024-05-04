package repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.DailyBoard
import model.DailyboardCollection
import model.File
import model.Response
import util.FirebaseAuth
import util.Util
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

interface DailyBoardRepository {
    suspend fun postBoard(contents: String, uploadFileUri: List<File>, viewType : Int): Response<Boolean>
    suspend fun getDailyBoards(): List<DailyBoard>

    suspend fun getDailyBoard(documentId: String): DailyBoard

    suspend fun increaseFavourability(dailyBoard: DailyBoard, isLike : Boolean): Response<Boolean>
}

@Singleton
class DailyBoardRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStoreRef: FirebaseFirestore
) :
    DailyBoardRepository {
    override suspend fun postBoard(
        contents: String,
        uploadFileUri: List<File>,
        viewType : Int
    ): Response<Boolean> = withContext(Dispatchers.IO) {
        try {
            val fireStore = fireStoreRef
            val board = hashMapOf(
                "boardContents" to contents,
                "like" to 0,
                "disLike" to 0,
                "writerUID" to FirebaseAuth.auth.uid,
                "viewType" to viewType
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


    override suspend fun getDailyBoard(documentId: String): DailyBoard = suspendCancellableCoroutine { continuation ->
        fireStoreRef.collection("dailyBoard").document(documentId).get().addOnSuccessListener { result ->
                val dailyBoardCollection = getDailyBoardCollection(result)

                try {
                    runBlocking {
                        val userInfoSnapshot =
                            fireStoreRef.collection("MZUsers")
                                .document(dailyBoardCollection.writerUID).get().await()

                        val files = dailyBoardCollection.files.map {
                            Uri.parse(it)
                        }

                        val boardUID = result.id
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

                        continuation.resume(dailyBoard, null)
                    }
                } catch (e: Exception) {
                    Logger.v(e.message.toString())
                    continuation.resumeWithException(Exception())
                }

        }
    }

    override suspend fun increaseFavourability(dailyBoard: DailyBoard, isLike : Boolean): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val fireStore = fireStoreRef
                val documentReference =
                    fireStore.collection("dailyBoard").document(dailyBoard.boardUID)

                // 사용자의 게시글에 대한 호감도가 보통이였던 경우
                if (dailyBoard.favourability.equals("usual")) {
                    if(isLike){
                        documentReference.update("like", FieldValue.increment(1)).await()
                        documentReference.set(setUserFavour("like"), SetOptions.merge()).await()
                    } else{
                        documentReference.update("disLike", FieldValue.increment(1)).await()
                        documentReference.set(setUserFavour("disLike"), SetOptions.merge()).await()
                    }


                    // 사용자의 게시글에 대한 호감도가 좋아요였던 경우
                } else if (dailyBoard.favourability.equals("like")) {
                    if (dailyBoard.like != 0)
                        documentReference.update("like", FieldValue.increment(-1)).await()

                    if(isLike){
                        documentReference.set(setUserFavour("usual"), SetOptions.merge()).await()
                    } else{
                        documentReference.update("disLike", FieldValue.increment(1)).await()
                        documentReference.set(setUserFavour("disLike"), SetOptions.merge()).await()
                    }

                    // 사용자의 게시글에 대한 호감도가 싫어요였던 경우
                } else if (dailyBoard.favourability.equals("disLike")) {
                    if(isLike)
                    documentReference.update("like", FieldValue.increment(1)).await()
                    if (dailyBoard.disLike != 0)
                        documentReference.update("disLike", FieldValue.increment(-1)).await()
                    if(isLike)
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