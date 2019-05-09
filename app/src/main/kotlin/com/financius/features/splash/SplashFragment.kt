package com.financius.features.splash

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.financius.R
import com.financius.features.splash.SplashModule.splashPresenter
import kotlinx.android.synthetic.main.splash_fragment.*
import life.shank.android.AutoAttachable
import life.shank.android.AutoScoped

class SplashFragment : Fragment(R.layout.splash_fragment), SplashPresenter.View, AutoScoped, AutoAttachable {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashPresenter.register(this)
    }

    override fun showLoading() = with(progressBar) { isVisible = true }
    override fun showLoggedIn() = findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
    override fun showNotLoggedIn() = findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
}
