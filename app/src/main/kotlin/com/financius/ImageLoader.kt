package com.financius

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.financius.models.Image

object ImageLoader {

    fun load(imageView: ImageView, image: Image) {
        Glide.with(imageView)
            .load(image.uri)
            .into(imageView)
    }

    fun loadAvatar(imageView: ImageView, image: Image) {
        Glide.with(imageView)
            .load(image.uri.value)
            .circleCrop()
            .into(imageView)
    }

}

fun ImageView.load(image: Image) {
    ImageLoader.load(this, image)
}

fun ImageView.loadAvatar(image: Image) {
    ImageLoader.loadAvatar(this, image)
}

fun ImageView.loadAvatarPlaceholder() {
    setImageResource(R.drawable.app_user_avatar_placeholder)
}