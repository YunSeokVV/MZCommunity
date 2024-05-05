package model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class LoginInfo (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email : String,
    val passwd : String
)