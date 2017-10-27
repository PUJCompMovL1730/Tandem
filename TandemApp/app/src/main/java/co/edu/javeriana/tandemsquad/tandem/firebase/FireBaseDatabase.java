package co.edu.javeriana.tandemsquad.tandem.firebase;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseDatabase {

    private Activity activity;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public FireBaseDatabase(Activity acticity) {
        this.activity = acticity;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("message");

        databaseReference.setValue("Hola mundo");
    }
}
