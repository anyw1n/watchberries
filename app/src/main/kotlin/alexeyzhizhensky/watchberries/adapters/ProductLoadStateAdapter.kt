package alexeyzhizhensky.watchberries.adapters

import alexeyzhizhensky.watchberries.adapters.viewholders.LoadStateViewHolder
import alexeyzhizhensky.watchberries.databinding.ListItemLoadStateBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class ProductLoadStateAdapter(
    private val context: Context,
    private val onRetryButtonClick: () -> Unit
) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemLoadStateBinding.inflate(layoutInflater, parent, false)
        return LoadStateViewHolder(binding, context, onRetryButtonClick)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}
