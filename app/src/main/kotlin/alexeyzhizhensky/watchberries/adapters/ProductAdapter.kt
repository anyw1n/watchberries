package alexeyzhizhensky.watchberries.adapters

import alexeyzhizhensky.watchberries.adapters.viewholders.ProductViewHolder
import alexeyzhizhensky.watchberries.data.room.Product
import alexeyzhizhensky.watchberries.databinding.ListItemProductBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ProductAdapter @Inject constructor(
    @ApplicationContext private val context: Context
) : PagingDataAdapter<Product, ProductViewHolder>(diffCallback) {

    private var onItemClick: ((Int) -> Unit)? = null

    fun setOnItemClickListener(onItemClick: (Int) -> Unit) {
        this.onItemClick = onItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemProductBinding.inflate(layoutInflater, parent, false)
        return ProductViewHolder(binding, context, onItemClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    private companion object {

        val diffCallback = object : DiffUtil.ItemCallback<Product>() {

            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.sku == newItem.sku
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.brand == newItem.brand &&
                        oldItem.title == newItem.title &&
                        oldItem.lastPrice == newItem.lastPrice &&
                        oldItem.trend == newItem.trend
            }
        }
    }
}
