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
import repository.CommentRepostiroy
import repository.CommentRepostiroyImpl
import repository.LoadingRepository
import repository.LoadingRepositoryImpl
import repository.SignUpActivityRepository
import repository.SignUpActivityRepositoryImpl
import repository.UserRepository
import repository.UserRepositoryImpl
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
    fun provideLoginActivityRepository(fireStore : FirebaseFirestore) : SignUpActivityRepository = SignUpActivityRepositoryImpl(
        fireStore
    )

    @Provides
    fun provideDailyBoardRepository(storageReference: FirebaseStorage, fireStore : FirebaseFirestore) : DailyBoardRepository = DailyBoardRepositoryImpl(
        storageReference, fireStore
    )

    @Provides
    fun provideLoadingRepository(storageReference: FirebaseStorage, fireStore : FirebaseFirestore) : LoadingRepository = LoadingRepositoryImpl(
        storageReference, fireStore
    )

    @Provides
    fun provideCommentRepostiroy(storageReference: FirebaseStorage, fireStore : FirebaseFirestore) : CommentRepostiroy = CommentRepostiroyImpl(
        fireStore, storageReference
    )

    @Provides
    fun provideVersusBoard(storageReference: FirebaseStorage, fireStore: FirebaseFirestore) : VersusRepostiroy = VersusRepostiroyImpl(
        fireStore, storageReference
    )

    @Provides
    fun provideUserRepository(storageReference: FirebaseStorage, fireStore: FirebaseFirestore) : UserRepository = UserRepositoryImpl(
        storageReference, fireStore
    )

}