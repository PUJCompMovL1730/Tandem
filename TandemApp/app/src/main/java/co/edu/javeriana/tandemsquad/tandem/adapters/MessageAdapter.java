package co.edu.javeriana.tandemsquad.tandem.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.ChatActivity;
import co.edu.javeriana.tandemsquad.tandem.R;
import co.edu.javeriana.tandemsquad.tandem.negocio.Mensaje;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends ArrayAdapter<Mensaje>{
    public MessageAdapter(Context context, List<Mensaje> array) {
        super(context, 0, array);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Mensaje mensaje = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_message, parent, false);
        }

        CircleImageView image = (CircleImageView) convertView.findViewById(R.id.message_adapter_image);
        TextView name = (TextView) convertView.findViewById(R.id.message_adapter_name);
        TextView message = (TextView) convertView.findViewById(R.id.message_adapter_msg);

        //image.setText(chat.getOriginName());
        if (mensaje.isMe()) {
            name.setText(mensaje.getReceptor().getNombre());
        } else {
            name.setText(mensaje.getEmisor().getNombre());
        }
        message.setText(mensaje.getTexto());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                if (mensaje.isMe()) {
                    chatIntent.putExtra("receiver", mensaje.getReceptor().getId());
                } else {
                    chatIntent.putExtra("receiver", mensaje.getEmisor().getId());
                }
                getContext().startActivity(chatIntent);
            }
        });

        return convertView;
    }
}
