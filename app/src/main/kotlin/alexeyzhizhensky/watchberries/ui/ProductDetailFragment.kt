package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.Price
import alexeyzhizhensky.watchberries.data.room.Product
import alexeyzhizhensky.watchberries.databinding.FragmentProductDetailBinding
import alexeyzhizhensky.watchberries.utils.getColorFromTheme
import alexeyzhizhensky.watchberries.utils.getRelativeDateTime
import alexeyzhizhensky.watchberries.utils.resetYAxisMinimum
import alexeyzhizhensky.watchberries.utils.setYAxisMinimum
import alexeyzhizhensky.watchberries.utils.toast
import alexeyzhizhensky.watchberries.viewmodels.ProductDetailViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.size.Scale
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

        override fun getAxisLabel(value: Float, axis: AxisBase?): String =
            LocalDateTime.ofEpochSecond(value.toLong(), 0, ZoneOffset.UTC).format(formatter)
    }

    private var refreshSwiped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(DELETE_REQUEST_KEY) { _, _ ->
            viewModel.removeProduct()
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
        detailAppBar.toolbar.inflateMenu(R.menu.menu_product_detail)
        detailAppBar.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_delete -> {
                    val action = ProductDetailFragmentDirections
                        .actionProductDetailFragmentToDeleteProductDialogFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        detailAppBar.toolbar.apply {
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        detailSwipeRefresh.setOnRefreshListener {
            refreshSwiped = true
            viewModel.updateProductInfo()
        }

        openLinkButton.setOnClickListener {
            context?.let(viewModel::openProductPage)
        }

        pricesLineChart.setup()

        pricesSwitch.setOnCheckedChangeListener { _, _ ->
            bindPrices(viewModel.pricesFlow.value)
        }
    }

    private fun LineChart.setup() {
        pricesDataSet.apply {
            mode = LineDataSet.Mode.STEPPED
            setDrawFilled(true)
            fillColor = context.getColorFromTheme(R.attr.colorChartFill)
            setDrawValues(false)
            val lineColor = context.getColorFromTheme(R.attr.colorChartLine)
            color = lineColor
            setCircleColor(lineColor)
        }

        description.isEnabled = false
        val textColor = context.getColorFromTheme(R.attr.colorText)
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = xAxisValueFormatter
            granularity = CHART_X_AXIS_GRANULARITY
            spaceMin = CHART_X_AXIS_SPACE
            spaceMax = CHART_X_AXIS_SPACE
            this.textColor = textColor
        }
        axisLeft.textColor = textColor
        axisRight.textColor = textColor
        legend.isEnabled = false
        isDoubleTapToZoomEnabled = false

        marker = ChartMarkerView(context, this, viewModel.getCurrency())
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
                    viewModel.uiStateFlow.collectLatest(::setUiState)
                }
            }
        }

        launch {
            viewModel.eventFlow.collectLatest(::handleEvent)
        }
    }

    private fun FragmentProductDetailBinding.bindProduct(product: Product) {
        detailAppBar.toolbar.title = product.title

        detailProductImageView.load(product.imageUrl) {
            scale(Scale.FILL)
            val cornerRadius = resources.getDimension(R.dimen.corner_radius)
            transformations(RoundedCornersTransformation(cornerRadius))
            placeholder(R.drawable.image_placeholder_animated_vector)
            crossfade(true)
        }

        detailPriceTextView.text = if (product.lastPrice.value == 0F) {
            getString(R.string.not_available_long)
        } else {
            "${product.lastPrice.value} ${product.lastPrice.currency.name}"
        }

        val relativeDateTime = context?.getRelativeDateTime(product.lastPrice.datetime)
        detailDateTimeTextView.text = getString(R.string.last_update, relativeDateTime)

        detailTitleTextView.text = product.title
        detailBrandTextView.text = product.brand
        detailSkuTextView.text = product.sku.toString()
    }

    private fun FragmentProductDetailBinding.bindPrices(prices: List<Price>) {
        var entries = prices.map {
            Entry(it.datetime.toEpochSecond(ZoneOffset.UTC).toFloat(), it.value)
        }

        if (pricesSwitch.isChecked) {
            pricesLineChart.setYAxisMinimum(CHART_Y_AXIS_MIN)
        } else {
            entries = entries.filter { it.y != 0F }
            pricesLineChart.resetYAxisMinimum()
        }

        pricesDataSet.values = entries
        pricesLineChart.apply {
            data = LineData(pricesDataSet)
            animateY(CHART_ANIMATION_DURATION)
        }
    }

    private fun setUiState(state: ProductDetailViewModel.UiState) {
        if (refreshSwiped && state == ProductDetailViewModel.UiState.Loading) {
            refreshSwiped = false
        } else {
            binding.detailSwipeRefresh.isRefreshing =
                state == ProductDetailViewModel.UiState.Loading
        }
    }

    private fun handleEvent(event: ProductDetailViewModel.Event) {
        when (event) {
            ProductDetailViewModel.Event.ProductDeleted -> {
                setFragmentResult(ProductListFragment.PRODUCT_DELETED_REQUEST_KEY, bundleOf())
                findNavController().navigateUp()
            }
            is ProductDetailViewModel.Event.ShowToast -> context?.toast(event.textRes)
            is ProductDetailViewModel.Event.ShowException -> context?.let {
                it.toast(event.exception.getMessage(it))
            }
        }
    }

    companion object {

        const val DELETE_REQUEST_KEY = "DELETE_SKU_REQUEST"

        private const val HOUR_IN_SECONDS = 60 * 60

        private const val CHART_DATA_SET_LABEL = "Prices"
        private const val CHART_X_AXIS_LABEL_FORMAT = "LLL d"
        private const val CHART_X_AXIS_GRANULARITY = 12F * HOUR_IN_SECONDS
        private const val CHART_X_AXIS_SPACE = 24F * HOUR_IN_SECONDS
        private const val CHART_Y_AXIS_MIN = 0F
        private const val CHART_ANIMATION_DURATION = 1000
    }
}
