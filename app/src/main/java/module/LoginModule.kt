package module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import usecase.GoogleLoginActivityUseCase

@Module
@InstallIn(SingletonComponent::class)
object LoginModule{

    @Provides
    fun provideLoginUseCase() = GoogleLoginActivityUseCase()
}