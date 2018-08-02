package ventum.zephyr.sounboardtemplate

import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.graphics.PorterDuff
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import ventum.zephyr.sounboardtemplate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Glide.with(this).load(R.drawable.bg_main)
                .apply(bitmapTransform(BlurTransformation(22)))
                .into(binding.bgImageView)

        setupImage(binding.testImage, R.drawable.img_victory)
        setupImage(binding.test2Image, R.drawable.img_defend)
        setupImage(binding.test3Image, R.drawable.blind)
    }

    private fun setupImage(view: ImageView, @DrawableRes drawable: Int){
        view.setColorFilter(ContextCompat.getColor(this, R.color.item_image_mask_color), PorterDuff.Mode.SRC_OVER)
        view.setImageDrawable(getRippleDrawable(drawable))
    }

    private fun getRippleColorStateList(color: Int): ColorStateList = ColorStateList(arrayOf(intArrayOf()), intArrayOf(color))

    private fun getRippleDrawable(@DrawableRes drawable: Int): RippleDrawable = RippleDrawable(
            getRippleColorStateList(ContextCompat.getColor(this, R.color.ripple_effect_color)),
            getDrawable(drawable), null)
}
