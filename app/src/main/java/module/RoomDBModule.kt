package module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import repository.SignInActivityRepository
import repository.SignInActivityRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RoomDBModule {
    @Provides
    fun provideLoginActivityRepository(): SignInActivityRepository = SignInActivityRepositoryImpl()
}