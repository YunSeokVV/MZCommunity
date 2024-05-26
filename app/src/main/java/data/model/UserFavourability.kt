package data.model

enum class UserFavourability(val value : String) {
    LIKE("like"),
    DISLIKE("disLike"),
    USUAL("usual");

    companion object {
        fun fromValue(value: String): UserFavourability? {
            return values().find { it.value == value }
        }

        fun fromValue(value: UserFavourability): String = value.name

    }
}