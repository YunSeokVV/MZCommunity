package data.model
enum class DailyBoardViewType(val value: Int) {
    TEXT(0),
    IMAGE(1),
    VIDEO(2);

    companion object {
        fun fromValue(value: Int): DailyBoardViewType? {
            return values().find { it.value == value }
        }

        fun fromValue(value : DailyBoardViewType) : Int = value.ordinal
    }
}