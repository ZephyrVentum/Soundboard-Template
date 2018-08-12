package ventum.zephyr.soundboardtemplate.listener

import ventum.zephyr.soundboardtemplate.model.SoundItem

interface SoundItemActionListener {
    fun onSoundItemClicked(item: SoundItem)
}