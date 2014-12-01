package com.code44.finance.ui.transactions;

import android.view.View;
import android.widget.Button;

import com.code44.finance.R;

public class TagsController implements View.OnClickListener {
    private final Button tagsButton;

    public TagsController(Button tagsButton) {
        this.tagsButton = tagsButton;
        this.tagsButton.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tagsButton:

        }
    }
}
