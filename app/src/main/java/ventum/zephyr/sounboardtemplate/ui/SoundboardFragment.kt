package ventum.zephyr.sounboardtemplate.ui

import android.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ventum.zephyr.sounboardtemplate.R
import ventum.zephyr.sounboardtemplate.listener.SoundItemActionListener
import ventum.zephyr.sounboardtemplate.adapter.SoundsAdapter
import ventum.zephyr.sounboardtemplate.databinding.FragmentSoundboardBinding
import ventum.zephyr.sounboardtemplate.model.SoundItems
import java.io.Serializable

class SoundboardFragment : Fragment(), SoundItemActionListener {

    private lateinit var binding: FragmentSoundboardBinding
    private lateinit var soundItems: SoundItems

    companion object {
        private const val SOUND_ITEMS_KEY = "SOUND_ITEMS_KEY"

        fun newInstance(soundItems: SoundItems): SoundboardFragment {
            val args = Bundle()
            args.putSerializable(SOUND_ITEMS_KEY, soundItems as Serializable)
            val fragment = SoundboardFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_soundboard, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            soundItems = it.getSerializable(SOUND_ITEMS_KEY) as SoundItems
            binding.soundboardRecycleView.adapter = SoundsAdapter(soundItems, this)
            binding.soundboardRecycleView.layoutManager = GridLayoutManager(context, 2)
        }
    }

    override fun onSoundItemClicked(position: Int) {
        val mp = MediaPlayer.create(context, soundItems[position].sound)
        mp.start()
    }
}