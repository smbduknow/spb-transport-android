package me.smbduknow.transport.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_suggestion.view.*
import me.smbduknow.transport.R
import me.smbduknow.transport.domain.model.Route

class SuggestAdapter : RecyclerView.Adapter<SuggestAdapter.ViewHolder>() {

    private val RES_ID = R.layout.item_suggestion

    private var items: List<Route> = emptyList()
    private var itemClickListener: (route: Route) -> Unit = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(RES_ID, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setItems(items: List<Route>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (route: Route) -> Unit) {
        itemClickListener = listener
    }


    class ViewHolder(
            itemView: View,
            private var clickListener: (route: Route) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        companion object {
            private val COLOR_MAP = mapOf(
                    "bus" to R.color.vehicle_bus_dark,
                    "tram" to R.color.vehicle_tram_dark,
                    "trolley" to R.color.vehicle_trolley_dark
            )
            private val ICON_MAP = mapOf(
                    "bus" to R.drawable.ic_vehicle_bus,
                    "tram" to R.drawable.ic_vehicle_tram,
                    "trolley" to R.drawable.ic_vehicle_trolley
            )
        }

        fun bind(item: Route) {
            val colorRes = COLOR_MAP[item.typeLabel]!!
            val iconRes = ICON_MAP[item.typeLabel]!!
            itemView.suggest_label.text = item.label
            itemView.suggest_label.setTextColor(ContextCompat.getColor(itemView.context, colorRes))
            itemView.suggest_icon.setImageResource(iconRes)
            itemView.setOnClickListener { clickListener(item) }
        }

    }

}