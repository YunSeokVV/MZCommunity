package model

import java.io.Serializable

data class LoginedUser(val profileUri : String, val nickName : String) : Serializable
