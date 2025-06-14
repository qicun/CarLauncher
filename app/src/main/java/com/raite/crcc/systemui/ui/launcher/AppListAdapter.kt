package com.raite.crcc.systemui.ui.launcher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.data.model.AppInfo

class AppListAdapter(private val onAppClickListener: (AppInfo) -> Unit) :
    ListAdapter<AppInfo, AppListAdapter.AppViewHolder>(AppDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appInfo = getItem(position)
        holder.bind(appInfo)
        holder.itemView.setOnClickListener { onAppClickListener(appInfo) }
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        private val appName: TextView = itemView.findViewById(R.id.appName)
        private val badge: View = itemView.findViewById(R.id.badge)

        fun bind(appInfo: AppInfo) {
            appIcon.setImageDrawable(appInfo.icon)
            appName.text = appInfo.label
            badge.visibility = if (appInfo.showBadge) View.VISIBLE else View.GONE
        }
    }

    private class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }
} 