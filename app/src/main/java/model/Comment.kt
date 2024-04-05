package model

import android.net.Uri

data class Comment(val witerUri : Uri, val writerName : String, val contents : String, val commentUID : String)