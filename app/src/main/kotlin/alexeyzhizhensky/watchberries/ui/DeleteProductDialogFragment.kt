package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteProductDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let {
        AlertDialog.Builder(it).apply {
            setTitle(R.string.delete_product_title)
            setMessage(R.string.delete_product_message)
            setPositiveButton(R.string.delete) { _, _ ->
                setFragmentResult(ProductDetailFragment.DELETE_REQUEST_KEY, bundleOf())
            }
            setNegativeButton(R.string.cancel) { _, _ -> dialog?.cancel() }
        }.create()
    } ?: throw IllegalStateException("Activity cannot be null")
}
