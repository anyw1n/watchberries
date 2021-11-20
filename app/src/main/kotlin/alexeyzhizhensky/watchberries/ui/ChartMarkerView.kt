package alexeyzhizhensky.watchberries.ui

import alexeyzhizhensky.watchberries.R
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ChartMarkerView(context: Context) : MarkerView(context, R.layout.view_chart_marker) {

    private val dateTimeTextView: TextView = findViewById(R.id.markerDateTimeTextView)
    private val priceTextView: TextView = findViewById(R.id.markerPriceTextView)

    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withZone(ZoneId.systemDefault())

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val dateTime = LocalDateTime.ofEpochSecond(it.x.toLong(), 0, ZoneOffset.UTC)
            val price = it.y.toInt()

            dateTimeTextView.text = dateTime.format(dateTimeFormatter)
            priceTextView.text = if (price == 0) {
                context.getString(R.string.not_available)
            } else {
                context.getString(R.string.price, price)
            }
        }

        super.refreshContent(e, highlight)
    }
}
