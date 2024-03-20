package repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import model.Response
import model.Response.Failure
import model.Response.Success
import javax.inject.Inject
import javax.inject.Singleton

interface ImageRepositry {
    suspend fun uploadImage(uri: Uri): Response<Boolean>
}

@Singleton
class ImageRepositoryImpl @Inject constructor(private val storageReference: FirebaseStorage) :
    ImageRepositry {
    override suspend fun uploadImage(uri: Uri): Response<Boolean> = try {
        val storageRef = storageReference.reference

        var testImg = storageRef.child("test_image/testImage.jpg")
        val uploadTask = testImg.putFile(uri)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Logger.v("upload failed ")

        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            Logger.v("upload complete")
        }

        Logger.v("Success called")

        Success(true)
    } catch (e: Exception) {
        Failure(e)
    }

}