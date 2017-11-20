package co.edu.javeriana.tandemsquad.tandem.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.R;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserNotFriendAdapter extends ArrayAdapter<Usuario> {

    private OnAddFriendButtonPressedListener listener;

    public UserNotFriendAdapter(Context context, List<Usuario> array, OnAddFriendButtonPressedListener listener) {
        super(context, 0, array);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Usuario user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_user_not_friend, parent, false);
        }

        CircleImageView image = (CircleImageView) convertView.findViewById(R.id.user_not_friend_adapter_image);
        TextView name = (TextView) convertView.findViewById(R.id.user_not_friend_adapter_name);
        ImageView addButton = (ImageView) convertView.findViewById(R.id.add_friend);

        //image.setImageBitmap(user.getImagen());
        user.addAsyncImageListener(image);
        name.setText(user.getNombre());
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(user);
                notifyDataSetChanged();
                listener.onAddFriendButtonPressed(user);
            }
        });

        return convertView;
    }

    public interface OnAddFriendButtonPressedListener{
        void onAddFriendButtonPressed(Usuario selectedUser);
    }
}
