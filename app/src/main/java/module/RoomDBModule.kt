package module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import data.repository.login.SignInActivityRepository
import data.repository.login.SignInActivityRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RoomDBModule {
    @Provides
    fun provideLoginActivityRepository(): SignInActivityRepository = SignInActivityRepositoryImpl()
}