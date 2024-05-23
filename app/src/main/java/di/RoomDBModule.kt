package di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import data.repository.login.SignInActivityRepositoryImpl
import domain.login.SignInActivityRepository

@Module
@InstallIn(SingletonComponent::class)
object RoomDBModule {
    @Provides
    fun provideLoginActivityRepository(): SignInActivityRepository = SignInActivityRepositoryImpl()
}