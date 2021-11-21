package alexeyzhizhensky.watchberries.adapters.viewholders

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.room.Product
import alexeyzhizhensky.watchberries.databinding.ListItemProductBinding
import alexeyzhizhensky.watchberries.utils.getRelativeDateTime
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation

class ProductViewHolder(
    private val binding: ListItemProductBinding,
    private val context: Context,
    onItemClick: ((Int) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    private val cornerRadius = context.resources.getDimension(R.dimen.corner_radius)

    private var sku: Int? = null

    init {
        binding.root.setOnClickListener { sku?.let { onItemClick?.invoke(it) } }
    }

    fun bind(product: Product) {
        sku = product.sku
        binding.bind(product)
    }

    private fun ListItemProductBinding.bind(product: Product) {
        productImageView.load(product.imageUrl) {
            transformations(RoundedCornersTransformation(cornerRadius))
            placeholder(R.drawable.image_placeholder_animated_vector)
            crossfade(true)
        }

        titleTextView.text = product.title
        brandTextView.text = product.brand
        skuTextView.text = product.sku.toString()

        val relativeDateTime = getRelativeDateTime(context, product.lastPrice.datetime)
        timeTextView.text = context.getString(R.string.last_update, relativeDateTime)

        priceTextView.text = if (product.lastPrice.value == 0) {
            context.getString(R.string.not_available_short)
        } else {
            context.getString(R.string.price, product.lastPrice.value)
        }

        val trendDrawable = ContextCompat.getDrawable(context, product.trend.drawableId)?.apply {
            setTint(product.trend.color)
        }
        trendImageView.load(trendDrawable)
    }
}
