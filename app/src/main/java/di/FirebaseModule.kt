package di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import data.repository.dailyboard.DailyBoardRepositoryImpl
import data.repository.comment.CommentRepositoryImpl
import data.repository.loading.LoadingRepositoryImpl
import data.repository.signup.SignUpActivityRepositoryImpl
import data.repository.user.UserRepositoryImpl
import data.repository.versus.VersusRepostiroyImpl
import domain.comment.CommentRepository
import domain.dailyboard.DailyBoardRepository
import domain.loading.LoadingRepository
import domain.signup.SignUpActivityRepository
import domain.user.UserRepository
import domain.versus.VersusRepostiroy

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun provideFireStoreRef() = Firebase.firestore

    @Provides
    fun provideStroageRef() = Firebase.storage

    @Provides
    fun provideLoginActivityRepository(fireStore : FirebaseFirestore,@ApplicationContext appContext : Context) : SignUpActivityRepository = SignUpActivityRepositoryImpl(
        fireStore, appContext
    )

    @Provides
    fun provideDailyBoardRepository(storageReference: FirebaseStorage, fireStore : FirebaseFirestore, @ApplicationContext appContext : Context) : DailyBoardRepository = DailyBoardRepositoryImpl(
        storageReference, fireStore, appContext
    )

    @Provides
    fun provideLoadingRepository(storageReference: FirebaseStorage, fireStore : FirebaseFirestore, @ApplicationContext appContext : Context) : LoadingRepository = LoadingRepositoryImpl(
        storageReference, fireStore, appContext
    )

    @Provides
    fun provideCommentRepostiroy(storageReference: FirebaseStorage, fireStore : FirebaseFirestore, @ApplicationContext appContext : Context) : CommentRepository = CommentRepositoryImpl(
        fireStore, storageReference, appContext
    )

    @Provides
    fun provideVersusBoard(storageReference: FirebaseStorage, fireStore: FirebaseFirestore, @ApplicationContext appContext : Context) : VersusRepostiroy = VersusRepostiroyImpl(
        fireStore, storageReference, appContext
    )

    @Provides
    fun provideUserRepository(storageReference: FirebaseStorage, fireStore: FirebaseFirestore, @ApplicationContext appContext : Context) : UserRepository = UserRepositoryImpl(
        storageReference, fireStore, appContext
    )

}