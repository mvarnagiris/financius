package com.financius.features.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.financius.R
import com.financius.features.BaseFragment
import com.financius.features.splash.SplashModule.splashPresenter
import kotlinx.android.synthetic.main.splash_fragment.*
import life.shank.android.AutoAttachable
import life.shank.android.AutoScoped

class SplashFragment : BaseFragment(), SplashPresenter.View, AutoScoped, AutoAttachable {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashPresenter.register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.splash_fragment, container, false)

    override fun showLoading() {
        progressBar.isVisible = true
    }

    override fun showLoggedIn() {
        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
    }

    override fun showNotLoggedIn() {
        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
    }
}
