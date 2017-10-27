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
import co.edu.javeriana.tandemsquad.tandem.negocio.Chat;
import co.edu.javeriana.tandemsquad.tandem.negocio.Travel;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends ArrayAdapter<Chat> {

    public ChatAdapter(Context context, List<Chat> array) {
        super(context, 0, array);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Chat chat = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_chat, parent, false);
        }

        CircleImageView image = (CircleImageView) convertView.findViewById(R.id.chat_adapter_image);
        TextView name = (TextView) convertView.findViewById(R.id.chat_adapter_name);
        TextView message = (TextView) convertView.findViewById(R.id.chat_adapter_msg);

        //image.setText(chat.getOriginName());
        name.setText(chat.getName());
        message.setText(chat.getLastMessage().getText());

        return convertView;
    }
}
