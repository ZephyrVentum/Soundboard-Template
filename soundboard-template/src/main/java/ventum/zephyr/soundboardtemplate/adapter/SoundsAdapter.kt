package ventum.zephyr.soundboardtemplate.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.RippleDrawable
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import ventum.zephyr.soundboardtemplate.R
import ventum.zephyr.soundboardtemplate.databinding.ItemSoundBinding
import ventum.zephyr.soundboardtemplate.listener.SoundItemActionListener
import ventum.zephyr.soundboardtemplate.model.SoundItem

class SoundsAdapter(private val soundItems: ArrayList<SoundItem>, val listener: SoundItemActionListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            SoundViewHolder(ItemSoundBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = soundItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            (holder as SoundViewHolder).bind(soundItems[position])

    private inner class SoundViewHolder(var binding: ItemSoundBinding) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(itemSound: SoundItem) {
            setupImage(binding.imageRoundedImageView, itemSound.image)
            binding.imageRoundedImageView.setOnClickListener { listener.onSoundItemClicked(itemSound) }
            binding.nameTextView.text = if (itemSound.name >= 0) binding.root.context.getString(itemSound.name) else ""
        }

        private fun setupImage(@NonNull view: ImageView, @DrawableRes drawable: Int) {
            val context: Context = view.context
            view.setColorFilter(ContextCompat.getColor(context, R.color.item_image_mask_color), PorterDuff.Mode.SRC_OVER)
            view.setImageDrawable(getRippleDrawable(context, drawable))
        }

        private fun getRippleColorStateList(color: Int): ColorStateList = ColorStateList(arrayOf(intArrayOf()), intArrayOf(color))

        private fun getRippleDrawable(context: Context, @DrawableRes drawable: Int) = RippleDrawable(
                getRippleColorStateList(ContextCompat.getColor(context, R.color.ripple_effect_color)),
                ContextCompat.getDrawable(context, drawable), null)
    }
}
