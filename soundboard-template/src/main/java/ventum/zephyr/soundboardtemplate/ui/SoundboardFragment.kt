package ventum.zephyr.soundboardtemplate.ui

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ventum.zephyr.soundboardtemplate.R
import ventum.zephyr.soundboardtemplate.adapter.SoundsAdapter
import ventum.zephyr.soundboardtemplate.databinding.FragmentSoundboardBinding
import ventum.zephyr.soundboardtemplate.listener.SoundItemActionListener
import ventum.zephyr.soundboardtemplate.model.SoundItems
import java.io.Serializable

class SoundboardFragment : Fragment() {

    private lateinit var binding: FragmentSoundboardBinding
    private lateinit var soundItems: SoundItems
    private lateinit var soundItemActionListener: SoundItemActionListener

    companion object {
        private const val SOUND_ITEMS_KEY = "SOUND_ITEMS_KEY"

        fun newInstance(soundItems: SoundItems): SoundboardFragment {
            val args = Bundle().apply { putSerializable(SOUND_ITEMS_KEY, soundItems as Serializable) }
            return SoundboardFragment().apply { arguments = args }
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
            binding.soundboardRecycleView.adapter = SoundsAdapter(soundItems, soundItemActionListener)
            binding.soundboardRecycleView.layoutManager = GridLayoutManager(context, 2)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is SoundboardActivity) {
            soundItemActionListener = context
        }
    }
}