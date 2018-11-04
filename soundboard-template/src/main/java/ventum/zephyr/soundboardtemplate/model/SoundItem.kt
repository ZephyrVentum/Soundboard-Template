package ventum.zephyr.soundboardtemplate.model

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes
import android.support.annotation.StringRes

class SoundItem(@DrawableRes val image: Int, @RawRes val sound: Int, @StringRes val name: Int = -1, val soundId: Int = -1) : Parcelable {

    constructor(@DrawableRes image: Int, @RawRes sound: Int, soundId: Int) : this(image, sound, -1, soundId)

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(image)
        writeInt(sound)
        writeInt(name)
        writeInt(soundId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SoundItem> = object : Parcelable.Creator<SoundItem> {
            override fun createFromParcel(source: Parcel): SoundItem = SoundItem(source)
            override fun newArray(size: Int): Array<SoundItem?> = arrayOfNulls(size)
        }
    }
}

class SoundItems() : ArrayList<SoundItem>(), Parcelable {
    constructor(parcel: Parcel) : this()

    override fun writeToParcel(dest: Parcel?, flags: Int) {}

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<SoundItems> {
        override fun createFromParcel(parcel: Parcel) = SoundItems(parcel)

        override fun newArray(size: Int): Array<SoundItems?> = arrayOfNulls(size)
    }
}