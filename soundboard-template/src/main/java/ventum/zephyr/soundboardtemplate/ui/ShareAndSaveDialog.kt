package ventum.zephyr.soundboardtemplate.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import ventum.zephyr.soundboardtemplate.R
import ventum.zephyr.soundboardtemplate.model.SoundItem




class ShareAndSaveDialogFragment : DialogFragment() {

    interface ShareAndSaveDialogListener{
        fun onShareButtonClick(dialog: DialogFragment)
        fun onSaveButtonClick(dialog: DialogFragment)
    }

    var soundItem: SoundItem? = null
    var listener: ShareAndSaveDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val items = arrayOf(getString(R.string.activity_share), getString(R.string.activity_save))
        builder.setTitle(R.string.select_activity)
                .setItems(items, DialogInterface.OnClickListener { dialog, which ->
                    if (which == 0){
                        listener!!.onShareButtonClick(this)
                    }
                    else if (which == 1){
                        listener!!.onSaveButtonClick(this)
                    }
                })
        return builder.create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            this.listener = context as ShareAndSaveDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement NoticeDialogListener")
        }

    }
}