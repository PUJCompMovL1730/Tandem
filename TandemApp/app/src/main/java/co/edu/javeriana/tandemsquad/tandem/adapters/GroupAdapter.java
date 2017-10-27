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
import co.edu.javeriana.tandemsquad.tandem.negocio.Group;
import co.edu.javeriana.tandemsquad.tandem.negocio.Travel;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends ArrayAdapter<Group> {

    public GroupAdapter(Context context, List<Group> array) {
        super(context, 0, array);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Group group = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_group, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.group_adapter_name);
        TextView members = (TextView) convertView.findViewById(R.id.group_adapter_members);

        name.setText(group.getName());
        members.setText(group.getMembers().size() + "");

        return convertView;
    }
}
