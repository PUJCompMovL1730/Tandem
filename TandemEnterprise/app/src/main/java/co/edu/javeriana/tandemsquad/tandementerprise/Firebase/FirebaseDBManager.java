package co.edu.javeriana.tandemsquad.tandementerprise.Firebase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import co.edu.javeriana.tandemsquad.tandementerprise.Mundo.Enterprise;

public class FirebaseDBManager
{
    private Activity activity;
    private FirebaseDatabase db;

    private static Map<String, Enterprise> userPool = new HashMap<>();
    private static int DATABASE_TIMEOUT_SECONDS = 15;

    public FirebaseDBManager( Activity activity )
    {
        this.activity = activity;

        db = FirebaseDatabase.getInstance();
    }

    public void writeEnterprise( final Enterprise enterprise )
    {
        final DatabaseReference enterpriseRef = db.getReference( "enterprises/" + enterprise.getId() );
        enterpriseRef.setValue( enterprise ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if( task.isSuccessful() )
                {
                    onSuccessWriteEnterprise( task );
                }
                else
                {
                    onFailureWriteEnterprise( task );
                }
            }
        });
    }

    protected void onSuccessWriteEnterprise( Task<Void> task )
    {

    }

    protected void onFailureWriteEnterprise( Task<Void> task )
    {

    }

    public Enterprise getEnterprise( String uid )
    {
        if( userPool.containsKey( uid ) )
        {
            if( userPool.get( uid ) != null )
            {
                Log.i("hay user", "USEEEEEEEEEEEEER EN POOL");
                return userPool.get( uid );
            }
        }
        else
        {
            Log.i("NO hay user", "NO HAAAAAAAAAY USEEEEEEEEEEEEER");
        }

        Map< String, String > unparsedEnterprise = getGenericClassFromDatabase( "enterprises/" + uid );

        if( unparsedEnterprise != null )
        {
            try
            {
                String id = unparsedEnterprise.get( "id" );
                String email = unparsedEnterprise.get( "email" );
                String name = unparsedEnterprise.get( "name" );
                Log.i("hay user", id);
                Log.i("hay user", email);
                Log.i("hay user", name);

                if( id != null && email != null && name != null )
                {
                    final Enterprise allegedEnterprise = new Enterprise(id, name, email );
                    userPool.put( id, allegedEnterprise );
                    Log.i("hay user", "USEEEEEEEEEEEEER");
                    return allegedEnterprise;
                }
                else
                {
                    throw new IllegalArgumentException("Unable to create User (Invalid data)");
                }
            }
            catch ( Exception e )
            {
                Log.e("DATABASE EXCEPTION", "Invalid user data: " + e.getMessage());
                return null;
            }
        }
        else
        {
            Log.e("DATABASE INFO", "TIMEOUT getting user: " + uid);
        }

        return null;
    }

    public Map< String, String > getGenericClassFromDatabase( String ref )
    {
        final LinkedBlockingQueue< Map< String, String > > asyncQueue = new LinkedBlockingQueue<>();

        DatabaseReference messageReference = db.getReference( ref );
        messageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Map< String, String > unparsedData = (Map<String, String>) dataSnapshot.getValue();

                try
                {
                    if( unparsedData != null )
                    {
                        asyncQueue.put( unparsedData );
                    }
                }
                catch ( InterruptedException e )
                {
                    Log.e("DATABASE EXCEPTION", e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e("DATABASE EXCEPTION", "Error en la consulta");
            }
        });

        try
        {
            return asyncQueue.poll( DATABASE_TIMEOUT_SECONDS, TimeUnit.SECONDS );
        }
        catch (InterruptedException e )
        {
            return null;
        }
    }
}
