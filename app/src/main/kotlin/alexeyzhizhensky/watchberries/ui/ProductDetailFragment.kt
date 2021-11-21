package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.Price
import alexeyzhizhensky.watchberries.data.room.Product
import alexeyzhizhensky.watchberries.databinding.FragmentProductDetailBinding
import alexeyzhizhensky.watchberries.utils.getRelativeDateTime
import alexeyzhizhensky.watchberries.utils.resetYAxisMinimum
import alexeyzhizhensky.watchberries.utils.setYAxisMinimum
import alexeyzhizhensky.watchberries.utils.toast
import alexeyzhizhensky.watchberries.viewmodels.ProductDetailViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private val viewModel: ProductDetailViewModel by viewModels()

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ProductDetailFragmentArgs by navArgs()

    private val pricesDataSet = LineDataSet(emptyList(), CHART_DATA_SET_LABEL)

    private val xAxisValueFormatter = object : ValueFormatter() {

        private val formatter = DateTimeFormatter.ofPattern(CHART_X_AXIS_LABEL_FORMAT)

        override fun getAxisLabel(value: Float, axis: AxisBase?): String = context?.let {
            LocalDateTime.ofEpochSecond(value.toLong(), 0, ZoneOffset.UTC).format(formatter)
        } ?: ""
    }
        }
    }

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

        pricesLineChart.setup()
    }

    private fun LineChart.setup() {
        pricesDataSet.apply {
            mode = LineDataSet.Mode.STEPPED
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(context, R.color.secondary_variant)
            setDrawValues(false)
            val lineColor = ContextCompat.getColor(context, R.color.primary)
            color = lineColor
            setCircleColor(lineColor)
        }

        description.isEnabled = false
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = xAxisValueFormatter
            granularity = CHART_X_AXIS_GRANULARITY
            spaceMin = CHART_X_AXIS_SPACE
            spaceMax = CHART_X_AXIS_SPACE
        }
        legend.isEnabled = false
        isDoubleTapToZoomEnabled = false

        val marker = ChartMarkerView(context).also {
            it.chartView = this
        }
        setMarker(marker)
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
            getString(R.string.not_available_long)
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
        if (prices.isEmpty()) return
        val graphValues = prices
            .plus(Price(LocalDateTime.now(ZoneOffset.UTC), prices.last().value))
            .dropWhile { it.value == 0 }
            .map { Entry(it.datetime.toEpochSecond(ZoneOffset.UTC).toFloat(), it.value.toFloat()) }
        pricesDataSet.values = graphValues
        pricesLineChart.apply {
            data = LineData(pricesDataSet)
            if (graphValues.minOf { it.y } == CHART_Y_AXIS_MIN) {
                setYAxisMinimum(CHART_Y_AXIS_MIN)
            } else {
                resetYAxisMinimum()
            }
            animateY(CHART_ANIMATION_DURATION)
        }
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

    private companion object {

        const val CHART_DATA_SET_LABEL = "Prices"
        const val CHART_Y_AXIS_MIN = 0F
        const val CHART_ANIMATION_DURATION = 1000
        private const val HOUR_IN_SECONDS = 60 * 60

        private const val CHART_X_AXIS_LABEL_FORMAT = "LLL d"
        private const val CHART_X_AXIS_GRANULARITY = 12F * HOUR_IN_SECONDS
        private const val CHART_X_AXIS_SPACE = 24F * HOUR_IN_SECONDS
    }
}
