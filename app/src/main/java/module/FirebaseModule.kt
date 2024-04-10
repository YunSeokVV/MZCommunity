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
import repository.DailyBoardRepository
import repository.DailyBoardRepositoryImpl
import repository.DailyCommentRepostiroy
import repository.DailyCommentRepostiroyImpl
import repository.LoginActivityRepository
import repository.LoginActivityRepositoryImpl
import repository.VersusRepostiroy
import repository.VersusRepostiroyImpl

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
    fun provideDailyBoardRepository(storageReference: FirebaseStorage, fireStore : FirebaseFirestore) : DailyBoardRepository = DailyBoardRepositoryImpl(
        storageReference, fireStore
    )

    @Provides
    fun provideDailyCommentRepostiroy(storageReference: FirebaseStorage, fireStore : FirebaseFirestore) : DailyCommentRepostiroy = DailyCommentRepostiroyImpl(
        fireStore, storageReference
    )

    @Provides
    fun provideVersusBoard(storageReference: FirebaseStorage, fireStore: FirebaseFirestore) : VersusRepostiroy = VersusRepostiroyImpl(
        fireStore, storageReference
    )

}