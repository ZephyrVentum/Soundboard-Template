package ventum.zephyr.soundboardtemplate.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.PorterDuff
import android.net.Uri
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import jp.wasabeef.glide.transformations.BlurTransformation
import ventum.zephyr.soundboardtemplate.R
import ventum.zephyr.soundboardtemplate.adapter.SoundboardPagerAdapter
import ventum.zephyr.soundboardtemplate.listener.AdShowTriggerListener
import ventum.zephyr.soundboardtemplate.model.SoundboardCategory

abstract class SoundboardActivity : AppCompatActivity(), AdShowTriggerListener {

    private lateinit var binding: ventum.zephyr.soundboardtemplate.databinding.ActivitySoundboardBinding
    private lateinit var soundboardCategories: ArrayList<SoundboardCategory>
    private lateinit var interstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_soundboard)
        setupAds()
        setupToolbar()
        setupBackgroundImage()
        soundboardCategories = getSoundboardCategories()

        binding.viewPager.adapter = SoundboardPagerAdapter(supportFragmentManager, soundboardCategories)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun setupAds() {
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        binding.adView.loadAd(AdRequest.Builder().build())
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.admob_interstitial_unit_id)
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
    }

    @DrawableRes
    abstract fun getBackgroundImage(): Int

    abstract fun getSoundboardCategories(): ArrayList<SoundboardCategory>

    override fun onAdShowTrigger() = interstitialAd.let { if (it.isLoaded) it.show() }

    private fun setupBackgroundImage() = Glide.with(this).load(getBackgroundImage())
            .apply(bitmapTransform(BlurTransformation(22)))
            .into(binding.bgImageView)

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar.let {
            it?.title = getString(R.string.app_name)
            binding.toolbar.setTitleTextColor(getToolbarItemsColor())
        }
    }

    private fun getToolbarItemsColor() = ContextCompat.getColor(this, R.color.toolbar_items_color)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        (0 until menu.size()).forEach {
            menu.getItem(it).icon.setColorFilter(getToolbarItemsColor(), PorterDuff.Mode.SRC_ATOP)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var handled = false
        when (item.itemId) {
            R.id.action_share -> {
                showShare()
                handled = true
            }
            R.id.action_rate -> {
                showRateUs()
                handled = true
            }
        }
        return handled || super.onOptionsItemSelected(item)
    }

    private fun showShare() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_title)))
    }

    private fun showRateUs() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.rate_us_market, "com.zephyr.ventum"))))
    }

    private fun showToastMessage(toastText: String) = Toast.makeText(this, toastText, LENGTH_SHORT).show()
}
