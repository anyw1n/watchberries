package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.Price
import alexeyzhizhensky.watchberries.data.room.Product
import alexeyzhizhensky.watchberries.databinding.FragmentProductDetailBinding
import alexeyzhizhensky.watchberries.utils.getRelativeDateTime
import alexeyzhizhensky.watchberries.utils.toast
import alexeyzhizhensky.watchberries.viewmodels.ProductDetailViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.RoundedCornersTransformation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private val viewModel: ProductDetailViewModel by viewModels()

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ProductDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)

        binding.setup()
        subscribeToFlows()

        viewModel.loadProduct(args.sku)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentProductDetailBinding.setup() {
        detailAppBar.toolbar.apply {
            setNavigationIcon(R.drawable.ic_baseline_chevron_left_24)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        openLinkButton.setOnClickListener {
            context?.let { viewModel.openProductPage(it) }
        }
    }

    private fun subscribeToFlows() = viewLifecycleOwner.lifecycleScope.apply {
        launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productFlow.filterNotNull().collectLatest { binding.bindProduct(it) }
                }

                launch {
                    viewModel.pricesFlow.collectLatest { binding.bindPrices(it) }
                }

                launch {
                    viewModel.uiStatesFlow.collectLatest { binding.setUiStates(it) }
                }
            }
        }

        launch {
            viewModel.eventsFlow.collectLatest(this@ProductDetailFragment::handleEvent)
        }
    }

    private fun FragmentProductDetailBinding.bindProduct(product: Product) {
        detailAppBar.toolbar.title = product.title

        detailProductImageView.load(product.imageUrl) {
            val cornerRadius = resources.getDimension(R.dimen.corner_radius)
            transformations(RoundedCornersTransformation(cornerRadius))
            placeholder(R.drawable.image_placeholder_animated_vector)
            crossfade(true)
        }

        detailPriceTextView.text = if (product.lastPrice.value == 0) {
            getString(R.string.not_available)
        } else {
            getString(R.string.price, product.lastPrice.value)
        }

        val relativeDateTime = getRelativeDateTime(requireContext(), product.lastPrice.datetime)
        detailDateTimeTextView.text = getString(R.string.last_update, relativeDateTime)

        detailTitleTextView.text = product.title
        detailBrandTextView.text = product.brand
        detailSkuTextView.text = product.sku.toString()
    }

    private fun FragmentProductDetailBinding.bindPrices(prices: List<Price>) {

    }

    private fun FragmentProductDetailBinding.setUiStates(states: ProductDetailViewModel.UiStates) {
        detailScrollView.isVisible =
            states.productState == ProductDetailViewModel.UiState.NotLoading
        detailProductAnimationView.isVisible =
            states.productState == ProductDetailViewModel.UiState.Loading
        pricesLineChart.isVisible = states.pricesState == ProductDetailViewModel.UiState.NotLoading
        detailPricesAnimationView.isVisible =
            states.pricesState == ProductDetailViewModel.UiState.Loading
    }

    private fun handleEvent(event: ProductDetailViewModel.Event) = when (event) {
        is ProductDetailViewModel.Event.ShowToast -> context?.toast(event.textRes)
        is ProductDetailViewModel.Event.ShowException -> context?.let {
            it.toast(event.exception.getMessage(it))
        }
    }
}
