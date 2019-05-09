package com.financius.features.user.avatar

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.financius.extensions.clicks
import com.financius.features.user.AppUserAvatarPresenter
import com.financius.features.user.AppUserAvatarPresenter.Intent.Select
import com.financius.features.user.avatar.AppUserAvatarModule.appUserAvatarPresenter
import com.financius.loadAvatar
import com.financius.loadAvatarPlaceholder
import com.financius.models.Image
import kotlinx.coroutines.channels.ReceiveChannel
import life.shank.android.AutoAttachable
import life.shank.android.AutoScoped

class AppUserAvatarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr), AppUserAvatarPresenter.View, AutoScoped, AutoAttachable {

    lateinit var showAppUserOptionsListener: () -> Unit

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        appUserAvatarPresenter.register(this)
    }

    override fun selects(): ReceiveChannel<Select> = clicks { Select }
    override fun showPlaceholder() = loadAvatarPlaceholder()
    override fun showPhoto(photo: Image) = loadAvatar(photo)
    override fun showAppUserOptions() = showAppUserOptionsListener()
}