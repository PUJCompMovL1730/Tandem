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
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;

public class RecorridoAdapter extends ArrayAdapter<Recorrido> {

    public RecorridoAdapter(Context context, List<Recorrido> array) {
        super(context, 0, array);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Recorrido recorrido = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_group, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.group_adapter_name);
        TextView members = (TextView) convertView.findViewById(R.id.group_adapter_members);

        //name.setText(recorrido.getName());
        //members.setText(recorrido.getMembers().size() + "");

        return convertView;
    }
}
