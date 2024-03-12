package util

import android.content.Context
import android.text.TextUtils
import android.widget.Toast

class Util {

    companion object {
        fun makeToastMessage(contents: String, context: Context) {
            Toast.makeText(context, contents, Toast.LENGTH_SHORT).show()
        }
    }
}