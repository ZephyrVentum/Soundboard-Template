package ventum.zephyr.sounboardtemplate

import ventum.zephyr.soundboardtemplate.model.SoundItem
import ventum.zephyr.soundboardtemplate.model.SoundItems
import ventum.zephyr.soundboardtemplate.model.SoundboardCategory
import ventum.zephyr.soundboardtemplate.ui.SoundboardActivity

class StartActivity : SoundboardActivity() {

    override fun getSoundboardCategories() = ArrayList<SoundboardCategory>().apply {
        add(createFirstCategory())
        add(createSecondCategory())
        add(createThirdCategory())
    }

    private fun createFirstCategory(): SoundboardCategory {
        val soundItems = SoundItems()
        for (i in 0..6 step 1) {
            soundItems.add(SoundItem(R.drawable.bg_main, R.raw.example_sound))
        }
        return SoundboardCategory("First", soundItems)
    }

    private fun createSecondCategory() = SoundboardCategory("Second", SoundItems().apply {
        for (i in 0..11) {
            add(SoundItem(R.drawable.bg_main, R.raw.example_sound))
        }
    })

    private fun createThirdCategory(): SoundboardCategory {
        val soundItems = SoundItems()
        for (i in 0..5) {
            soundItems.add(SoundItem(R.drawable.bg_main, R.raw.example_sound, R.string.example_sound_name))
        }
        return SoundboardCategory("Third", soundItems)
    }
}