package alexeyzhizhensky.watchberries.adapters

import alexeyzhizhensky.watchberries.databinding.ListItemTopBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TopItemAdapter : RecyclerView.Adapter<TopItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTopBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = Unit

    override fun getItemCount() = 1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
