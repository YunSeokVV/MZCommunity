package util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import view.ProgressDialog
import java.util.Random

class Util {

    companion object {
        private var progressDialog: ProgressDialog? = null

        fun makeToastMessage(contents: String, context: Context) {
            Toast.makeText(context, contents, Toast.LENGTH_SHORT).show()
        }

        fun parsingFireStoreDocument(documentSnapshot: DocumentSnapshot, key: String): String {
            var result: String
            if (key == "disLike") {
                result = (documentSnapshot.get(key) as? Long ?: 0).toString()
            } else if (key == "like") {
                result = (documentSnapshot.get(key) as? Long ?: 0).toString()
            } else if (key == "writerUID") {
                result = documentSnapshot.get(key) as? String ?: "noWriterUID"
            } else if (key == "boardContents") {
                result = documentSnapshot.get(key) as? String ?: "noBoardContents"
            } else if (key == "userFavourability") {
                val userFavour = documentSnapshot.get(key) as? Map<String, Any>
                result = (userFavour?.get(FirebaseAuth.auth.uid.toString()) ?: "usual").toString()
            } else if (key == "viewType") {
                result = (documentSnapshot.get(key) as? Long ?: 0).toString()
            } else {
                result = documentSnapshot.get(key) as? String ?: "nothing"
            }
            return result
        }

        fun parsingDailyBoardFiles(documentSnapshot: DocumentSnapshot, key: String): List<String> =
            documentSnapshot.get(key) as? List<String> ?: listOf()

        fun removeStr(original: String, deleteStr: String): String {
            var result = original
            result = result.substring(deleteStr.length)
            return result
        }

        fun showProgressDialog(context: Context, showDialog: Boolean) {

            progressDialog = progressDialog ?: ProgressDialog(context)

            if (showDialog) {
                progressDialog?.show()
            } else {
                progressDialog?.dismiss()
                progressDialog = null
            }
        }

        fun getRanNum(ranNum: Int): Int {
            // 0~ranNum 사이의 랜덤 숫자
            val randomNum = Random().nextInt(ranNum)
            return randomNum
        }

    }
}