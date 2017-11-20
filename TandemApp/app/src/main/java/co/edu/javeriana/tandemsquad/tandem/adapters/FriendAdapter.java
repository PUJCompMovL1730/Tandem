package co.edu.javeriana.tandemsquad.tandem.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.ChatActivity;
import co.edu.javeriana.tandemsquad.tandem.R;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends ArrayAdapter<Usuario> {

    public FriendAdapter(Context context, List<Usuario> array) {
        super(context, 0, array);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Usuario user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_friend, parent, false);
        }

        CircleImageView image = (CircleImageView) convertView.findViewById(R.id.friend_adapter_image);
        TextView name = (TextView) convertView.findViewById(R.id.friend_adapter_name);
        TextView username = (TextView) convertView.findViewById(R.id.friend_adapter_username);
        ImageView startChat = (ImageView) convertView.findViewById(R.id.start_chat);


        image.setImageBitmap(user.getImagen());
        name.setText(user.getNombre());
        username.setText(user.getCorreo());

        View.OnClickListener profileListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open profile?
            }
        };
        image.setOnClickListener(profileListener);
        name.setOnClickListener(profileListener);
        username.setOnClickListener(profileListener);
        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                chatIntent.putExtra("receiver", user.getId());
                getContext().startActivity(chatIntent);
            }
        });

        return convertView;
    }
}
