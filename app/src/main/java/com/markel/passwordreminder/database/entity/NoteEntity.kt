package com.markel.passwordreminder.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val description : String?,
    val password : String?,
    val isProtected : Boolean = false
) : Parcelable {
    @Ignore var passwordHided: Boolean = isProtected
    @Ignore var isBackVisible: Boolean = false

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
        passwordHided = parcel.readByte() != 0.toByte()
        isBackVisible = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(description)
        parcel.writeString(password)
        parcel.writeByte(if (isProtected) 1 else 0)
        parcel.writeByte(if (passwordHided) 1 else 0)
        parcel.writeByte(if (isBackVisible) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoteEntity> {
        override fun createFromParcel(parcel: Parcel): NoteEntity {
            return NoteEntity(parcel)
        }

        override fun newArray(size: Int): Array<NoteEntity?> {
            return arrayOfNulls(size)
        }
    }
}