package co.edu.javeriana.tandemsquad.tandem.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.R;
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;

public class TravelAdapter extends ArrayAdapter<Recorrido> {

    public TravelAdapter(Context context, List<Recorrido> array) {
        super(context, 0, array);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Recorrido travel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_travel, parent, false);
        }

        TextView origin = (TextView) convertView.findViewById(R.id.travel_adapter_origin);
        TextView destiny = (TextView) convertView.findViewById(R.id.travel_adapter_destiny);
        TextView distance = (TextView) convertView.findViewById(R.id.travel_adapter_distance);

        //origin.setText(travel.getOriginName());
        //destiny.setText(travel.getDestinyName());
        //distance.setText(travel.getDistance() + " km");

        return convertView;
    }
}
