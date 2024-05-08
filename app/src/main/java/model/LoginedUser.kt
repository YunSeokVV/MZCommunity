package model

import android.net.Uri
import java.io.Serializable

data class LoginedUser(val profileUri : Uri, val nickName : String) : Serializable
