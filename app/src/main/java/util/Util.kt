package util

import android.content.Context
import android.provider.DocumentsContract.Document
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.orhanobut.logger.Logger

class Util {

    companion object {
        fun makeToastMessage(contents: String, context: Context) {
            Toast.makeText(context, contents, Toast.LENGTH_SHORT).show()
        }

        fun parsingFireStoreDocument(documentSnapshot: DocumentSnapshot, key: String): String {
            var result: String
            if (key.equals("disLike")) {
                result = (documentSnapshot.get(key) as? Long ?: 0).toString()
            } else if (key.equals("like")) {
                result = (documentSnapshot.get(key) as? Long ?: 0).toString()
            } else if (key.equals("writerUID")) {
                result = documentSnapshot.get(key) as? String ?: "noWriterUID"
            } else if (key.equals("boardContents")) {
                result = documentSnapshot.get(key) as? String ?: "noBoardContents"
            } else if (key.equals("userFavourability")) {
                val userFavour = documentSnapshot.get(key) as? Map<String, Any>
                result = (userFavour?.get(FirebaseAuth.auth.uid.toString())?:"usual").toString()
            } else {
                result = documentSnapshot.get(key) as? String ?: "nothing"
            }
            return result
        }

        fun removeStr(original: String, deleteStr : String): String {
            var result = original
            result = result.substring(deleteStr.length)
            return result
        }

    }
}