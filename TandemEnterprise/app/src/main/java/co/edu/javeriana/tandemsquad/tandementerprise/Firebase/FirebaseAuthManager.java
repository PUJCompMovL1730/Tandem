package co.edu.javeriana.tandemsquad.tandementerprise.Firebase;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthManager
{
    private Activity activity;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user;

    public FirebaseAuthManager( Activity activity )
    {
        this.activity = activity;

        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if( user != null )
                {
                    onSignInSuccess();
                }
            }
        };
    }

    public void start()
    {
        auth.addAuthStateListener( authListener );
        if( user != null )
        {
            onSignInSuccess();
        }
    }

    public void stop()
    {
        if( authListener != null )
        {
            auth.removeAuthStateListener( authListener );
        }
    }

    public final void signInWithEmailAndPassword( String email, String password )
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if( task.isSuccessful() )
                {
                    user = task.getResult().getUser();
                    onSignInSuccess();
                }
                else
                {
                    onSignInFailed( task );
                }
            }
        });
    }

    protected void onSignInSuccess()
    {

    }

    protected void onSignInFailed( Task<AuthResult> task )
    {
        //Snackbar.make(activity.getCurrentFocus(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
    }

    public FirebaseUser getUser()
    {
        return user;
    }

    public void signOut()
    {
        auth.signOut();
    }
}
