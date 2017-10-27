package co.edu.javeriana.tandemsquad.tandem.firebase;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FireBaseAuthentication {

    private Activity activity;
    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener authenticationListener;
    private FirebaseUser user;

    public FireBaseAuthentication(Activity activity) {
        this.activity = activity;
        authentication = FirebaseAuth.getInstance();

        authenticationListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(isAnUserSignedIn()) {
                    onSignInSuccess();
                }
            }
        };
    }

    public void start() {
        authentication.addAuthStateListener(authenticationListener);
        if(isAnUserSignedIn()) {
            onSignInSuccess();
        }
    }

    public void stop() {
        if (authenticationListener != null) {
            authentication.removeAuthStateListener(authenticationListener);
        }
    }

    /***********************************
    * SIGN IN USER IN FIREBASE
    * **********************************/

    public final void signInWithEmailAndPassword(String email, String password) {
        authentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user = task.getResult().getUser();
                    onSignInSuccess();
                }
                else onSignInFailed(task);
            }
        });
    }

    protected void onSignInSuccess(){}

    protected void onSignInFailed(Task<AuthResult> task) {
        Snackbar.make(activity.getCurrentFocus(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
    }

    /***********************************
    * SIGN UP USER IN FIREBASE
    * **********************************/

    public final void createUserWithEmailAndPassword(final String email, String password) {
        authentication.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                user = task.getResult().getUser();
                onSignUpSuccess();
            }
            else onSignUpFailed(task);
            }
        });
    }

    protected void onSignUpSuccess() {}

    protected void onSignUpFailed(Task<AuthResult> task) {
        Snackbar.make(activity.getCurrentFocus(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
    }

    /***********************************
    * UPDATE USER PROFILE IN FIREBASE
    * **********************************/

    public final void updateUserProfile(String displayName, Uri imageUri) {
        UserProfileChangeRequest.Builder userBuilder = new UserProfileChangeRequest.Builder();
        userBuilder.setDisplayName(displayName);
        userBuilder.setPhotoUri(imageUri);
        UserProfileChangeRequest request = userBuilder.build();
        this.user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) onUserProfileUpdateSuccess();
                else onUserProfileUpdateFailed(task);
            }
        });
    }

    public final void updateUserProfile(String displayName) {
        UserProfileChangeRequest.Builder userBuilder = new UserProfileChangeRequest.Builder();
        userBuilder.setDisplayName(displayName);
        UserProfileChangeRequest request = userBuilder.build();
        this.user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) onUserProfileUpdateSuccess();
                else onUserProfileUpdateFailed(task);
            }
        });
    }

    protected void onUserProfileUpdateSuccess() {}

    protected void onUserProfileUpdateFailed(Task<Void> task) {
        Snackbar.make(activity.getCurrentFocus(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
    }

    public FirebaseUser getUser() {
        return user;
    }

    public boolean isAnUserSignedIn() {
        return user != null;
    }

    public void signOut() {
        authentication.signOut();
    }
}
