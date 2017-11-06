package co.edu.javeriana.tandemsquad.tandem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CreatePublicTravelDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

  TextInputEditText placeInput;
  TextView dateText, timeText;
  RadioGroup radioGroup;

  DatePickerDialog datePickerDialog;
  TimePickerDialog timePickerDialog;
  Calendar currentDate;
  SimpleDateFormat dateFormat;
  SimpleDateFormat timeFormat;

  View rootView;
  Bundle bundle;
  String placeName;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
    rootView = inflater.inflate(R.layout.travel_create_dialog, container, false);

    //Argumentos enviados por la actividad
    bundle = getArguments();
    placeName = bundle.getString("placeName");

    Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.dialog_toolbar);
    toolbar.setTitle(R.string.create_travel_title);

    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.icon_close);
    }
    setHasOptionsMenu(true);

    initComponents();
    setUpActions();

    return rootView;
  }

  private void initComponents() {
    placeInput = (TextInputEditText) rootView.findViewById(R.id.destination_input);
    dateText = (TextView) rootView.findViewById(R.id.date_text);
    timeText = (TextView) rootView.findViewById(R.id.time_text);
    radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);

    dateFormat = new SimpleDateFormat("EEE, MMM d, YYYY", Locale.US);
    timeFormat = new SimpleDateFormat("hh:mm aaa", Locale.US);
    currentDate = Calendar.getInstance(TimeZone.getDefault());
    timePickerDialog = new TimePickerDialog(getContext(), CreatePublicTravelDialog.this,
        currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
    datePickerDialog = new DatePickerDialog(getContext(), CreatePublicTravelDialog.this,
        currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

    dateText.setText(dateFormat.format(currentDate.getTime()));
    timeText.setText(timeFormat.format(currentDate.getTime()));

    placeInput.setText(placeName);

  }

  private void setUpActions() {

    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
          case R.id.radio_button_trip:
            dateText.setText(dateFormat.format(currentDate.getTime()));
            break;
          case R.id.radio_button_frequent:
            break;
        }
      }
    });

    dateText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(radioGroup.getCheckedRadioButtonId() == R.id.radio_button_trip){ //Viaje
          datePickerDialog.show();
        } else { //Viaje frecuente
          //TODO Desplegar diálogo para seleccionar los días de la semana
        }
      }
    });

    timeText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        timePickerDialog.show();
      }
    });
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.clear();
    getActivity().getMenuInflater().inflate(R.menu.travel_creation_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_create_travel:
        //TODO Create Travel
        return true;
      case android.R.id.home:
        dismiss();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  //Actualizar la fecha del recorrido
  @Override
  public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
    currentDate.set(year, month, dayOfMonth);
    dateText.setText(dateFormat.format(currentDate.getTime()));
  }

  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    //TODO "Settear" la hora seleccionada
  }
}
