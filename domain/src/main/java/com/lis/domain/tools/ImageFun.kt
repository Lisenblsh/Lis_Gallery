package com.lis.domain.tools

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class ImageFun {
    fun setImage(imageView: ImageView, image: Any){
        Glide.with(imageView).load(image).into(imageView)
    }

    fun setBitmapImage(imageView: ImageView, thumbnail: Bitmap) {
        Glide.with(imageView)
            .asBitmap()
            .load(thumbnail)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    imageView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    fun setThumbnail(imageView: ImageView, image: Any){
        Glide.with(imageView).load(image).apply(RequestOptions().override(150, 150)).into(imageView);

    }
}