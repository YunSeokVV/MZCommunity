package data.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
data class DailyBoard(
    val writerNickname: String,
    val writerProfileUri: String,
    val boardContents: String,
    val files: List<String>,
    val disLike: Int,
    val like: Int,
    val boardUID: String,
    val favourability: String,
    val viewType: DailyBoardViewType
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        DailyBoardViewType.values()[parcel.readInt()]
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(writerNickname)
        dest.writeString(writerProfileUri)
        dest.writeString(boardContents)
        dest.writeStringList(files)
        dest.writeInt(disLike)
        dest.writeInt(like)
        dest.writeString(boardUID)
        dest.writeString(favourability)
        dest.writeInt(viewType.ordinal)
    }

    companion object CREATOR : Parcelable.Creator<DailyBoard> {
        override fun createFromParcel(parcel: Parcel): DailyBoard {
            return DailyBoard(parcel)
        }

        override fun newArray(size: Int): Array<DailyBoard?> {
            return arrayOfNulls(size)
        }
    }
}
