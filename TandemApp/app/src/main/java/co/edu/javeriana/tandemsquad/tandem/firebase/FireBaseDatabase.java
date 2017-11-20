package co.edu.javeriana.tandemsquad.tandem.firebase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
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
import co.edu.javeriana.tandemsquad.tandem.negocio.Historia;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;

public class FireBaseDatabase {

    private Activity activity;
    private FirebaseDatabase firebaseDatabase;
    private FireBaseStorage firebaseStorage;
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
        firebaseStorage = new FireBaseStorage(activity) {
            @Override
            protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {
            }
        };
        usuariosReference = firebaseDatabase.getReference("users");
        recorridosReference = firebaseDatabase.getReference("travels");
        historiasReference = firebaseDatabase.getReference("histories");
        mensajesReference = firebaseDatabase.getReference("messages");
        marcadoresReference = firebaseDatabase.getReference("markers");
    }

    public void updateFriendsList(final Usuario usuario) {
        final DatabaseReference userReference = firebaseDatabase.getReference("users/" + usuario.getId());
        String amigos = "";
        boolean friends = false;
        if (!usuario.getAmigos().isEmpty()) {
            for (Usuario u : usuario.getAmigos()) {
                if (friends) {
                    amigos += ",";
                }
                friends = true;
                amigos += u.getId();
            }
        }
        userReference.child("amigos").setValue(amigos);
    }

    public void writeUser(final Usuario usuario) {

        final DatabaseReference userReference = firebaseDatabase.getReference("users/" + usuario.getId());
        Log.i("User ID", usuario.getId());
        final Map<String, String> messageData = new HashMap<>();
        messageData.put("correo", usuario.getCorreo());
        messageData.put("id", usuario.getId());
        messageData.put("nombre", usuario.getNombre());
        messageData.put("telefono", usuario.getTelefono());

        String amigos = "";

        if (!usuario.getAmigos().isEmpty()) {
            for (Usuario u : usuario.getAmigos()) {
                amigos += u.getId() + ",";
            }
        }

        messageData.put("amigos", amigos.trim());
        userReference.setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    onSuccessWriteUser(task);
                } else {
                    onFailureWriteUser(task);
                }
            }
        });
    }

    protected void onSuccessWriteUser(Task<Void> task) {}
    protected void onFailureWriteUser(Task<Void> task) {}

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

    public List<Usuario> getFriendsFromUids(String amigos) {
        List<Usuario> amigosList = new ArrayList<>();
        String[] amigosArray = amigos.split(",");

        for (String a : amigosArray) {
            Usuario u = getUserWithoutFriends(a);

            if (u != null) {
                amigosList.add(u);
            }
        }

        return amigosList;
    }

    public Usuario getUserWithoutFriends(String id) {
        if (userPool.containsKey(id)) {
            if (userPool.get(id) != null) {
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
                    final Usuario allegedUser = new Usuario(uid, nombre, correo);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            firebaseStorage.downloadUserImage(allegedUser);
                        }
                    }).start();
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

    //TODO Revisar
    public List<Usuario> getAllUsers() {
        List<Usuario> usuarios = new LinkedList<>();
        List<Map<String, String>> unparsedUsers = getGenericListFromDatabase("users");
        for (Map<String, String> unparsedUser : unparsedUsers) {
            try {
                String uid = unparsedUser.get("id");
                String nombre = unparsedUser.get("nombre");
                String correo = unparsedUser.get("correo");

                if (uid != null && nombre != null && correo != null) {
                    final Usuario allegedUser = new Usuario(uid, nombre, correo);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            firebaseStorage.downloadUserImage(allegedUser);
                        }
                    }).start();
                    usuarios.add(allegedUser);
                } else {
                    throw new IllegalArgumentException("Unable to create User (Invalid data)");
                }
            } catch (Exception e) {
                Log.e("DATABASE EXCEPTION", "Invalid user data: " + e.getMessage());
                return null;
            }
        }
        return usuarios;
    }

    public Usuario getUser(String id) {
        if (userPool.containsKey(id)) {
            if (userPool.get(id) != null) {
                return userPool.get(id);
            }
        }
        Map<String, String> unparsedUser = getGenericClassFromDatabase("users/" + id);
        if (unparsedUser != null) {
            try {
                String uid = unparsedUser.get("id");
                String nombre = unparsedUser.get("nombre");
                String correo = unparsedUser.get("correo");
                String amigos = unparsedUser.get("amigos");
                if (uid != null && nombre != null && correo != null) {
                    final Usuario allegedUser = new Usuario(uid, nombre, correo);
                    if (amigos != null) {
                        List<Usuario> amigosList = getFriendsFromUids(amigos);
                        allegedUser.setAmigos(amigosList);
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            firebaseStorage.downloadUserImage(allegedUser);
                        }
                    }).start();
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

    public void removeMessageEventListener(ValueEventListener listener, String uid) {
        DatabaseReference messageReference = firebaseDatabase.getReference("messages/" + uid);
        messageReference.removeEventListener(listener);
    }

    public ValueEventListener addMessageListener(final String uid, final AsyncEventListener<Mensaje> handler) {
        DatabaseReference messageReference = firebaseDatabase.getReference("messages/" + uid);
        return messageReference.addValueEventListener(new ValueEventListener() {
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

    public List<Historia> getAllStories() {
        List<Historia> historias = new LinkedList<>();
        List<Map<String, String>> unparsedStories = getGenericListFromDatabase("stories");
        for (Map<String, String> unparsedStory : unparsedStories) {
            try {
                String mensaje = unparsedStory.get("mensaje");
                String usuarioId = unparsedStory.get("usuario");
                String fechaInt = unparsedStory.get("fecha");
                String imagenUri = unparsedStory.get("imagen");
                if (mensaje != null && usuarioId != null && fechaInt != null && imagenUri != null) {
                    Usuario usuario = getUser(usuarioId);
                    if (usuario == null) {
                        throw new NullPointerException("Null user when getting story");
                    }
                    GregorianCalendar fecha = getGregorianCalendar(Long.parseLong(fechaInt));
                    final Historia historia = new Historia(mensaje, usuario, fecha, imagenUri);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            firebaseStorage.downloadStoryImage(historia);
                        }
                    }).start();
                    historias.add(historia);
                } else {
                    throw new IllegalArgumentException("Unable to create Story (Invalid data)");
                }
            } catch (Exception e) {
                Log.e("DATABASE EXCEPTION", "Exception parsing Message. " + e.getMessage());
            }
        }
        return historias;
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
                    if (unparsedData != null) {
                        objects.add(unparsedData);
                    }
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
                    if (unparsedData != null) {
                        asyncQueue.put(unparsedData);
                    }
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

    public void addTravel(final Recorrido recorrido) {
        final Map<String, String> messageData = new HashMap<>();
        String estado, tipo, privacidad;
        estado = "finalizado";
        if (recorrido.getEstado() == Recorrido.Estado.PLANEADO) {
            estado = "planeado";
        } else if (recorrido.getEstado() == Recorrido.Estado.EN_CURSO) {
            estado = "en_curso";
        }
        tipo = "instantaneo";
        if (recorrido.getTipo() == Recorrido.Tipo.VIAJE) {
            tipo = "viaje";
        } else if (recorrido.getTipo() == Recorrido.Tipo.FRECUENTE) {
            tipo = "frecuente";
        }
        privacidad = recorrido.getPrivacidad() == Recorrido.Privacidad.PUBLICO ? "publico" : "privado";
        messageData.put("inicioLat", String.valueOf(recorrido.getInicio().getPosicion().latitude));
        messageData.put("inicioLng", String.valueOf(recorrido.getInicio().getPosicion().longitude));
        messageData.put("finLat", String.valueOf(recorrido.getFin().getPosicion().latitude));
        messageData.put("finLng", String.valueOf(recorrido.getFin().getPosicion().longitude));
        messageData.put("fecha", recorrido.getFecha());
        messageData.put("hora", recorrido.getHora());
        messageData.put("estado", estado);
        messageData.put("tipo", tipo);
        messageData.put("privacidad", privacidad);
        List<String> uids = new ArrayList<>();
        for (Usuario u : recorrido.getParticipantes()) {
            uids.add(u.getId());
        }
        messageData.put("participantes", uids.toString());
        recorridosReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference ref = dataSnapshot.getRef();
                ref = ref.child(recorrido.getParticipantes().get(0).getId());
                ref = ref.push();
                ref.setValue(messageData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DATABASE EXCEPTION", "Error en la consulta");
            }
        });
    }

    public List<Recorrido> getTravels(String uid) {
        List<Recorrido> recorridos = new LinkedList<>();
        List<Map<String, String>> unparsedTravels = getGenericListFromDatabase("travels/" + uid);
        for (Map<String, String> unparsedTravel : unparsedTravels) {
            try {
                String inicioLat = unparsedTravel.get("inicioLat");
                String inicioLng = unparsedTravel.get("inicioLng");
                String finLat = unparsedTravel.get("finlat");
                String finLng = unparsedTravel.get("finLng");
                String fecha = unparsedTravel.get("fecha");
                String hora = unparsedTravel.get("hora");
                String estado = unparsedTravel.get("estado");
                String tipo = unparsedTravel.get("tipo");
                String privacidad = unparsedTravel.get("privacidad");
                Usuario user = getUser(uid);
                if (user != null && inicioLat != null && inicioLng != null && finLat != null && finLng != null && tipo != null) {
                    Marcador inicio = new Marcador(new LatLng(Double.parseDouble(inicioLat), Double.parseDouble(inicioLng)), Marcador.Tipo.INICIO);
                    Marcador fin = new Marcador(new LatLng(Double.parseDouble(finLat), Double.parseDouble(finLng)), Marcador.Tipo.FIN);
                    Recorrido.Estado estadoEnum;
                    Recorrido.Tipo tipoEnum;
                    Recorrido.Privacidad privacidadEnum;
                    if (estado.equals("en_curso")) {
                        estadoEnum = Recorrido.Estado.EN_CURSO;
                    } else if (estado.equals("Publicado")) {
                        estadoEnum = Recorrido.Estado.PLANEADO;
                    } else {
                        estadoEnum = Recorrido.Estado.FINALIZADO;
                    }
                    if (tipo.equals("viaje")) {
                        tipoEnum = Recorrido.Tipo.VIAJE;
                    } else if (tipo.equals("frecuente")) {
                        tipoEnum = Recorrido.Tipo.FRECUENTE;
                    } else {
                        tipoEnum = Recorrido.Tipo.INSTANTANEO;
                    }
                    if (privacidad.equals("publico")) {
                        privacidadEnum = Recorrido.Privacidad.PUBLICO;
                    } else {
                        privacidadEnum = Recorrido.Privacidad.PRIVADO;
                    }
                    Recorrido recorrido = new Recorrido(inicio, fin, fecha, hora, estadoEnum, privacidadEnum, tipoEnum);
                    //TODO List of participants
                } else {
                    throw new IllegalArgumentException("Unable to create Message");
                }
            } catch (Exception e) {
                Log.e("DATABASE EXCEPTION", "Exception parsing Message. " + e.getMessage());
            }
        }
        return recorridos;
    }
}
