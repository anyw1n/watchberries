package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.WbException
import alexeyzhizhensky.watchberries.databinding.DialogAddProductBinding
import alexeyzhizhensky.watchberries.utils.toast
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductDialogFragment : DialogFragment() {

    private var _binding: DialogAddProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let {
        _binding = DialogAddProductBinding.inflate(it.layoutInflater)

        binding.bind()

        AlertDialog.Builder(it).apply {
            setTitle(R.string.add_new_product)
            setView(binding.root)
            setPositiveButton(R.string.add) { _, _ -> addButtonClicked() }
            setNegativeButton(R.string.cancel) { _, _ -> dialog?.cancel() }
        }.create()
    } ?: throw IllegalStateException("Activity cannot be null")

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun DialogAddProductBinding.bind() {
        skuEditText.setOnEditorActionListener { _, actionCode, _ ->
            when (actionCode) {
                EditorInfo.IME_ACTION_DONE -> {
                    addButtonClicked()
                    dialog?.cancel()
                    true
                }
                else -> false
            }
        }
    }

    private fun addButtonClicked() {
        try {
            sendResult()
        } catch (exception: WbException) {
            context?.let { it.toast(exception.getMessage(it)) }
        }
    }

    private fun sendResult() {
        val sku = binding.skuEditText.text.toString().toIntOrNull()

        if (sku == null || sku <= 0) throw WbException.InvalidSku

        setFragmentResult(
            ProductListFragment.ADD_SKU_REQUEST_KEY,
            bundleOf(ProductListFragment.SKU_KEY to sku)
        )
    }
}
