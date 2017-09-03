package co.edu.javeriana.tandem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class Utils {

    public static BitmapDescriptor getBitmapDescriptor(Context context, int id_resource, int w, int h) {
        Drawable vectorDrawable = context.getDrawable(id_resource);
        vectorDrawable.setBounds(0, 0, w, h);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }



    public static void requestFocus(AppCompatActivity activity, View view) {
        if(view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static boolean validateText(AppCompatActivity activity, EditText username, TextInputLayout username_layout) {
        if (username.getText().toString().trim().isEmpty()) {
            username_layout.setError("Debes ingresar este campo");
            Utils.requestFocus(activity, username);
            return false;
        } else {
            username_layout.setErrorEnabled(false);
        }

        return true;
    }

    public static boolean validatePassword(AppCompatActivity activity, EditText password, TextInputLayout password_layout) {
        if (password.getText().toString().trim().isEmpty()) {
            password_layout.setError("Debes ingresar la contraseña");
            Utils.requestFocus(activity, password);
            return false;
        } else {
            password_layout.setErrorEnabled(false);
        }

        return true;
    }
}
