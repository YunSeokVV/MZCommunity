package data.model

enum class UserFavourability(val value : String) {
    LIKE("like"),
    DISLIKE("disLike"),
    USUAL("usual");

    companion object {
        fun fromValue(value: String): UserFavourability? {
            if(value == "LIKE" || value == "like")return LIKE
            else if(value == "DISLIKE" || value == "disLike")return  DISLIKE
            else return USUAL
        }
        fun fromValue(value: UserFavourability): String = value.name

    }
}