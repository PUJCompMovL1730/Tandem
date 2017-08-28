package co.edu.javeriana.tandem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

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

        btn_signup = (Button) findViewById(R.id.btn_signup);
        name = (EditText) findViewById(R.id.signup_name);
        username = (EditText) findViewById(R.id.signup_username);
        email = (EditText) findViewById(R.id.signup_email);
        cellphone = (EditText) findViewById(R.id.signup_cellphone);
        password = (EditText) findViewById(R.id.signup_password);
        conf_password = (EditText) findViewById(R.id.signup_conf_password);

        users = getIntent().getStringArrayListExtra("users");

        homeIntent = new Intent(getBaseContext(), HomeActivity.class);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                boolean flagU = false;

                for(int i = 0; i < users.size() && flagU == false; i++)
                {
                    if(users.get(i).equals(user))
                    {
                        flagU = true;
                    }
                }

                if(flagU == false)
                {
                    startActivity(homeIntent);
                }
                else
                {
                    Toast.makeText(v.getContext(), "El usuario ya existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
