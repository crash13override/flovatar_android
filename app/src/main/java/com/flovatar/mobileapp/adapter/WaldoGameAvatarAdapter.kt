package com.flovatar.mobileapp.adapter

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.util.CoilUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.flovatar.mobileapp.R
import com.flovatar.mobileapp.model.AvatarModel
import com.flovatar.mobileapp.utils.AvatarUtils
import com.flovatar.mobileapp.view.AvatarClickedListener
import okhttp3.OkHttpClient
import kotlin.random.Random


class WaldoGameAvatarAdapter(val clickListener: AvatarClickedListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var avatarList: List<AvatarModel> = listOf()
    var level: Int = 0
    fun submitList(level: Int, list: List<AvatarModel>) {
        this.level = level
        avatarList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_game_flovatar, parent, false)
        return ViewHolder(view)
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
        private var layoutParams: LinearLayout.LayoutParams? = null
        private var widthPixels: Int = 0
        private var heightPixels: Int = 0

        init {
            avatarImage = itemView.findViewById(R.id.avatar)
        }

        fun setSize(context: Context, width: Float, height: Float, position: Int) {
            layoutParams = LinearLayout.LayoutParams(width.toInt(), height.toInt())
            if (position >= getSizeOfTheRowByLevel()) {
                layoutParams?.setMargins(
                    getSideMargin(),
                    getTopMarginByLevel(),
                    0, getBottomMarginByLevel()
                )
            } else {
                layoutParams?.setMargins(
                    getSideMargin(), 0,
                    0, getBottomMarginByLevel()
                )
            }
            widthPixels = dpToPx(width, context)
            heightPixels = dpToPx(height, context)
        }

        private fun getSideMargin(): Int {
            return Random.nextInt(-20, 20)
        }

        private fun getTopMarginByLevel(): Int {
            var margin = 0
            val resources = itemView.resources
            val width: Float =
                resources.displayMetrics.widthPixels / getSizeOfTheRowByLevel().toFloat()
            val height: Float = width * 1.7F
            margin = (height * 0.26.unaryMinus()).toInt()
            val maxMargin =(height * 0.45.unaryMinus()).toInt()
            return Random.nextInt(maxMargin, margin)
        }

        private fun getBottomMarginByLevel(): Int {
            return Random.nextInt(-17, 15)
        }

        private fun getSizeOfTheRowByLevel(): Int {
            var size = 0
            when (level) {
                1, 2 -> size = 7
                3, 4 -> size = 8
                5, 6 -> size = 9
                7, 8 -> size = 10
                9, 10 -> size = 11
                11, 12 -> size = 12
                else -> {
                    size = 13
                }
            }
            return size
        }

        fun dpToPx(sp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sp,
                context.getResources().getDisplayMetrics()
            ).toInt()
        }

        fun bind(model: AvatarModel, position: Int) {
            val resources = itemView.resources
            val width: Float =
                resources.displayMetrics.widthPixels / getSizeOfTheRowByLevel().toFloat()
            val height: Float = width * 1.7F
            setSize(itemView.context, width, height, position)

            avatarImage.setLayoutParams(layoutParams)
            val requestOptions: RequestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .dontAnimate()
                .dontTransform()
                .priority(Priority.IMMEDIATE)
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT)

            if (model.flowId.equals(4548)) {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_waldo)
                    .apply(requestOptions)
                    .into(avatarImage)
            } else {
                if (model.image != null) {
                    Glide.with(itemView.context)
                        .load(AvatarUtils.stringToBitMap(model.image))
                        .apply(requestOptions)
                        .into(avatarImage)
                }
            }

            avatarImage.setOnClickListener {
                clickListener.onAvatarClicked(model.flowId)
            }
        }


    }
}