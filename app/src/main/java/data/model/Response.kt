package data.model

sealed class Response<out T> {

    companion object{
        fun <T> success(data:T):Response<T>{
            return if(data != null){
                Success(data)
            } else {
                Failure(null)
            }
        }
    }
    data class Success<out T>(
        val data : T
    ) : Response<T>()

    data class Failure(
        val e : Exception?
    ) : Response<Nothing>()

}