package repository

import android.net.Uri
import com.example.mzcommunity.R
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Comment
import model.Response
import util.FirebaseAuth
import util.Util
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

    suspend fun getComments(
        parentUID: String,
        collectionName: String
    ): List<Comment>

    suspend fun getMoreComments(
        parentUID: String,
        collectionName: String
    ): List<Comment>

    suspend fun getNestedComments(parentUID: String, nestedCommentName: String): List<Comment>
}

@Singleton
class CommentRepostiroyImpl @Inject constructor(
    private val fireStoreRef: FirebaseFirestore,
    private val storage: FirebaseStorage,
) :
    CommentRepostiroy {
    lateinit var recentTask: Query
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

    override suspend fun getComments(
        parentUID: String,
        collectionName: String,
    ): List<Comment> =
        withContext(Dispatchers.IO) {
            var comments = mutableListOf<Comment>()
            val fireStore = fireStoreRef
            try {
                recentTask = fireStore.collection(collectionName)
                    .orderBy("postingTime", Query.Direction.DESCENDING)
                    .limit(5)
                    .whereEqualTo("parentUID", parentUID)

                fireStore.collection(collectionName)
                    .orderBy("postingTime", Query.Direction.DESCENDING)
                    .limit(5)
                    .whereEqualTo("parentUID", parentUID).get().addOnSuccessListener { documents ->
                        documents.forEach {
                            runBlocking {
                                val resourceId = R.drawable.user_profile2
                                val defaultProfile: String = Util.getResourceImage(resourceId)
                                val userDoc = fireStore.collection("MZUsers")
                                    .document(it.get("writerUID").toString()).get().await()
                                val nickName = userDoc.getString("nickName") ?: "알 수 없는 사용자"

                                val profileURL = userDoc.getString("profileURL") ?: defaultProfile
                                val comment = Comment(
                                    Uri.parse(profileURL),
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

    override suspend fun getMoreComments(
        parentUID: String,
        collectionName: String,
    ): List<Comment> =
        withContext(Dispatchers.IO) {
            var comments = mutableListOf<Comment>()
            val fireStore = fireStoreRef
            try {
                val documentSnapshot = recentTask.get().await()
                val lastVisible = documentSnapshot.documents[documentSnapshot.size() - 1]
                val next = fireStore.collection(collectionName)
                    .orderBy("postingTime", Query.Direction.DESCENDING)
                    .whereEqualTo("parentUID", parentUID)
                    .startAfter(lastVisible)
                    .limit(5)

                recentTask = next
                val documents = next.get().await()
                documents.forEach {
                    val userDoc = fireStore.collection("MZUsers")
                        .document(it.get("writerUID").toString()).get().await()
                    val resourceId = R.drawable.user_profile2
                    val defaultProfile: String = Util.getResourceImage(resourceId)
                    val profileURL = userDoc.getString("profileURL") ?: defaultProfile
                    val nickName = userDoc.getString("nickName") ?: "알 수 없는 사용자"
                    val comment = Comment(
                        Uri.parse(profileURL),
                        nickName,
                        it.get("commentContents").toString(),
                        it.id,
                        it.getBoolean("hasNestedComment") ?: false
                    )
                    comments.add(comment)

                }

            } catch (e: Exception) {
                Logger.v(e.message.toString())
            }
            return@withContext comments
        }

    override suspend fun getNestedComments(
        parentUID: String,
        nestedCommentName: String
    ): List<Comment> =
        withContext(Dispatchers.IO) {
            var comments = mutableListOf<Comment>()
            val fireStore = fireStoreRef
            try {
                fireStore.collection(nestedCommentName)
                    .orderBy("postingTime", Query.Direction.ASCENDING)
                    .whereEqualTo("parentUID", parentUID).get().addOnSuccessListener { documents ->
                        documents.forEach {
                            runBlocking {
                                val userDoc = fireStore.collection("MZUsers")
                                    .document(it.get("writerUID").toString()).get().await()
                                val resourceId = R.drawable.user_profile2
                                val defaultProfile: String = Util.getResourceImage(resourceId)
                                val profileURL = userDoc.getString("profileURL") ?: defaultProfile
                                val nickName = userDoc.get("nickName").toString()
                                val comment = Comment(
                                    Uri.parse(profileURL),
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