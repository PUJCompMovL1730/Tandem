package co.edu.javeriana.tandemsquad.tandem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.twitter.sdk.android.core.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.UserAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class TravelMembersActivity extends NavigationActivity
{
    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;

    private ListView friends;
    private ListView members;
    private List<Usuario> listFriends;
    private List<Usuario> listMembers;
    private UserAdapter userAdapter;
    private UserAdapter memebersAdapter;

    private int invitedPosition;
    private int removedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_members);

        initComponents();
        setButtonActions();

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                setToolbarData(fireBaseAuthentication, fireBaseStorage);
            }
        };

        fireBaseStorage = new FireBaseStorage(this) {
            @Override
            protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {
                Uri uri = Uri.fromFile(file);
                Bitmap image = (Bitmap) Utils.getImageFormUri(TravelMembersActivity.this, uri);
                viewImage.setImageBitmap(image);
            }
        };
    }

    @Override
    protected void initComponents()
    {
        super.initComponents();

        friends = (ListView) findViewById(R.id.invites_list_view);
        //TODO: Obetener los amigos del usuario
        listFriends = new ArrayList<>();
        userAdapter = new UserAdapter(this, listFriends);
        members = (ListView) findViewById(R.id.members_list_view);
        listMembers = new ArrayList<>();
        memebersAdapter = new UserAdapter(this, listMembers );

        friends.setAdapter(userAdapter);
        members.setAdapter(memebersAdapter);
    }

    @Override
    protected void setButtonActions()
    {
        super.setButtonActions();

        members.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                removedPosition = position;
            }
        });

        friends.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                invitedPosition = position;
                showInviteDialog();
            }
        });
    }

    public void invitationManager()
    {
        Usuario user = (Usuario) friends.getAdapter().getItem(invitedPosition);

        // TODO: Invitarlo
    }

    public void removalManager()
    {
        Usuario user = (Usuario) members.getAdapter().getItem(invitedPosition);
        int userIndex = -1;

        for( int i = 0; i < listMembers.size(); i++ )
        {
            if( user.getCorreo().equals(user.getCorreo()) )
            {
                userIndex = i;
            }
        }

        listMembers.remove(userIndex);
        memebersAdapter = new UserAdapter(this, listMembers );
        members.setAdapter(memebersAdapter);
    }

    public void showInviteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Invitacion")
                .setMessage(R.string.invite_dialog_title)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        invitationManager();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showRemoveDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminacion")
                .setMessage(R.string.remove_dialog_title)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        removalManager();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        fireBaseAuthentication.start();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        fireBaseAuthentication.stop();
    }

    @Override
    protected void logout()
    {
        fireBaseAuthentication.signOut();
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
