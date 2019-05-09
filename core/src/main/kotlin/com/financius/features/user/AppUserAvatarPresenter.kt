package com.financius.features.user

import com.financius.AttachListeningPresenter
import com.financius.data.AppUserDataSource
import com.financius.extensions.ignoreException
import com.financius.features.user.AppUserAvatarPresenter.Intent
import com.financius.features.user.AppUserAvatarPresenter.Intent.Select
import com.financius.features.user.AppUserAvatarPresenter.SideEffect
import com.financius.features.user.AppUserAvatarPresenter.SideEffect.ShowAppUserOptions
import com.financius.features.user.AppUserAvatarPresenter.State
import com.financius.features.user.AppUserAvatarPresenter.State.Loaded
import com.financius.features.user.AppUserAvatarPresenter.State.Placeholder
import com.financius.features.user.AppUserAvatarPresenter.View
import com.financius.models.AppUser
import com.financius.models.Image
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class AppUserAvatarPresenter(private val appUserDataSource: AppUserDataSource) : AttachListeningPresenter<Intent, State, SideEffect, View>() {

    init {
        launch {
            ignoreException {
                val appUser = appUserDataSource.getAppUser()
                setState(Loaded(appUser))
            }
        }
    }

    override fun onAttached(view: View) {
        super.onAttached(view)
        view.selects().receiveUntilDetached()
    }

    override fun getInitialState(): State = Placeholder

    override suspend fun mapIntentToSideEffect(currentState: State, intent: Intent): SideEffect? {
        return if (currentState is Loaded && intent is Select) ShowAppUserOptions
        else null
    }

    override fun onStateChanged(view: View, state: State) {
        when (state) {
            Placeholder -> view.showPlaceholder()
            is Loaded -> view.showPhoto(state.appUser.photo)
        }
    }

    override fun onSideEffectReceived(view: View, sideEffect: SideEffect) {
        when (sideEffect) {
            ShowAppUserOptions -> view.showAppUserOptions()
        }
    }

    sealed class Intent {
        object Select : Intent()
    }

    sealed class State {
        internal object Placeholder : State()
        internal data class Loaded(val appUser: AppUser) : State()
    }

    sealed class SideEffect {
        internal object ShowAppUserOptions : SideEffect()
    }

    interface View : AttachListeningPresenter.View {
        fun selects(): ReceiveChannel<Select>
        fun showPlaceholder()
        fun showPhoto(photo: Image)
        fun showAppUserOptions()
    }

}