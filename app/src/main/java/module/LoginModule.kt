package module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import useCase.LoginActivityUseCase

@Module
@InstallIn(SingletonComponent::class)
object LoginModule{

    @Provides
    fun provideLoginUseCase() = LoginActivityUseCase()
}