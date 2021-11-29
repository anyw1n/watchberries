package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.Price
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ChartMarkerView<T : ChartData<out IDataSet<out Entry>>?> @JvmOverloads constructor(
    context: Context,
    chart: Chart<T>? = null,
    private val currency: Price.Currency? = null
) : MarkerView(context, R.layout.view_chart_marker) {

    private val dateTimeTextView: TextView = findViewById(R.id.markerDateTimeTextView)
    private val priceTextView: TextView = findViewById(R.id.markerPriceTextView)

    private val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    init {
        chart?.let { chartView = it }
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val dateTime = LocalDateTime.ofEpochSecond(it.x.toLong(), 0, ZoneOffset.UTC)
                .atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault())
            val price = it.y

            dateTimeTextView.text = dateTime.format(formatter)
            priceTextView.text = if (price == 0F) {
                context.getString(R.string.not_available_long)
            } else {
                "$price ${currency?.name}"
            }
        }

        super.refreshContent(e, highlight)
    }
}
