package co.edu.javeriana.tandem;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout username_layout;
    private TextInputLayout password_layout;

    private EditText username;
    private EditText password;

    private Button btn_login;
    private Intent homeIntent;
    private ArrayList<String> users;
    private ArrayList<String> passs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username_layout = (TextInputLayout) findViewById(R.id.login_username_layout);
        password_layout = (TextInputLayout) findViewById(R.id.login_password_layout);

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);

        btn_login = (Button) findViewById(R.id.login_btn_login);

        username.addTextChangedListener(new MyTextWatcher(username));
        password.addTextChangedListener(new MyTextWatcher(password));


        Bundle bundle = getIntent().getBundleExtra("bundle");
        users = bundle.getStringArrayList("users");
        passs = bundle.getStringArrayList("passs");

        homeIntent = new Intent(getBaseContext(), HomeActivity.class);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        boolean validForm = true;
        validForm &= Utils.validateText(this, username, username_layout);
        validForm &= Utils.validatePassword(this, password, password_layout);

        if (!validForm) {
            return;
        }

        String user = username.getText().toString();
        String pass = password.getText().toString();
        boolean flag = false;

        for(int i = 0; i < users.size() && flag == false; i++) {
            if(users.get(i).equals(user) && passs.get(i).equals(pass)) {
                flag = true;
            }
        }

        if(flag == true) {
            startActivity(homeIntent);
        } else {
            Snackbar.make(btn_login, "Wrong username or password", Snackbar.LENGTH_SHORT).show();
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.login_username:
                    Utils.validateText(LoginActivity.this, username, username_layout);
                    break;
                case R.id.login_password:
                    Utils.validatePassword(LoginActivity.this, password, password_layout);
                    break;
            }
        }
    }
}
