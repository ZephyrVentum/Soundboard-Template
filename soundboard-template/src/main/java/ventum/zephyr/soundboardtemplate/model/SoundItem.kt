package ventum.zephyr.soundboardtemplate.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes

data class SoundItem(@DrawableRes val image: Int, @RawRes val sound: Int, @StringRes val name: Int = -1, val isLongSound: Boolean = false) : Parcelable {

    constructor(@DrawableRes image: Int, @RawRes sound: Int) : this(image, sound, -1)

    constructor(@DrawableRes image: Int, @RawRes sound: Int, isLongSound: Boolean) : this(image, sound, -1, isLongSound)

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readInt(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(image)
        writeInt(sound)
        writeInt(name)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SoundItem> = object : Parcelable.Creator<SoundItem> {
            override fun createFromParcel(source: Parcel): SoundItem = SoundItem(source)
            override fun newArray(size: Int): Array<SoundItem?> = arrayOfNulls(size)
        }
    }
}

data class SoundItems(val index: Int = -1) : ArrayList<SoundItem>(), Parcelable {
    constructor(parcel: Parcel) : this()

    override fun writeToParcel(dest: Parcel?, flags: Int) {}

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<SoundItems> {
        override fun createFromParcel(parcel: Parcel) = SoundItems(parcel)

        override fun newArray(size: Int): Array<SoundItems?> = arrayOfNulls(size)
    }
}