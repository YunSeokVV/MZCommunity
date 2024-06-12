package domain.login

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSavedUserLoginInfoUsecase @Inject constructor(private val loginRepository: LoginRepository) {
    suspend operator fun invoke() = flow {
        emit(loginRepository.getSavedUserLoginInfo())
    }
}