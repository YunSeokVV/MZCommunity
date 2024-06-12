package di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import data.repository.login.LoginRepositoryImpl
import domain.login.LoginRepository

@Module
@InstallIn(SingletonComponent::class)
object RoomDBModule {
    @Provides
    fun provideLoginRepository(@ApplicationContext appContext : Context): LoginRepository = LoginRepositoryImpl(appContext)
}