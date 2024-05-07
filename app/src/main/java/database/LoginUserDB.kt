package database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import model.LoginInfo


@Database(entities = [LoginInfo::class], version = 1)
abstract class LoginUserDB : RoomDatabase() {
    abstract fun getEventsDao(): LoginInfoDao

    companion object {
        private var instance: LoginUserDB? = null

        @Synchronized
        fun getInstance(context: Context): LoginUserDB? {
            if (instance == null) {
                synchronized(LoginUserDB::class.java) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LoginUserDB::class.java, "login_user_db"
                    ).build()
                }
            }
            return instance
        }


    }


}


@Dao
interface LoginInfoDao {
    @Insert
    fun insertUserInfo(loginInfo: LoginInfo)

    @Query("SELECT * FROM LoginInfo")
    fun getUserInfo(): LoginInfo
}