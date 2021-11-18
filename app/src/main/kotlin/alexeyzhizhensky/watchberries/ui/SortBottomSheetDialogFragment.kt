package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.Sort
import alexeyzhizhensky.watchberries.data.WbException
import alexeyzhizhensky.watchberries.databinding.BottomSheetSortBinding
import alexeyzhizhensky.watchberries.utils.toast
import alexeyzhizhensky.watchberries.viewmodels.SortBottomSheetViewModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.ArrayRes
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: SortBottomSheetViewModel by viewModels()

    private var _binding: BottomSheetSortBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSortBinding.inflate(inflater, container, false)

        binding.bind()

        return binding.root
    }

    private fun BottomSheetSortBinding.bind() {
        context?.let {
            bySpinner.adapter = createAdapter(it, R.array.sort_field_names)
            orderSpinner.adapter = createAdapter(it, R.array.sort_order_names)
        }

        viewModel.getSort().also {
            bySpinner.setSelection(it.by.ordinal)
            orderSpinner.setSelection(it.order.ordinal)
        }

        applyButton.setOnClickListener {
            runCatching {
                val sort = Sort(
                    Sort.Field.values[bySpinner.selectedItemPosition],
                    Sort.Order.values[orderSpinner.selectedItemPosition]
                )

                viewModel.changeSort(sort)
            }.onFailure { exception ->
                val wbException = when (exception) {
                    is IndexOutOfBoundsException -> WbException.OutOfBounds(exception)
                    else -> WbException.Unknown(exception)
                }
                context?.let { it.toast(wbException.getMessage(it)) }
            }

            dialog?.cancel()
        }
    }

    private fun createAdapter(
        context: Context,
        @ArrayRes stringArray: Int
    ) = ArrayAdapter.createFromResource(context, stringArray, android.R.layout.simple_spinner_item)
        .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
}
