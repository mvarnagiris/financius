package com.code44.finance.ui.user;

import com.code44.finance.backend.endpoint.users.Users;
import com.code44.finance.backend.endpoint.users.model.RegisterBody;
import com.code44.finance.backend.endpoint.users.model.UserAccount;
import com.code44.finance.ui.BaseActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class LoginActivity extends BaseActivity {
    public void asd() throws IOException {
        UserAccount builder = new Users.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null).build().register(new RegisterBody()).execute();
    }
}
