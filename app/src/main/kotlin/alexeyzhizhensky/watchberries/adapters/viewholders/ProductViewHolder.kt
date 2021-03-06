package alexeyzhizhensky.watchberries.adapters.viewholders

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.room.Product
import alexeyzhizhensky.watchberries.databinding.ListItemProductBinding
import alexeyzhizhensky.watchberries.utils.getColorFromTheme
import alexeyzhizhensky.watchberries.utils.getRelativeDateTime
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation

class ProductViewHolder(
    private val binding: ListItemProductBinding,
    private val context: Context,
    onItemClick: ((Int) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    private val cornerRadius = context.resources.getDimension(R.dimen.corner_radius)

    private var sku: Int? = null

    init {
        binding.root.setOnClickListener {
            sku?.let { onItemClick?.invoke(it) }
        }
    }

    fun bind(product: Product) {
        sku = product.sku
        binding.bind(product)
    }

    private fun ListItemProductBinding.bind(product: Product) {
        productImageView.load(product.imageUrl) {
            scale(Scale.FILL)
            transformations(RoundedCornersTransformation(cornerRadius))
            placeholder(R.drawable.image_placeholder_animated_vector)
            crossfade(true)
        }

        titleTextView.text = product.title
        brandTextView.text = product.brand
        skuTextView.text = product.sku.toString()

        val relativeDateTime = context.getRelativeDateTime(product.lastPrice.datetime)
        dateTimeTextView.text = context.getString(R.string.last_update, relativeDateTime)

        priceTextView.text = if (product.lastPrice.value == 0F) {
            context.getString(R.string.not_available_short)
        } else {
            "${product.lastPrice.value} ${product.lastPrice.currency.name}"
        }

        val trendDrawable = ContextCompat.getDrawable(context, product.trend.drawableId)?.apply {
            setTint(context.getColorFromTheme(product.trend.colorAttr))
        }
        trendImageView.load(trendDrawable)
    }
}
