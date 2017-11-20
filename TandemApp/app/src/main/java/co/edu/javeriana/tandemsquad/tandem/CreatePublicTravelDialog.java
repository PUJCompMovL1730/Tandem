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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import co.edu.javeriana.tandemsquad.tandem.negocio.Marcador;
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;

public class CreatePublicTravelDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, WeekDayPickerDialog.OnDaysSetListener {

    TextInputEditText placeInput;
    TextView dateText, timeText;
    RadioGroup radioGroupType;
    RadioGroup radioGroupPrivacy;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    WeekDayPickerDialog weekDayPickerDialog;
    Calendar currentDate;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;

    View rootView;
    Bundle bundle;
    String placeName;

    Marcador mInicio;
    Marcador mFin;

    Recorrido travel;
    OnTravelCreatedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        rootView = inflater.inflate(R.layout.travel_create_dialog, container, false);

        //Argumentos enviados por la actividad
        bundle = getArguments();
        placeName = bundle.getString("placeName");
        LatLng inicioLatLng = new LatLng(bundle.getDouble("mInicioLat"), bundle.getDouble("mInicioLng"));
        LatLng finLatLng = new LatLng(bundle.getDouble("mFinalLat"), bundle.getDouble("mFinalLng"));

        mInicio = new Marcador(inicioLatLng, Marcador.Tipo.INICIO, "");
        mFin = new Marcador(finLatLng, Marcador.Tipo.FIN, "");

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.dialog_toolbar);
        toolbar.setTitle(R.string.create_travel_title);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            //actionBar.setHomeButtonEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.icon_close);
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
        radioGroupType = (RadioGroup) rootView.findViewById(R.id.radio_group_type);
        radioGroupPrivacy = (RadioGroup) rootView.findViewById(R.id.radio_group_privacy);

        dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
        timeFormat = new SimpleDateFormat("hh:mm aaa", Locale.US);
        currentDate = Calendar.getInstance(TimeZone.getDefault());

        timePickerDialog = new TimePickerDialog(getContext(), CreatePublicTravelDialog.this,
                currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
        datePickerDialog = new DatePickerDialog(getContext(), CreatePublicTravelDialog.this,
                currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        weekDayPickerDialog = new WeekDayPickerDialog(getContext(), CreatePublicTravelDialog.this);

        dateText.setText(dateFormat.format(currentDate.getTime()));
        timeText.setText(timeFormat.format(currentDate.getTime()));

        placeInput.setText(placeName);

        travel = new Recorrido(mInicio, mFin);
    }

    private void setUpActions() {
        //Actualizar el campo de fecha según el Radio Button seleccionado
        radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_trip:
                        dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
                        break;
                    case R.id.radio_button_frequent:
                        dateFormat = new SimpleDateFormat("EEEE", Locale.US);
                        break;
                }
                dateText.setText(dateFormat.format(currentDate.getTime()));
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroupType.getCheckedRadioButtonId() == R.id.radio_button_trip) { //Viaje
                    //Seleccionar fecha
                    datePickerDialog.show();
                } else { //Viaje frecuente
                    //Seleccionar días de la semana
                    weekDayPickerDialog.show();
                }
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Seleccionar la hora
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

    //Listener de las opciones del diálogo
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_travel:
                travel.setEstadoVal(Recorrido.Estado.PLANEADO);
                if (radioGroupPrivacy.getCheckedRadioButtonId() == R.id.radio_button_private) {
                    travel.setPrivacidadVal(Recorrido.Privacidad.PRIVADO);
                }
                else {
                    travel.setPrivacidadVal(Recorrido.Privacidad.PUBLICO);
                }
                if (radioGroupType.getCheckedRadioButtonId() == R.id.radio_button_trip) {
                    travel.setTipoVal(Recorrido.Tipo.VIAJE);
                }
                else {
                    travel.setTipoVal(Recorrido.Tipo.FRECUENTE);
                }
                travel.setFecha(dateText.getText().toString());
                travel.setHora(timeText.getText().toString());
                listener.onTravelCreated(travel);
                this.dismiss();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setOnTravelCreatedListener(OnTravelCreatedListener listener){
        this.listener = listener;
    }

    //La fecha del recorrido se seleccionó
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        currentDate.set(year, month, dayOfMonth);
        dateText.setText(dateFormat.format(currentDate.getTime()));
    }

    //La hora del recorrido se seleccionó
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        timeText.setText(timeFormat.format(calendar.getTime()));
    }

    //Los días del recorrido han sido seleccionados
    @Override
    public void onDaysSet(String selectedDaysString) {
        dateText.setText(selectedDaysString);
    }

    public interface OnTravelCreatedListener {
        void onTravelCreated(Recorrido newTravel);
    }
}
