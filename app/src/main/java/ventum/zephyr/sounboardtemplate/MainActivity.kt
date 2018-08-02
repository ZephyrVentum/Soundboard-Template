package ventum.zephyr.sounboardtemplate

import android.databinding.DataBindingUtil
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ventum.zephyr.sounboardtemplate.databinding.ActivityMainBinding
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.BlurTransformation


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.testImage.setColorFilter(R.color.item_image_mask_color, PorterDuff.Mode.SRC_OVER)//DST_OUT
        Glide.with(this).load(R.drawable.bg_main)
                .apply(bitmapTransform(BlurTransformation(22)))
                .into(binding.bgImageView)
    }
}
