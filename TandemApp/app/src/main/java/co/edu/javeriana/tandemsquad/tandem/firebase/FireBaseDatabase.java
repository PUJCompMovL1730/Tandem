package co.edu.javeriana.tandemsquad.tandem.firebase;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.edu.javeriana.tandemsquad.tandem.negocio.Marcador;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;

public class FireBaseDatabase {

    private Activity activity;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usuariosReference;
    private DatabaseReference recorridosReference;
    private DatabaseReference historiasReference;
    private DatabaseReference mensajesReference;
    private DatabaseReference marcadoresReference;

    public FireBaseDatabase(Activity acticity) {
        this.activity = acticity;
        firebaseDatabase = FirebaseDatabase.getInstance();
        usuariosReference = firebaseDatabase.getReference("users");
        recorridosReference = firebaseDatabase.getReference("travels");
        historiasReference = firebaseDatabase.getReference("histories");
        mensajesReference = firebaseDatabase.getReference("messages");
        marcadoresReference = firebaseDatabase.getReference("markers");
    }

    public void writeUser(Usuario usuario) {
        DatabaseReference userReference = firebaseDatabase.getReference("users/" + usuario.getId());
        userReference.setValue(usuario);
    }

    public void writeMarker(Marcador marcador) {
        DatabaseReference marcadorReference = firebaseDatabase.getReference("marcador");
        String key = marcadorReference.push().getKey();
        marcadorReference = firebaseDatabase.getReference("marcador/" + key);
        marcadorReference.setValue(marcador);
    }
}
