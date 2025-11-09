package com.flovatar.mobileapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.model.LeaderboardItem

class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.Viewholder>() {
    var leaderboardList: List<LeaderboardItem> = listOf()

    fun submitList(list: List<LeaderboardItem>) {
        leaderboardList = list
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard, parent, false)
        return LeaderboardAdapter.Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.bind(leaderboardList.get(position), position)
    }

    override fun getItemCount(): Int {
        return leaderboardList.size
    }


    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val score: TextView
        val name: TextView
        val number: TextView
        val address: TextView

        init {
            score = itemView.findViewById(R.id.score)
            name = itemView.findViewById(R.id.name)
            number = itemView.findViewById(R.id.position)
            address = itemView.findViewById(R.id.address)
        }

        fun bind(item: LeaderboardItem, position: Int) {
            if (position % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#AE3DDF"))
            } else {
                itemView.setBackgroundColor(Color.parseColor("#9416CB"))
            }
            score.text = item.score.toString()
            number.text = (position + 1).toString()
            address.text = item.flowAddress
            name.text = item.name
        }
    }

}