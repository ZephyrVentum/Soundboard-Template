package ventum.zephyr.sounboardtemplate.model

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes
import java.io.Serializable

class SoundItem(@DrawableRes val image: Int, @RawRes val sound: Int) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(image)
        writeInt(sound)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SoundItem> = object : Parcelable.Creator<SoundItem> {
            override fun createFromParcel(source: Parcel): SoundItem = SoundItem(source)
            override fun newArray(size: Int): Array<SoundItem?> = arrayOfNulls(size)
        }
    }
}

class SoundItems : ArrayList<SoundItem>(), Serializable