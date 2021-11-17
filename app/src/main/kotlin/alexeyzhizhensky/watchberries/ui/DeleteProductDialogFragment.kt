package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.WbException
import alexeyzhizhensky.watchberries.utils.toast
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteProductDialogFragment : DialogFragment() {

    private val args: DeleteProductDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let {
        AlertDialog.Builder(it).apply {
            setTitle(R.string.delete_product_title)
            setMessage(R.string.delete_product_message)
            setPositiveButton(R.string.delete) { _, _ ->
                try {
                    sendResult()
                } catch (exception: WbException) {
                    context.toast(exception.getMessage(context))
                }
            }
            setNegativeButton(R.string.cancel) { _, _ -> dialog?.cancel() }
        }.create()
    } ?: throw IllegalStateException("Activity cannot be null")

    private fun sendResult() {
        if (args.sku <= 0) throw WbException.InvalidSku

        setFragmentResult(
            ProductListFragment.DELETE_SKU_REQUEST_KEY,
            bundleOf(ProductListFragment.SKU_KEY to args.sku)
        )
    }
}
