package repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Images
import model.Response
import model.Response.Failure
import model.Response.Success
import util.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

interface PostDailyBoardRepository {
    suspend fun postBoard(contents: String, uploadImagesUri: ArrayList<Images>): Response<Boolean>
}
@Singleton
class PostDailyBoardRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStoreRef: FirebaseFirestore
) : PostDailyBoardRepository {
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
            // Firestore에 게시물 추가
            val documentReference = fireStore.collection("dailyBoard").add(board).await()
            // 이미지 업로드 작업 수행
            val uploadTasks = uploadImagesUri.mapIndexed { idx, it ->
                val choosenImg =
                    storage.reference.child("board/${documentReference.id}/${idx}.png")
                choosenImg.putFile(Uri.parse(it.uri)).await()
            }
            // 모든 이미지 업로드 작업이 성공적으로 완료되었을 경우
            if (uploadTasks.all { it.task.isSuccessful }) {
                Logger.v("task is successful")
                Success(true)
            } else {
                Logger.v("task is failed")
                Success(false)
            }
        } catch (e: Exception) {
            Failure(e)
        }
    }
}