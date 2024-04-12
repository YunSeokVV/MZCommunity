package repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Comment
import model.Response
import util.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

interface CommentRepostiroy {

    suspend fun postComment(
        contents: String,
        parentUID: String,
        collectionName: String
    ): Response<Boolean>

    suspend fun postReply(
        contents: String,
        parentUID: String,
        nestedCommentCollection: String,
        commentName: String
    ): Response<Boolean>

    suspend fun getComments(parentUID: String, collectionName: String): List<Comment>

    suspend fun getNestedComments(parentUID: String, nestedCommentName : String): List<Comment>
}

@Singleton
class CommentRepostiroyImpl @Inject constructor(
    private val fireStoreRef: FirebaseFirestore,
    private val storage: FirebaseStorage,
) :
    CommentRepostiroy {
    override suspend fun postComment(
        contents: String,
        parentUID: String,
        collectionName: String
    ): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val fireStore = fireStoreRef
                val dailyBoardComment = hashMapOf(
                    "commentContents" to contents,
                    "writerUID" to FirebaseAuth.auth.uid,
                    "parentUID" to parentUID,
                    "hasNestedComment" to false,
                    "postingTime" to Timestamp.now()
                )

                fireStore.collection(collectionName).add(dailyBoardComment)
                    .addOnSuccessListener {
                        Response.Success(true)
                    }.addOnFailureListener {
                        Response.Success(false)
                    }.await()

                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }

    override suspend fun postReply(
        contents: String,
        parentUID: String,
        nestedCommentCollection: String,
        commentName: String
    ): Response<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val fireStore = fireStoreRef
                val dailyBoardReply = hashMapOf(
                    "writerUID" to FirebaseAuth.auth.uid,
                    "commentContents" to contents,
                    "parentUID" to parentUID,
                    "postingTime" to Timestamp.now()
                )

                fireStore.collection(nestedCommentCollection).add(dailyBoardReply).await()

                val dailyBoardComment = hashMapOf(
                    "hasNestedComment" to true
                )

                fireStore.collection(commentName).document(parentUID).update(
                    dailyBoardComment as Map<String, Any>
                ).await()


                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }

    override suspend fun getComments(parentUID: String, collectionName: String): List<Comment> =
        withContext(Dispatchers.IO) {
            var comments = mutableListOf<Comment>()
            val fireStore = fireStoreRef
            try {
                fireStore.collection(collectionName)
                    .orderBy("postingTime", Query.Direction.ASCENDING)
                    .whereEqualTo("parentUID", parentUID).get().addOnSuccessListener { documents ->
                    documents.forEach {
                        runBlocking {
                            val profile: Uri =
                                storage.reference.child("user_profile_image/" + it.get("writerUID") + ".jpg").downloadUrl.await()
                            val userDoc = fireStore.collection("MZUsers")
                                .document(it.get("writerUID").toString()).get().await()
                            val nickName = userDoc.get("nickName").toString()
                            val comment = Comment(
                                profile,
                                nickName,
                                it.get("commentContents").toString(),
                                it.id,
                                it.getBoolean("hasNestedComment") ?: false
                            )
                            comments.add(comment)
                        }
                    }
                }.await()


            } catch (e: Exception) {
                Logger.v(e.message.toString())
            }
            return@withContext comments
        }

    override suspend fun getNestedComments(parentUID: String, nestedCommentName : String): List<Comment> =
        withContext(Dispatchers.IO) {
            var comments = mutableListOf<Comment>()
            val fireStore = fireStoreRef
            try {
                fireStore.collection(nestedCommentName)
                    .orderBy("postingTime", Query.Direction.ASCENDING)
                    .whereEqualTo("parentUID", parentUID).get().addOnSuccessListener { documents ->
                    documents.forEach {
                        runBlocking {
                            val profile: Uri =
                                storage.reference.child("user_profile_image/" + it.get("writerUID") + ".jpg").downloadUrl.await()
                            val userDoc = fireStore.collection("MZUsers")
                                .document(it.get("writerUID").toString()).get().await()
                            val nickName = userDoc.get("nickName").toString()
                            val comment = Comment(
                                profile,
                                nickName,
                                it.get("commentContents").toString(),
                                it.id,
                                it.getBoolean("hasNestedComment") ?: false
                            )
                            comments.add(comment)
                        }
                    }
                }.await()


            } catch (e: Exception) {
                Logger.v(e.message.toString())
            }
            return@withContext comments
        }
}