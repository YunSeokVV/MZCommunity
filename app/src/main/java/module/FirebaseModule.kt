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
import data.repository.dailyboard.DailyBoardRepository
import data.repository.dailyboard.DailyBoardRepositoryImpl
import data.repository.comment.CommentRepostiroy
import data.repository.comment.CommentRepostiroyImpl
import data.repository.loading.LoadingRepository
import data.repository.loading.LoadingRepositoryImpl
import data.repository.signup.SignUpActivityRepository
import data.repository.signup.SignUpActivityRepositoryImpl
import data.repository.user.UserRepository
import data.repository.user.UserRepositoryImpl
import data.repository.versus.VersusRepostiroy
import data.repository.versus.VersusRepostiroyImpl

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