package co.edu.javeriana.tandemsquad.tandem.firebase;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.twitter.sdk.android.core.TwitterSession;

import co.edu.javeriana.tandemsquad.tandem.utilities.ActivityResult;

public class FireBaseAuthentication {

    private Activity activity;
    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener authenticationListener;
    private FirebaseUser user;

    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;

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

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("761540755261-8l6eg1m6p278s3lktso5t6pplffuqhmg.apps.googleusercontent.com").requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage((FragmentActivity) activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Snackbar.make(FireBaseAuthentication.this.activity.getCurrentFocus(), connectionResult.getErrorMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
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
    * SIGN IN WITH GOOGLE
    * **********************************/

    public final void signInWithGoogle() {
        ActivityResult.startGoogleLogin(activity, googleApiClient);
    }

    public void onGoogleSignInSucess(Intent data){
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            authentication.signInWithCredential(credential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = task.getResult().getUser();
                        onSignUpSuccess();
                    }
                    else onSignUpFailed(task);
                }
            });
        } else {
            Snackbar.make(activity.getCurrentFocus(), result.getStatus().getStatusMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    /***********************************
    * SIGN IN WITH FACEBOOK
    * **********************************/

    public final void setUpFacebookAuthentication(LoginButton loginButton, CallbackManager callbackManager){
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Snackbar.make(activity.getCurrentFocus(), error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        authentication.signInWithCredential(credential)
            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = authentication.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        onSignUpFailed(task);
                    }
                    // ...
                }
            });
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

    public void handleTwitterSession(TwitterSession session) {
        AuthCredential credential = TwitterAuthProvider.getCredential(
            session.getAuthToken().token,
            session.getAuthToken().secret);

        authentication.signInWithCredential(credential)
            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        user = authentication.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        onSignUpFailed(task);
                    }
                    // ...
                }
            });
    }

    public void signOut() {
        authentication.signOut();
    }
}
