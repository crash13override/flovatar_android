package com.flovatar.mobileapp.adapter

import android.graphics.Bitmap
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.model.AvatarModel
import com.flovatar.mobileapp.utils.AvatarUtils
import com.flovatar.mobileapp.view.PaginationArgs
import com.flovatar.mobileapp.view.ProgressViewHolder

class BigAvatarAdapter(val paginationArgs: PaginationArgs) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var CONTENT_TYPE = 1
    var PROGRESS_TYPE = 2
    var avatarList: MutableList<AvatarModel> = mutableListOf()

    fun submitList(list: List<AvatarModel>) {
        avatarList.clear()
        avatarList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == CONTENT_TYPE) {
            view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_big_avatar, parent, false)
            return ViewHolder(view)
        } else {
            view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
            return ProgressViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (paginationArgs.isShowProgress && position + 1 == avatarList.size) {
            PROGRESS_TYPE
        } else {
            CONTENT_TYPE
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(avatarList.get(position))
        }
    }

    override fun getItemCount(): Int {
        return avatarList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var avatarImage: AppCompatImageView
        private var rareCount: AppCompatTextView
        private var legendaryCount: AppCompatTextView
        private var epicCount: AppCompatTextView
        private val name: AppCompatTextView

        init {
            avatarImage = itemView.findViewById(R.id.avatar)
            rareCount = itemView.findViewById(R.id.rareCount)
            legendaryCount = itemView.findViewById(R.id.legendaryCount)
            epicCount = itemView.findViewById(R.id.epicCount)
            name = itemView.findViewById(R.id.name)
        }

        fun bind(model: AvatarModel) {
            model.createDrawable()
            val requestOptions: RequestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .centerCrop()
                .dontAnimate()
                .dontTransform()
                .priority(Priority.IMMEDIATE)
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT)

            Glide.with(itemView.context)
                .load(AvatarUtils.stringToBitMap(model.image))
                .apply(requestOptions)
                .into(avatarImage)

            rareCount.text = model.rareCount.toString()
            legendaryCount.text = model.legendaryCount.toString()
            epicCount.text = model.epicCount.toString()
            val nameText = model.name
            if (!TextUtils.isEmpty(nameText)) {
                name.text = nameText
            }
        }
    }
}