package com.flovatar.mobileapp.model

import android.graphics.drawable.PictureDrawable
import com.caverock.androidsvg.SVG
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Base64
import java.io.ByteArrayOutputStream


data class AvatarModel(
    @SerializedName("flow_id")
    val flowId: Int,
    @SerializedName("flow_address")
    val flowAddress: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("full_svg_no_bg")
    val svg: String,
    @SerializedName("rare_count")
    val rareCount: Int,
    @SerializedName("legendary_count")
    val legendaryCount: Int,
    @SerializedName("epic_count")
    val epicCount: Int,
    var image: String?

) : Serializable {

    fun createDrawable() {
        val svgString: String = svg
        val svg = SVG.getFromString(svgString)
        val drawable = PictureDrawable(svg.renderToPicture(500, 500))
        val bitmap = Bitmap.createBitmap(
            drawable.getIntrinsicWidth(),
            drawable.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawPicture(drawable.getPicture())
        image = bitmapToString(bitmap)
    }

    fun bitmapToString(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}