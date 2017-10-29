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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.HomeActivity;
import co.edu.javeriana.tandemsquad.tandem.R;
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;

public class TravelAdapter extends ArrayAdapter<Recorrido>{

    public TravelAdapter(Context context, List<Recorrido> array) {
        super(context, 0, array);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final Recorrido travel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_travel, parent, false);
        }

        TextView origin = (TextView) convertView.findViewById(R.id.travel_adapter_origin);
        TextView originDate = (TextView) convertView.findViewById(R.id.travel_adapter_origin_date);
        TextView originTime = (TextView) convertView.findViewById(R.id.travel_adapter_origin_time);
        TextView destiny = (TextView) convertView.findViewById(R.id.travel_adapter_destiny);
        TextView destinyDate = (TextView) convertView.findViewById(R.id.travel_adapter_destiny_date);
        TextView destinyTime = (TextView) convertView.findViewById(R.id.travel_adapter_destiny_time);

        origin.setText(travel.getInicio().getDescripcion());
        originDate.setText("A");
        originTime.setText("A");
        destiny.setText(travel.getFin().getDescripcion());
        destinyDate.setText("A");
        destinyTime.setText("A");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putBoolean("draw", true);
                extras.putDouble("lat1", travel.getInicio().getPosicion().latitude);
                extras.putDouble("lon1", travel.getInicio().getPosicion().longitude);
                extras.putDouble("lat2", travel.getFin().getPosicion().latitude);
                extras.putDouble("lon2", travel.getFin().getPosicion().longitude);
                Intent intent = new Intent(parent.getContext(), HomeActivity.class);
                intent.putExtras(extras);
                parent.getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
