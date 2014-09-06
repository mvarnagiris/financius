package com.code44.finance.ui.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.api.User;
import com.code44.finance.ui.BaseFragment;

import javax.inject.Inject;

public class UserFragment extends BaseFragment implements View.OnClickListener {
    @Inject User user;

    private ImageView cover_IV;
    private ImageView photo_IV;
    private TextView name_TV;
    private Button login_B;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        cover_IV = (ImageView) view.findViewById(R.id.cover_IV);
        photo_IV = (ImageView) view.findViewById(R.id.photo_IV);
        name_TV = (TextView) view.findViewById(R.id.name_TV);
        login_B = (Button) view.findViewById(R.id.login_B);

        // Setup
        login_B.setOnClickListener(this);
    }

    @Override public void onResume() {
        super.onResume();
        updateViews();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_B:
                LoginActivity.start(getActivity());
                break;
        }
    }

//    @SuppressWarnings("UnusedDeclaration")
// TODO    public void onEventMainThread(User.UserChangedEvent event) {
//        updateViews();
//    }

    private void updateViews() {
        if (user.isLoggedIn()) {
            cover_IV.setVisibility(View.VISIBLE);
            photo_IV.setVisibility(View.VISIBLE);
            name_TV.setVisibility(View.VISIBLE);
            login_B.setVisibility(View.GONE);

            name_TV.setText(user.getFirstName() + " " + user.getLastName());
        } else {
            cover_IV.setVisibility(View.GONE);
            photo_IV.setVisibility(View.GONE);
            name_TV.setVisibility(View.GONE);
            login_B.setVisibility(View.VISIBLE);
        }
    }

    private void logout() {
        user.logout();
    }
}
