package com.financius.features.splash

import com.financius.data.datasources.DataSourcesModule.authenticationDataSource
import life.shank.ShankModule
import life.shank.android.supportAutoAttach
import life.shank.scoped

object SplashModule : ShankModule {

    val splashPresenter = scoped { -> SplashPresenter(authenticationDataSource()) }.supportAutoAttach()

}
