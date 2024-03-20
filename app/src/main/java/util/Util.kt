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
            var result : String
            if (key.equals("disLike") || key.equals("like")) {
                result = (documentSnapshot.get(key) as? Long ?: 0).toString()
            } else {
                result = documentSnapshot.get(key) as? String ?: "nothing"
            }
            return result
        }
    }
}