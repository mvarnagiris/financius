package com.code44.finance.ui.settings.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.code44.finance.R;
import com.code44.finance.ui.BaseFragment;

public class FaqFragment extends BaseFragment {
    public static FaqFragment newInstance() {
        return new FaqFragment();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faq, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final WebView web_V = (WebView) view.findViewById(R.id.web_V);

        // Setup
        web_V.loadUrl("https://github.com/mvarnagiris/Financius/blob/dev/README.md");
    }
}
