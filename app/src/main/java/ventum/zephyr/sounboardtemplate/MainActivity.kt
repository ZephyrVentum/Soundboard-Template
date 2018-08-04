package ventum.zephyr.sounboardtemplate

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import ventum.zephyr.sounboardtemplate.databinding.ActivityMainBinding
import ventum.zephyr.sounboardtemplate.model.SoundItem
import ventum.zephyr.sounboardtemplate.model.SoundItems
import ventum.zephyr.sounboardtemplate.model.SoundboardCategory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var soundboardCategories: ArrayList<SoundboardCategory> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Glide.with(this).load(R.drawable.bg_main)
                .apply(bitmapTransform(BlurTransformation(22)))
                .into(binding.bgImageView)

//        soundItems = ArrayList()
//        soundItems.add(SoundItem(R.drawable.img_victory, R.raw.victory_sound))
//        soundItems.add(SoundItem(R.drawable.img_defend, R.raw.defend_sound))

        soundboardCategories.add(createBattleSoundboardCategory())
        soundboardCategories.add(createSpellsSoundboardCategory())
        binding.viewPager.adapter = SoundboardPagerAdapter(supportFragmentManager, soundboardCategories)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun createBattleSoundboardCategory(): SoundboardCategory {
        val soundItems = SoundItems()
        soundItems.add(SoundItem(R.drawable.img_victory, R.raw.victory_sound))
        soundItems.add(SoundItem(R.drawable.img_defend, R.raw.defend_sound))
        return SoundboardCategory("Battle", soundItems)
    }

    private fun createSpellsSoundboardCategory(): SoundboardCategory {
        val soundItems = SoundItems()
        soundItems.add(SoundItem(R.drawable.blind, R.raw.blind_sound))
        return SoundboardCategory("Spell", soundItems)
    }
}
