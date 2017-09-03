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
import android.widget.Toast;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout name_layout;
    private TextInputLayout username_layout;
    private TextInputLayout email_layout;
    private TextInputLayout cellphone_layout;
    private TextInputLayout password_layout;
    private TextInputLayout conf_password_layout;

    private EditText name;
    private EditText username;
    private EditText email;
    private EditText cellphone;
    private EditText password;
    private EditText conf_password;

    private Button btn_signup;
    private Intent homeIntent;
    private ArrayList<String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = (EditText) findViewById(R.id.signup_name);
        username = (EditText) findViewById(R.id.signup_username);
        email = (EditText) findViewById(R.id.signup_email);
        cellphone = (EditText) findViewById(R.id.signup_cellphone);
        password = (EditText) findViewById(R.id.signup_password);
        conf_password = (EditText) findViewById(R.id.signup_conf_password);

        name_layout = (TextInputLayout) findViewById(R.id.signup_name_layout);
        username_layout = (TextInputLayout) findViewById(R.id.signup_username_layout);
        email_layout = (TextInputLayout) findViewById(R.id.signup_email_layout);
        cellphone_layout = (TextInputLayout) findViewById(R.id.signup_cellphone_layout);
        password_layout = (TextInputLayout) findViewById(R.id.signup_password_layout);
        conf_password_layout = (TextInputLayout) findViewById(R.id.signup_conf_password_layout);

        btn_signup = (Button) findViewById(R.id.signup_btn_signup);

        name.addTextChangedListener(new MyTextWatcher(name));
        username.addTextChangedListener(new MyTextWatcher(username));
        email.addTextChangedListener(new MyTextWatcher(email));
        cellphone.addTextChangedListener(new MyTextWatcher(cellphone));
        password.addTextChangedListener(new MyTextWatcher(password));
        conf_password.addTextChangedListener(new MyTextWatcher(conf_password));

        users = getIntent().getStringArrayListExtra("users");

        homeIntent = new Intent(getBaseContext(), HomeActivity.class);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        boolean validForm = true;
        validForm &= Utils.validateText(this, name, name_layout);
        validForm &= Utils.validateText(this, username, username_layout);
        validForm &= Utils.validateText(this, email, email_layout);
        validForm &= Utils.validateText(this, cellphone, cellphone_layout);
        validForm &= Utils.validatePassword(this, password, password_layout);
        validForm &= Utils.validatePassword(this, conf_password, conf_password_layout);

        if (!validForm) {
            return;
        }

        String user = username.getText().toString();
        boolean flag = false;

        for(int i = 0; i < users.size() && flag == false; i++) {
            if(users.get(i).equals(user)) {
                flag = true;
            }
        }

        if(flag == true) {
            startActivity(homeIntent);
        } else {
            Snackbar.make(btn_signup, "Este usuaro ya existe", Snackbar.LENGTH_SHORT).show();
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
                case R.id.signup_name:
                    Utils.validateText(SignupActivity.this, name, name_layout);
                    break;
                case R.id.signup_username:
                    Utils.validatePassword(SignupActivity.this, username, username_layout);
                    break;
                case R.id.signup_email:
                    Utils.validatePassword(SignupActivity.this, email, email_layout);
                    break;
                case R.id.signup_cellphone:
                    Utils.validatePassword(SignupActivity.this, cellphone, cellphone_layout);
                    break;
                case R.id.signup_password:
                    Utils.validatePassword(SignupActivity.this, password, password_layout);
                    break;
                case R.id.signup_conf_password:
                    Utils.validatePassword(SignupActivity.this, conf_password, conf_password_layout);
                    break;

            }
        }
    }
}
