package alexeyzhizhensky.watchberries.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs

class SingleChoiceListDialogFragment : DialogFragment() {

    private val args: SingleChoiceListDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let {
        AlertDialog.Builder(it).apply {
            setTitle(args.title)
            setItems(args.items) { _, index ->
                setFragmentResult(args.key, bundleOf(SettingsFragment.INDEX_KEY to index))
            }
        }.create()
    } ?: throw IllegalStateException("Activity cannot be null")
}
