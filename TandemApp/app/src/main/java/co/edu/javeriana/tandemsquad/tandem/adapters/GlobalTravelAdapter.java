package co.edu.javeriana.tandemsquad.tandem.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.HomeActivity;
import co.edu.javeriana.tandemsquad.tandem.R;
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;

public class GlobalTravelAdapter extends ArrayAdapter<Recorrido>{

    private OnJoinTravelButtonPressedListener listener;
    private String currentUserId;

    public GlobalTravelAdapter(Context context, List<Recorrido> array, OnJoinTravelButtonPressedListener listener, String id) {
        super(context, 0, array);
        this.listener = listener;
        this.currentUserId = id;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final Recorrido travel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_global_travel, parent, false);
        }

        TextView originDate = (TextView) convertView.findViewById(R.id.travel_adapter_origin_date);
        TextView originTime = (TextView) convertView.findViewById(R.id.travel_adapter_origin_time);
        TextView destiny = (TextView) convertView.findViewById(R.id.travel_adapter_destiny);
        final ImageView addTravel = (ImageView) convertView.findViewById(R.id.join_travel);
        for(Usuario usuario : travel.getParticipantes()) {
            if(usuario.getId().equals(currentUserId)) {
                addTravel.setImageResource(R.drawable.icon_done);
                addTravel.setEnabled(false);
            }
        }
        originDate.setText(travel.getFecha());
        originTime.setText(travel.getHora());
        destiny.setText(travel.getEndName());

        addTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTravel.setImageResource(R.drawable.icon_done);
                addTravel.setEnabled(false);;
                listener.onJoinTravelButtonPressed(travel);
            }
        });

        return convertView;
    }

    public interface OnJoinTravelButtonPressedListener{
        void onJoinTravelButtonPressed(Recorrido selectedTravel);
    }
}
