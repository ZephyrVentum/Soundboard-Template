package ventum.zephyr.soundboardtemplate.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import ventum.zephyr.soundboardtemplate.R
import ventum.zephyr.soundboardtemplate.adapter.SoundsAdapter
import ventum.zephyr.soundboardtemplate.databinding.FragmentSoundboardBinding
import ventum.zephyr.soundboardtemplate.listener.SoundItemActionListener
import ventum.zephyr.soundboardtemplate.model.SoundItem
import ventum.zephyr.soundboardtemplate.model.SoundItems

class SoundboardFragment : Fragment() {

    private lateinit var binding: FragmentSoundboardBinding
    private lateinit var soundItems: SoundItems
    private lateinit var soundItemActionListener: SoundItemActionListener

    companion object {
        private const val SOUND_ITEMS_KEY = "SOUND_ITEMS_KEY"
        private const val ADS_ID_KEY = "ADS_ID_KEY"

        fun newInstance(soundItems: SoundItems, adsId: String) = SoundboardFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(SOUND_ITEMS_KEY, soundItems)
                putString(ADS_ID_KEY, adsId)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_soundboard, container, false)
        retainInstance = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { arguments ->
            soundItems = savedInstanceState?.let {
                it.getParcelableArrayList<SoundItem>(SOUND_ITEMS_KEY) as SoundItems
            } ?: arguments.getParcelableArrayList<SoundItem>(SOUND_ITEMS_KEY) as SoundItems
            binding.soundboardRecycleView.adapter = SoundsAdapter(soundItems, soundItemActionListener)
            binding.soundboardRecycleView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, resources.getInteger(R.integer.span_count))
            createAdsBanner(arguments.getString(ADS_ID_KEY))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(SOUND_ITEMS_KEY, soundItems)
    }

    private fun createAdsBanner(adsId: String?) {
        if (binding.root is ConstraintLayout) {
            val rootView: ConstraintLayout = binding.root as ConstraintLayout
            val adView = AdView(context)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = adsId
            adView.id = View.generateViewId()
            adView.loadAd(AdRequest.Builder().build())
            rootView.addView(adView)

            val constraintSet = ConstraintSet()
            constraintSet.clone(rootView)
            constraintSet.connect(adView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
            constraintSet.connect(adView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
            constraintSet.connect(adView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
            constraintSet.applyTo(rootView)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is SoundboardActivity) {
            soundItemActionListener = context
        }
    }
}