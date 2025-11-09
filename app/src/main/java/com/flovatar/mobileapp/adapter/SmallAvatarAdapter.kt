package com.flovatar.mobileapp.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.eventbus.HideProgressEvent
import com.flovatar.mobileapp.model.AvatarModel
import com.flovatar.mobileapp.utils.AvatarUtils
import com.flovatar.mobileapp.view.AvatarClickedListener
import com.flovatar.mobileapp.view.PaginationArgs
import com.flovatar.mobileapp.view.ProgressViewHolder
import org.greenrobot.eventbus.EventBus

class SmallAvatarAdapter(
    val paginationArgs: PaginationArgs,
    val clickListener: AvatarClickedListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var CONTENT_TYPE = 1
    var PROGRESS_TYPE = 2
    var avatarList: List<AvatarModel> = mutableListOf()

    fun submitList(list: List<AvatarModel>) {
        avatarList = listOf()
        avatarList= list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == CONTENT_TYPE) {
            view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_small_avatar, parent, false)
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
            holder.bind(avatarList.get(position), position)
        }
    }

    override fun getItemCount(): Int {
        return avatarList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var avatarImage: AppCompatImageView

        init {
            avatarImage = itemView.findViewById(R.id.avatar)
        }

        fun bind(model: AvatarModel, position: Int) {
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
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (avatarList.size > 0 && position == 0) {
                            EventBus.getDefault().post(HideProgressEvent())
                        }
                        return false
                    }

                })
                .into(avatarImage)
            avatarImage.setOnClickListener {
                clickListener.onAvatarClicked(position)
            }
        }
    }
}