package com.financius.features.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.financius.R
import kotlinx.android.synthetic.main.app_user_avatar_view.*
import life.shank.android.AutoScoped

class HomeFragment : Fragment(R.layout.home_fragment), AutoScoped {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appUserAvatarView.showAppUserOptionsListener = {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAppUserOptionsDialogFragment())
        }
    }

}