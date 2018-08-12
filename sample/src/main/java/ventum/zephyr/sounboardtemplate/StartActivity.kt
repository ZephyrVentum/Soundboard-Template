package ventum.zephyr.sounboardtemplate

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

    private fun createFirstCategory(): SoundboardCategory {
        val soundItems = SoundItems()
        for (i in 0..6 step 1) {
            soundItems.add(SoundItem(R.drawable.bg_main, R.raw.lvlup_sound))
        }
        return SoundboardCategory("First", soundItems)
    }

    private fun createSecondCategory(): SoundboardCategory {
        val soundItems = SoundItems().apply {
            for (i in 0..11) {
                add(SoundItem(R.drawable.bg_main, R.raw.lvlup_sound))
            }
        }
        return SoundboardCategory("Second", soundItems)
    }

    private fun createThirdCategory(): SoundboardCategory {
        val soundItems = SoundItems()
        for (i in 0..5) {
            soundItems.add(SoundItem(R.drawable.bg_main, R.raw.lvlup_sound))
        }
        return SoundboardCategory("Third", soundItems)
    }
}