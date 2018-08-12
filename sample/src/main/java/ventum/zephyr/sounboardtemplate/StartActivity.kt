package ventum.zephyr.sounboardtemplate

import android.support.annotation.DrawableRes
import ventum.zephyr.soundboardtemplate.model.SoundItem
import ventum.zephyr.soundboardtemplate.model.SoundItems
import ventum.zephyr.soundboardtemplate.model.SoundboardCategory
import ventum.zephyr.soundboardtemplate.ui.SoundboardActivity

class StartActivity : SoundboardActivity() {

    override fun getSoundboardCategories(): ArrayList<SoundboardCategory> {
        val soundboardCategories: ArrayList<SoundboardCategory> = ArrayList()
        soundboardCategories.add(createFirstCategory())
        soundboardCategories.add(createSecondCategory())
        soundboardCategories.add(createThirdCategory())
        return soundboardCategories
    }

    @DrawableRes
    override fun getBackgroundImage(): Int = R.drawable.sample_image

    private fun createFirstCategory(): SoundboardCategory {
        val soundItems = SoundItems()
        for (i in 0..6) {
            soundItems.add(SoundItem(R.drawable.sample_image, R.raw.lvlup_sound))
        }
        return SoundboardCategory("First", soundItems)
    }

    private fun createSecondCategory(): SoundboardCategory {
        val soundItems = SoundItems()
        for (i in 0..11) {
            soundItems.add(SoundItem(R.drawable.sample_image, R.raw.lvlup_sound))
        }
        return SoundboardCategory("Second", soundItems)
    }

    private fun createThirdCategory(): SoundboardCategory {
        val soundItems = SoundItems()
        for (i in 0..5) {
            soundItems.add(SoundItem(R.drawable.sample_image, R.raw.lvlup_sound))
        }
        return SoundboardCategory("Third", soundItems)
    }
}