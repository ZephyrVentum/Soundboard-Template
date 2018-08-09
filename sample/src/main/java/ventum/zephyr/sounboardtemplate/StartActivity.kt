package ventum.zephyr.sounboardtemplate

import android.support.annotation.DrawableRes
import ventum.zephyr.soundboardtemplate.ui.SoundboardActivity

class StartActivity : SoundboardActivity() {

    @DrawableRes
    override fun getBackgroundImage(): Int = R.drawable.sample_image
}