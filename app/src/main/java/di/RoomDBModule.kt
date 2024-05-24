package di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import data.repository.login.SignInActivityRepositoryImpl
import domain.login.SignInActivityRepository

@Module
@InstallIn(SingletonComponent::class)
object RoomDBModule {
    @Provides
    fun provideLoginActivityRepository(@ApplicationContext appContext : Context): SignInActivityRepository = SignInActivityRepositoryImpl(appContext)
}