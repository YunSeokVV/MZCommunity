package module

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import repository.GetDailyBoardRepositoryImpl
import repository.GetDailyBoardRepositry
import repository.LoginActivityRepository
import repository.LoginActivityRepositoryImpl
import repository.PostDailyBoardRepository
import repository.PostDailyBoardRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun provideFireStoreRef() = Firebase.firestore

    @Provides
    fun provideStroageRef() = Firebase.storage

    @Provides
    fun provideLoginActivityRepository(fireStore : FirebaseFirestore) : LoginActivityRepository = LoginActivityRepositoryImpl(
        fireStore
    )

    @Provides
    fun providePostingDailyBoardRepository(storageReference: FirebaseStorage, fireStore : FirebaseFirestore) : PostDailyBoardRepository = PostDailyBoardRepositoryImpl(
        storageReference, fireStore
    )

    @Provides
    fun provideGetDailyBoardRepository(storageReference: FirebaseStorage, fireStore : FirebaseFirestore) : GetDailyBoardRepositry = GetDailyBoardRepositoryImpl(
        storageReference, fireStore
    )



}