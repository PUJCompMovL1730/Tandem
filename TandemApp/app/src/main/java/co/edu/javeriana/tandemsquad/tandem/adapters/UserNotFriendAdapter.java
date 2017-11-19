package co.edu.javeriana.tandemsquad.tandem.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.R;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserNotFriendAdapter extends ArrayAdapter<Usuario> {

    public UserNotFriendAdapter(Context context, List<Usuario> array) {
        super(context, 0, array);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Usuario user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_user_not_friend, parent, false);
        }

        CircleImageView image = (CircleImageView) convertView.findViewById(R.id.user_not_friend_adapter_image);
        TextView name = (TextView) convertView.findViewById(R.id.user_not_friend_adapter_name);
        ImageButton addButton = (ImageButton) convertView.findViewById(R.id.add_friend);

        //image.setText(user.getOriginName());
        //name.setText(user.getName());
        //username.setText(user.getUsername());

        return convertView;
    }
}
