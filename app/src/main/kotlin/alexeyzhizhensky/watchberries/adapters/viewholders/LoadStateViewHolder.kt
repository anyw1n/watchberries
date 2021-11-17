package alexeyzhizhensky.watchberries.adapters.viewholders

import alexeyzhizhensky.watchberries.data.WbException
import alexeyzhizhensky.watchberries.databinding.ListItemLoadStateBinding
import android.content.Context
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView

class LoadStateViewHolder(
    private val binding: ListItemLoadStateBinding,
    private val context: Context,
    onRetryButtonClick: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { onRetryButtonClick() }
    }

    fun bind(loadState: LoadState) = binding.apply {
        if (loadState is LoadState.Error) {
            errorTextView.text = (loadState.error as? WbException)?.getMessage(context)
        }

        errorTextView.isVisible = loadState is LoadState.Error
        retryButton.isVisible = loadState is LoadState.Error
        progressAnimationView.isVisible = loadState is LoadState.Loading
    }
}
