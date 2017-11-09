package co.edu.javeriana.tandemsquad.tandem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WeekDayPickerDialog extends AlertDialog implements DialogInterface.OnClickListener {

  LayoutInflater inflater;
  View view;

  ToggleButton bMonday, bTuesday, bWednesday, bThursday, bFriday, bSaturday, bSunday;
  boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday = false;

  HashMap<String, Boolean> days;

  OnDaysSetListener mDaysSetListener;

  public WeekDayPickerDialog(Context context, OnDaysSetListener listener) {
    super(context);
    mDaysSetListener = listener;

    days = new HashMap<String, Boolean>();

    setTitle("Selecciona los días del recorrido");
    setButton(Dialog.BUTTON_POSITIVE, context.getString(android.R.string.ok), this);
    setButton(Dialog.BUTTON_NEGATIVE, context.getString(android.R.string.cancel), this);

    inflater = LayoutInflater.from(context);
    view = inflater.inflate(R.layout.weekday_picker_dialog, null);
    setView(view);

    initComponents();
    setUpActions();
  }

  private void initComponents() {
    bMonday = (ToggleButton) view.findViewById(R.id.monday_button);
    bTuesday = (ToggleButton) view.findViewById(R.id.tuesday_button);
    bWednesday = (ToggleButton) view.findViewById(R.id.wednesday_button);
    bThursday = (ToggleButton) view.findViewById(R.id.thursday_button);
    bFriday = (ToggleButton) view.findViewById(R.id.friday_button);
    bSaturday = (ToggleButton) view.findViewById(R.id.saturday_button);
    bSunday = (ToggleButton) view.findViewById(R.id.sunday_button);

    //TODO ¿Mejorar?
    days.put(bMonday.getTextOff().toString(), false);
    days.put(bTuesday.getTextOff().toString(), false);
    days.put(bWednesday.getTextOff().toString(), false);
    days.put(bThursday.getTextOff().toString(), false);
    days.put(bFriday.getTextOff().toString(), false);
    days.put(bSaturday.getTextOff().toString(), false);
    days.put(bSunday.getTextOff().toString(), false);
  }

  private void setUpActions(){
    //TODO ¿Mejorar?
    addOnClickListener(bMonday);
    addOnClickListener(bTuesday);
    addOnClickListener(bWednesday);
    addOnClickListener(bThursday);
    addOnClickListener(bFriday);
    addOnClickListener(bSaturday);
    addOnClickListener(bSunday);
  }

  //Añadir el listener a un ToggleButton
  private void addOnClickListener(final ToggleButton toggleButton){
    toggleButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //Alternar (true|false) el valor del mapa para cada día
        days.put(toggleButton.getTextOff().toString(), !days.get(toggleButton.getTextOff().toString()));
      }
    });
  }

  //Obtener un String con los días selecionados
  private String getSelectedDays(){
    StringBuilder daysString = new StringBuilder("");
    Iterator iterator = days.entrySet().iterator();
    while(iterator.hasNext()){
      Map.Entry pair = (Map.Entry) iterator.next();
      if(pair.getValue().equals(true)){ //El dia se seleccionó
        daysString.append(pair.getKey().toString());
        daysString.append(",");
      }
    }
    daysString.setLength(daysString.length() - 1);
    return daysString.toString();
  }

  @Override
  public void onClick(@NonNull DialogInterface dialog, int which) {
    switch (which) {
      case BUTTON_POSITIVE:
        if (mDaysSetListener!= null) {
          mDaysSetListener.onDaysSet(getSelectedDays()); //Llamar el callback en la clase que lanzó el diálogo
        }
        break;
      case BUTTON_NEGATIVE: //Cerrar el diálogo
        cancel();
        break;
    }
  }

  //Interfaz a implementar por la clase que lanza el diálogo
  public interface OnDaysSetListener {
    void onDaysSet(String selectedDaysString);
  }
}
