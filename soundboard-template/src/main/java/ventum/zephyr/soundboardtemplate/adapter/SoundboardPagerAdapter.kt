package ventum.zephyr.soundboardtemplate.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ventum.zephyr.soundboardtemplate.model.SoundboardCategory
import ventum.zephyr.soundboardtemplate.ui.SoundboardFragment


class SoundboardPagerAdapter(fm: FragmentManager,
                             private var soundboardCategories: ArrayList<SoundboardCategory>) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = soundboardCategories.size

    override fun getItem(position: Int): Fragment = SoundboardFragment.newInstance(soundboardCategories[position].soundItems)

    override fun getPageTitle(position: Int): CharSequence? = soundboardCategories[position].name
}

