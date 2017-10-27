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
import co.edu.javeriana.tandemsquad.tandem.negocio.Travel;

public class TravelAdapter extends ArrayAdapter<Travel> {

    public TravelAdapter(Context context, List<Travel> array) {
        super(context, 0, array);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Travel travel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_travel, parent, false);
        }

        TextView origin = (TextView) convertView.findViewById(R.id.travel_adapter_origin);
        TextView destiny = (TextView) convertView.findViewById(R.id.travel_adapter_destiny);
        TextView distance = (TextView) convertView.findViewById(R.id.travel_adapter_distance);

        origin.setText(travel.getOriginName());
        destiny.setText(travel.getDestinyName());
        distance.setText(travel.getDistance() + " km");

        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(finalConvertView.getContext(), travel.getDestinyName(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
