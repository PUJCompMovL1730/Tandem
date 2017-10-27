package co.edu.javeriana.tandemsquad.tandem.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.R;
import co.edu.javeriana.tandemsquad.tandem.negocio.Travel;
import co.edu.javeriana.tandemsquad.tandem.negocio.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter(Context context, List<User> array) {
        super(context, 0, array);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_user, parent, false);
        }

        CircleImageView image = (CircleImageView) convertView.findViewById(R.id.user_adapter_image);
        TextView name = (TextView) convertView.findViewById(R.id.user_adapter_name);
        TextView username = (TextView) convertView.findViewById(R.id.user_adapter_username);

        //image.setText(user.getOriginName());
        name.setText(user.getName());
        username.setText(user.getUsername());

        return convertView;
    }
}
