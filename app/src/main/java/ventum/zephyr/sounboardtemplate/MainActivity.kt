package ventum.zephyr.sounboardtemplate

import android.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import ventum.zephyr.sounboardtemplate.databinding.ActivityMainBinding
import ventum.zephyr.sounboardtemplate.model.SoundItem


class MainActivity : AppCompatActivity(), SoundItemActionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var soundItems: ArrayList<SoundItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Glide.with(this).load(R.drawable.bg_main)
                .apply(bitmapTransform(BlurTransformation(22)))
                .into(binding.bgImageView)
        soundItems = ArrayList()
        soundItems.add(SoundItem(R.drawable.img_victory, R.raw.victory_sound))
        soundItems.add(SoundItem(R.drawable.img_defend, R.raw.defend_sound))
        soundItems.add(SoundItem(R.drawable.blind, R.raw.blind_sound))

        binding.soundsRecycleView.adapter = SoundsAdapter(soundItems, this)
        binding.soundsRecycleView.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onSoundItemClicked(position: Int) {
        val mp = MediaPlayer.create(this, soundItems[position].sound)
        mp.start()
    }
}
