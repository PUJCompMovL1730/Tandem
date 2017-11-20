package co.edu.javeriana.tandemsquad.tandementerprise.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Circle;

import java.util.List;

import co.edu.javeriana.tandemsquad.tandementerprise.Mundo.Event;
import co.edu.javeriana.tandemsquad.tandementerprise.R;

public class EventsAdapter extends ArrayAdapter< Event >
{

    public EventsAdapter(@NonNull Context context, List< Event > array) {
        super(context, 0, array);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent )
    {
        final Event evt = getItem( position );

        if( convertView == null )
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_event, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.event_adapter_name);
        TextView lon = (TextView) convertView.findViewById(R.id.event_adapter_lon);
        TextView lat = (TextView) convertView.findViewById(R.id.event_adapter_lat);
        TextView hStart = (TextView) convertView.findViewById(R.id.event_adapter_hStart);
        TextView hEnd = (TextView) convertView.findViewById(R.id.event_adapter_hEnd);

        name.setText(evt.getDescription());
        lon.setText(String.valueOf(evt.getInitialPosition().longitude));
        lat.setText(String.valueOf(evt.getInitialPosition().latitude));
        hStart.setText(evt.getFinalDate());
        hEnd.setText(evt.getFinalDate());

        View.OnClickListener eventListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        name.setOnClickListener(eventListener);

        return convertView;
    }
}
