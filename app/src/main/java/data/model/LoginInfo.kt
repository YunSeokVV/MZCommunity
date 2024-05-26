package data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LoginInfo (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email : String,
    val passwd : String,
    val loginWay : String
)