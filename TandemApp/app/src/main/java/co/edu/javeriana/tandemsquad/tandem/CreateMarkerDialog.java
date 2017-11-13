package co.edu.javeriana.tandemsquad.tandem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.R;
import co.edu.javeriana.tandemsquad.tandem.WeekDayPickerDialog;
import co.edu.javeriana.tandemsquad.tandem.negocio.Marcador;

public class CreateMarkerDialog extends AlertDialog implements DialogInterface.OnClickListener {

  LayoutInflater inflater;
  View view;

  Spinner eventTypeSpinner;
  EditText descriptionEditText;

  OnMarkerCreatedListener mMarkerCreatedListener;

  public CreateMarkerDialog(Context context, OnMarkerCreatedListener listener) {
    super(context);
    mMarkerCreatedListener = listener;

    setTitle(R.string.create_marker_dialog_title);
    setButton(Dialog.BUTTON_POSITIVE, context.getString(android.R.string.ok), this);
    setButton(Dialog.BUTTON_NEGATIVE, context.getString(android.R.string.cancel), this);

    inflater = LayoutInflater.from(context);
    view = inflater.inflate(R.layout.create_marker_dialog, null);
    setView(view);

    initComponents();
  }

  private void initComponents(){
    eventTypeSpinner = (Spinner) view.findViewById(R.id.event_type_spinner);
    descriptionEditText = (EditText) view.findViewById(R.id.marker_description_edit);
    //TODO Coregir valores que pueden estar en el spinner
    eventTypeSpinner.setAdapter(new ArrayAdapter<Marcador.Tipo>(view.getContext(), android.R.layout.simple_list_item_1, Marcador.Tipo.values()));
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    switch (which) {
      case BUTTON_POSITIVE:
        if (mMarkerCreatedListener!= null) {
          mMarkerCreatedListener.onMarkerCreated(eventTypeSpinner.getSelectedItem().toString(), descriptionEditText.getText().toString());
        }
        break;
      case BUTTON_NEGATIVE: //Cerrar el diálogo
        cancel();
        break;
    }
  }

  //Interfaz a implementar por la clase que lanza el diálogo
  public interface OnMarkerCreatedListener {
    void onMarkerCreated(String type, String description);
  }
}
