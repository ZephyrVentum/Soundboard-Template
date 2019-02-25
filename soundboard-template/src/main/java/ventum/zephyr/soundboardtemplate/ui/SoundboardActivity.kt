package ventum.zephyr.soundboardtemplate.ui

import android.Manifest
import android.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.PorterDuff
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RawRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import jp.wasabeef.glide.transformations.BlurTransformation
import ventum.zephyr.soundboardtemplate.BuildConfig
import ventum.zephyr.soundboardtemplate.R
import ventum.zephyr.soundboardtemplate.adapter.SoundboardPagerAdapter
import ventum.zephyr.soundboardtemplate.databinding.ActivitySoundboardBinding
import ventum.zephyr.soundboardtemplate.listener.SoundItemActionListener
import ventum.zephyr.soundboardtemplate.model.SoundItem
import ventum.zephyr.soundboardtemplate.model.SoundboardCategory
import java.io.File
import java.io.FileOutputStream
import java.util.*

const val STORAGE_NAME = "ventum.zephyr.soundboardtemplate.SHARED_PREFS"
const val MULTI_STREAM = "STORAGE_NAME" + ".MULTI_STREAM"
private const val WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101

abstract class SoundboardActivity : AppCompatActivity(), SoundItemActionListener,
        ShareAndSaveDialogFragment.ShareAndSaveDialogListener {

    protected lateinit var binding: ActivitySoundboardBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var soundboardCategories: ArrayList<SoundboardCategory>
    private lateinit var interstitialAd: InterstitialAd
    protected lateinit var soundPool: SoundPool
    protected var mediaPlayer: MediaPlayer = MediaPlayer()
    private var clicksAdCounter = 0
    @Suppress("LeakingThis")
    private var clicksToShowAd = getClickToAdsCount()
    private var isMultiStreamsEnable: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_soundboard)
        sharedPreferences = getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
        initSoundPool()
        soundboardCategories = getSoundboardCategories()
        setupAds()
        setupToolbar()
        setupBackgroundImage()
        setupViewPager()
    }

    private fun initSoundPool() {
        soundPool = SoundPool.Builder().setAudioAttributes(
                AudioAttributes.Builder()
                        .setUsage(getSoundPoolUsage())
                        .setContentType(getSoundPoolContentType())
                        .build())
                .setMaxStreams(10)
                .build()
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            run {
                if (status == 0) {
                    if (!BuildConfig.DEBUG) onAdShowTrigger()
                    if (!isMultiStreamsEnable) {
                        soundPool.autoPause()
                        mediaPlayer.release()
                    }
                    soundPool.play(sampleId, 1f, 1f, 1, 0, 1f)
                    soundPool.unload(sampleId)
                }
            }
        }
    }

    protected open fun getSoundPoolUsage() = AudioAttributes.USAGE_GAME

    protected open fun getSoundPoolContentType() = AudioAttributes.CONTENT_TYPE_SONIFICATION

    protected open fun getBlurRadius() = 22

    protected open fun getClickToAdsCount() = Random().nextInt(6) + 10

    private fun setupAds() {
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        interstitialAd = InterstitialAd(this).apply {
            adUnitId = getString(R.string.admob_interstitial_unit_id)
            adListener = object : AdListener() {
                override fun onAdClosed() {
                    clicksAdCounter = 0
                    clicksToShowAd = getClickToAdsCount()
                    interstitialAd.loadAd(AdRequest.Builder().build())
                }
            }
        }.also { it.loadAd(AdRequest.Builder().build()) }
    }

    abstract fun getSoundboardCategories(): ArrayList<SoundboardCategory>

    private fun onAdShowTrigger() = interstitialAd.let { if (it.isLoaded && ++clicksAdCounter == clicksToShowAd) it.show() }

    override fun onSoundItemClicked(item: SoundItem) {
        if (item.isLongSound) {
            mediaPlayer.release()
            if (!isMultiStreamsEnable) {
                soundPool.autoPause()
            }
            mediaPlayer = MediaPlayer.create(this, item.sound)
            mediaPlayer.start()
        } else {
            soundPool.load(this, item.sound, 1)
        }
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
        mediaPlayer.release()
    }

    override fun onSoundItemLongClicked(item: SoundItem) {
        if (isPermissionToWriteExternalStorageGranted()) {
            showShareAndSaveDialog(item)
        } else {
            requestPermissionsToExternalStorage()
        }
    }

    override fun onSaveButtonClick(dialog: android.support.v4.app.DialogFragment) {
        val shareAndSaveDialog = dialog as ShareAndSaveDialogFragment
        prepareToSaveSound(shareAndSaveDialog.soundItem!!.sound)
    }

    override fun onShareButtonClick(dialog: android.support.v4.app.DialogFragment) {
        val shareAndSaveDialog = dialog as ShareAndSaveDialogFragment
        shareSoundFile(shareAndSaveDialog.soundItem!!.sound)
    }

    private fun prepareToSaveSound(@RawRes soundRes: Int) {
        val path = Environment.getExternalStorageDirectory().path + File.separator + getString(R.string.app_name)
        val dir = File(path)
        if (dir.mkdirs() || dir.isDirectory) {
            saveSoundToExternalStorage(soundRes, path + File.separator + resources.getResourceEntryName(soundRes) + ".mp3")
        }
    }

    private fun saveSoundToExternalStorage(@RawRes soundRes: Int, path: String) {
        val input = resources.openRawResource(soundRes)
        val output = FileOutputStream(path)
        val soundData = input.readBytes()
        output.write(soundData)
        input.close()
        output.close()
        interstitialAd.let { if (it.isLoaded) it.show() }
        Toast.makeText(this, "Saved!\n$path", Toast.LENGTH_SHORT).show()
    }

    private fun shareSoundFile(@RawRes soundRes: Int){
        val input = resources.openRawResource(soundRes)
        val soundData = input.readBytes()
        val soundPath = File(this.applicationContext.filesDir, "sounds")
        val newFile = File(soundPath, resources.getResourceEntryName(soundRes) + ".mp3")
        soundPath.mkdirs()
        newFile.createNewFile()
        newFile.writeBytes(soundData)

        val uri = FileProvider.getUriForFile(
                this,
                getString(R.string.provider_authorities),
                newFile)
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "audio/mp3"
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_sound)))
    }

    private fun requestPermissionsToExternalStorage() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
    }

    private fun isPermissionToWriteExternalStorageGranted() =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE -> {
                val isPermissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                Toast.makeText(this,
                        if (isPermissionGranted) "Successful! Long click to save sound!" else "Something went wrong!",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBackgroundImage() {
        if (getBlurRadius() > 0) {
            Glide.with(this).load(R.drawable.bg_main)
                    .apply(bitmapTransform(BlurTransformation(getBlurRadius())))
                    .into(binding.bgImageView)
        } else {
            binding.bgImageView.setImageDrawable(getDrawable(R.drawable.bg_main))
        }
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = SoundboardPagerAdapter(supportFragmentManager, soundboardCategories)
        binding.viewPager.offscreenPageLimit = soundboardCategories.size
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar.let {
            it?.title = getString(R.string.app_label)
            binding.toolbar.setTitleTextColor(getToolbarItemsColor())
        }
    }

    private fun getToolbarItemsColor() = ContextCompat.getColor(this, R.color.toolbar_items_color)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        (0 until menu.size()).forEach {
            if (menu.getItem(it).itemId == R.id.action_repeat) {
                isMultiStreamsEnable = sharedPreferences.getBoolean(MULTI_STREAM, true)
                if (!isMultiStreamsEnable) {
                    menu.getItem(it).setIcon(R.drawable.ic_repeat_one_black_24dp)
                }
            }
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
            R.id.action_repeat -> {
                isMultiStreamsEnable = !isMultiStreamsEnable
                sharedPreferences.edit().putBoolean(MULTI_STREAM, isMultiStreamsEnable).apply()
                item.setIcon(if (isMultiStreamsEnable) R.drawable.ic_repeat_black_24dp else R.drawable.ic_repeat_one_black_24dp)
                        .icon.setColorFilter(getToolbarItemsColor(), PorterDuff.Mode.SRC_ATOP)
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

    private fun showShareAndSaveDialog(item: SoundItem){
        val dialog = ShareAndSaveDialogFragment()
        dialog.soundItem = item
        dialog.show(supportFragmentManager, "ShareAndSaveDialogFragment")
    }

    private fun showRateUs() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.rate_us_market))))
    }
}
