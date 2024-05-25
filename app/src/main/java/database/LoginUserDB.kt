package database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import data.model.LoginInfo


@Database(entities = [LoginInfo::class], version = 1)
abstract class LoginUserDB : RoomDatabase() {
    abstract fun getEventsDao(): LoginInfoDao

    companion object {

        @Synchronized
        fun getInstance(@ApplicationContext appContext: Context): LoginUserDB {
            synchronized(LoginUserDB::class.java){
                return Room.databaseBuilder(
                    appContext,
                    LoginUserDB::class.java, "login_user_db"
                ).build()
            }
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