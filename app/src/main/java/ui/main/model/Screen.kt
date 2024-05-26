package ui.main.model

enum class Screen(val value : Int) {
    DAILY_BOARD(0),
    VERSUS_BOARD(1),
    MY_PAGE(2);

    companion object {
        fun fromValue(value: Int): Screen? {
            return Screen.values().find { it.value == value }
        }
    }
}