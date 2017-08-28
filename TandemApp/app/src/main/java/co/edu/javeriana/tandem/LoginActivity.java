package co.edu.javeriana.tandem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

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

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        btn_login = (Button) findViewById(R.id.btn_login);

        Bundle bund = getIntent().getBundleExtra("bundle");
        users = bund.getStringArrayList("users");
        passs = bund.getStringArrayList("passs");

        homeIntent = new Intent(getBaseContext(), HomeActivity.class);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                boolean flag = false;

                for(int i = 0; i < users.size() && flag == false; i++)
                {
                    if(users.get(i).equals(user) && passs.get(i).equals(pass))
                    {
                        flag = true;
                    }
                }

                if(flag == true)
                {
                    startActivity(homeIntent);
                }
                else
                {
                    Toast.makeText(v.getContext(), "El usuario o contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
