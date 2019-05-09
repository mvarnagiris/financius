package com.financius.features.user.avatar

import com.financius.data.datasources.DataSourcesModule.appUserDataSource
import com.financius.features.user.AppUserAvatarPresenter
import life.shank.ShankModule
import life.shank.android.supportAutoAttach
import life.shank.scoped

object AppUserAvatarModule : ShankModule {

    val appUserAvatarPresenter = scoped { -> AppUserAvatarPresenter(appUserDataSource()) }.supportAutoAttach()

}