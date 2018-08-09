package ventum.zephyr.soundboardtemplate.ui

import android.databinding.DataBindingUtil
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import ventum.zephyr.soundboardtemplate.R
import ventum.zephyr.soundboardtemplate.adapter.SoundboardPagerAdapter
import ventum.zephyr.soundboardtemplate.model.SoundItem
import ventum.zephyr.soundboardtemplate.model.SoundItems
import ventum.zephyr.soundboardtemplate.model.SoundboardCategory


abstract class SoundboardActivity : AppCompatActivity() {

    private lateinit var binding: ventum.zephyr.soundboardtemplate.databinding.ActivitySoundboardBinding
    private var soundboardCategories: ArrayList<SoundboardCategory> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_soundboard)
        setupToolbar()
        Glide.with(this).load(getBackgroundImage())
                .apply(bitmapTransform(BlurTransformation(22)))
                .into(binding.bgImageView)

        soundboardCategories.add(createBattleSoundboardCategory())
        soundboardCategories.add(createSpellsSoundboardCategory())
        binding.viewPager.adapter = SoundboardPagerAdapter(supportFragmentManager, soundboardCategories)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

    }

    @DrawableRes
    abstract fun getBackgroundImage(): Int

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar.let {
            it?.title = getToolbarTitle()
            binding.toolbar.setTitleTextColor(getToolbarItemsColor())
        }
    }

    private fun getToolbarBackgroundColor() = ContextCompat.getColor(this, R.color.colorPrimary)

    private fun getToolbarTitle() = getString(R.string.app_name)

    private fun getToolbarItemsColor() = ContextCompat.getColor(this, android.R.color.white)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        for (i in 0 until menu.size()) {
            menu.getItem(i).icon.setColorFilter(getToolbarItemsColor(), PorterDuff.Mode.SRC_ATOP)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var handled = false
        when (item.itemId) {
            R.id.action_share -> {
                showToastMessage("share menu")
                handled = true
            }
            R.id.action_rate -> {
                showToastMessage("rate menu")
                handled = true
            }
        }
        return handled || super.onOptionsItemSelected(item)
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

    private fun showToastMessage(toastText: String) = Toast.makeText(this, toastText, LENGTH_SHORT).show()
}
