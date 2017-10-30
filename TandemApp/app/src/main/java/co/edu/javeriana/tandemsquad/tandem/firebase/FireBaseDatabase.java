package co.edu.javeriana.tandemsquad.tandem.firebase;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import co.edu.javeriana.tandemsquad.tandem.negocio.Marcador;
import co.edu.javeriana.tandemsquad.tandem.negocio.Mensaje;
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;

public class FireBaseDatabase {

    private Activity activity;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usuariosReference;
    private DatabaseReference recorridosReference;
    private DatabaseReference historiasReference;
    private DatabaseReference mensajesReference;
    private DatabaseReference marcadoresReference;

    private static Map<String, Usuario> userPool = new HashMap<>();
    private static int DATABASE_TIMEOUT_SECONDS = 15;

    public interface AsyncEventListener<T> {
        void onActionPerformed(T item);
    }

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

    public static long getUnixTimestamp(GregorianCalendar date) {
        return date.getTimeInMillis() / 1000;
    }
    public static GregorianCalendar getGregorianCalendar(long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp * 1000);
        return calendar;
    }

    public Usuario getUser(String id) {
        if (userPool.containsKey(id)) {
            if(userPool.get(id) != null) {
                return userPool.get(id);
            }
        }
        Map<String, String> unparsedUser = getGenericClassFromDatabase("users/" + id);
        if (unparsedUser != null) {
            try {
                String uid = unparsedUser.get("id");
                String nombre = unparsedUser.get("nombre");
                String correo = unparsedUser.get("correo");

                if (uid != null && nombre != null && correo != null) {
                    Usuario allegedUser = new Usuario(uid, nombre, correo);
                    userPool.put(id, allegedUser);
                    return allegedUser;
                } else {
                    throw new IllegalArgumentException("Unable to create User (Invalid data)");
                }
            } catch (Exception e) {
                Log.e("DATABASE EXCEPTION", "Invalid user data: " + e.getMessage());
                return null;
            }
        } else {
            Log.e("DATABASE INFO", "TIMEOUT getting user: " + id);
        }

        return null;
    }

    public void addMessageListener(final String uid, final AsyncEventListener<Mensaje> handler) {
        DatabaseReference messageReference = firebaseDatabase.getReference("messages/" + uid);
        messageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("DATABASE INFO", "Data changed");
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Map<String, String> unparsedMessage = (Map<String, String>) singleSnapshot.getValue();
                    try {
                        String plainSender = unparsedMessage.get("sender");
                        String plainReceiver = unparsedMessage.get("receiver");
                        String plainText = unparsedMessage.get("text");
                        String plainDate = unparsedMessage.get("date");

                        Usuario sender = getUser(plainSender);
                        Usuario receiver = getUser(plainReceiver);
                        GregorianCalendar date = getGregorianCalendar(Long.parseLong(plainDate));

                        if (sender != null && receiver != null && date != null && plainText != null) {
                            Mensaje mensaje = new Mensaje(plainText, sender, receiver, date, uid.equals(plainSender));
                            handler.onActionPerformed(mensaje);
                        } else {
                            throw new IllegalArgumentException("Unable to create Message");
                        }
                    } catch (Exception e) {
                        Log.e("DATABASE EXCEPTION", "Exception parsing Message. " + e.getMessage());
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DATABASE EXCEPTION", "Error en la consulta");
            }
        });

    }

    public List<Mensaje> getMessages(String uid) {
        List<Mensaje> mensajes = new LinkedList<>();
        List<Map<String, String>> unparsedMessages = getGenericListFromDatabase("messages/" + uid);
        for (Map<String, String> unparsedMessage : unparsedMessages) {
            try {
                String plainSender = unparsedMessage.get("sender");
                String plainReceiver = unparsedMessage.get("receiver");
                String plainText = unparsedMessage.get("text");
                String plainDate = unparsedMessage.get("date");

                Usuario sender = getUser(plainSender);
                Usuario receiver = getUser(plainReceiver);
                GregorianCalendar date = getGregorianCalendar(Long.parseLong(plainDate));

                if (sender != null && receiver != null && date != null && plainText != null) {
                    Mensaje mensaje = new Mensaje(plainText, sender, receiver, date, uid.equals(plainSender));
                    mensajes.add(mensaje);
                } else {
                    throw new IllegalArgumentException("Unable to create Message");
                }
            } catch (Exception e) {
                Log.e("DATABASE EXCEPTION", "Exception parsing Message. " + e.getMessage());
            }
        }
        return mensajes;
    }

    public List<Map<String, String>> getGenericListFromDatabase(String reference) {
        final LinkedBlockingQueue<List<Map<String, String>>> asyncQueue = new LinkedBlockingQueue<>();

        DatabaseReference messageReference = firebaseDatabase.getReference(reference);
        messageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Map<String, String>> objects = new LinkedList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, String> unparsedData = (Map<String, String>) child.getValue();
                    objects.add(unparsedData);
                }
                try {
                    asyncQueue.put(objects);
                } catch (InterruptedException e) {
                    Log.e("DATABASE EXCEPTION", e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DATABASE EXCEPTION", "Error en la consulta");
            }
        });
        try {
            return asyncQueue.poll(DATABASE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public Map<String, String> getGenericClassFromDatabase(String reference) {
        final LinkedBlockingQueue<Map<String, String>> asyncQueue = new LinkedBlockingQueue<>();

        DatabaseReference messageReference = firebaseDatabase.getReference(reference);
        messageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> unparsedData = (Map<String, String>) dataSnapshot.getValue();
                try {
                    asyncQueue.put(unparsedData);
                } catch (InterruptedException e) {
                    Log.e("DATABASE EXCEPTION", e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DATABASE EXCEPTION", "Error en la consulta");
            }
        });
        try {
            return asyncQueue.poll(DATABASE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void writeMessage(final Mensaje mensaje) {
        final Map<String, String> messageData = new HashMap<>();
        messageData.put("sender", mensaje.getEmisor().getId());
        messageData.put("receiver", mensaje.getReceptor().getId());
        messageData.put("date", String.valueOf(getUnixTimestamp(mensaje.getFecha())));
        messageData.put("text", mensaje.getTexto());

        DatabaseReference messageReference = firebaseDatabase.getReference("messages");
        messageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference ref = dataSnapshot.getRef();
                ref = ref.child(mensaje.getReceptor().getId());
                ref = ref.push();
                ref.setValue(messageData);

                ref = dataSnapshot.getRef();
                ref = ref.child(mensaje.getEmisor().getId());
                ref = ref.push();
                ref.setValue(messageData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DATABASE EXCEPTION", "Error en la consulta");
            }
        });
    }

    public void addTravel(final Recorrido recorrido )
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        final Map<String, String> messageData = new HashMap<>();
        String estado = recorrido.getEstado() == Recorrido.Estado.VIAJE ? "viaje" : "casual";
        messageData.put("inicio", recorrido.getInicio().getPosicion().toString());
        messageData.put("fin", recorrido.getFin().getPosicion().toString());
        messageData.put("hora", dateFormatter.format(recorrido.getHoraInicio().getTime()));
        messageData.put("tipo", estado);
        List<String> uids = new ArrayList<>();

        for( Usuario u : recorrido.getParticipantes())
        {
            uids.add(u.getId());
        }
        messageData.put("participantes", uids.toString());

        recorridosReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                DatabaseReference ref = dataSnapshot.getRef();
                ref = ref.child(recorrido.getParticipantes().get(0).getId());
                ref = ref.push();
                ref.setValue(messageData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e("DATABASE EXCEPTION", "Error en la consulta");
            }
        });
    }
}
