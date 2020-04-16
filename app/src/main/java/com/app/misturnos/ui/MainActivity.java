package com.app.misturnos.ui;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.misturnos.App;
import com.app.misturnos.EventDecorator;
import com.app.misturnos.R;
import com.app.misturnos.utils.ExcelExporter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private final String KEY_PREFS_TURNOS = "MisTurnosKey";
    private final Integer WRITE_EXST = 0x3;
    private final Integer READ_EXST = 0x4;

    @BindView(R.id.calendarView)
    MaterialCalendarView mcv;
    @BindView(R.id.spinner)
    Spinner spinner;
    String textSpinner = "";
    @BindView(R.id.saveBtn)
    Button saveButton;
    @BindView(R.id.clearBtn)
    Button clearButton;
    @BindView(R.id.exportBtn)
    Button exportButton;

    ArrayList<EventDecorator> calendarios = new ArrayList();

    @Inject
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((App) getApplication()).getAppComponent().inject(this);

        ButterKnife.bind(this);

        loadDates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CalendarDay> dates = mcv.getSelectedDates();

                for (CalendarDay c : dates
                ) {
                    List<CalendarDay> dateAdd = new ArrayList<>();
                    dateAdd.add(c);
                    EventDecorator eventDecorator = new EventDecorator(mcv.getSelectionColor(), dateAdd);
                    mcv.addDecorator(eventDecorator);
                    calendarios.add(eventDecorator);

                }

                savePreferences();
                mcv.clearSelection();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Iterator<EventDecorator> it = calendarios.iterator(); it.hasNext(); ) {
                    EventDecorator e = it.next();
                    boolean delete = checkIfDateExists(e.getDates().iterator().next().toString());
                    if (delete) {
                        mcv.removeDecorator(e);
                        it.remove();
                    }
                }

                savePreferences();
                mcv.invalidateDecorators();
                mcv.clearSelection();
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = new GregorianCalendar();
                cal.set(mcv.getCurrentDate().getYear(), mcv.getCurrentDate().getMonth() - 1, 1, 0, 0, 0);

                int myMonth = cal.get(Calendar.MONTH);

                TreeMap<Date, String> cDlist = new TreeMap<>();
                for (EventDecorator e : calendarios
                ) {
                    Date date = new GregorianCalendar(e.getDates().iterator().next().getYear(), e.getDates().iterator().next().getMonth() - 1, e.getDates().iterator().next().getDay()).getTime();

                    if (e.getDates().iterator().next().getMonth() - 1 == mcv.getCurrentDate().getMonth() - 1) {
                        String turno = "";
                        switch (e.getColor()) {
                            case -26317:
                                turno = "Tardes";
                                break;
                            case -65281:
                                turno = "Noches";
                                break;
                            default:
                                turno = "Mañanas";
                                break;
                        }
                        cDlist.put(date, turno);
                    }

                }


                while (myMonth == cal.get(Calendar.MONTH)) {
                    cDlist.put(cal.getTime(), "");
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }


                TreeMap<Date, String> cDlistFinal = new TreeMap<>();

                Iterator it = cDlist.entrySet().iterator();
                String oldValue = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    if (oldValue.equalsIgnoreCase(pair.getKey().toString())) {
                        continue;
                    } else {
                        cDlistFinal.put((Date) pair.getKey(), pair.getValue().toString());
                    }

                    oldValue = (String) pair.getKey().toString();
                }

                askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST);
                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXST);
                ExcelExporter xlsExporter = new ExcelExporter(cDlistFinal, MainActivity.this, myMonth);
                xlsExporter.export();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textSpinner = spinner.getSelectedItem().toString();
                switch (textSpinner) {
                    case "Mañanas":
                        mcv.setSelectionColor(Color.parseColor("#3399ff"));
                        break;
                    case "Tardes":
                        mcv.setSelectionColor(Color.parseColor("#ff9933"));
                        break;
                    case "Noches":
                        mcv.setSelectionColor(Color.parseColor("#ff00ff"));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    boolean checkIfDateExists(String d) {
        List<CalendarDay> dates = mcv.getSelectedDates();
        for (CalendarDay c : dates
        ) {
            if (c.toString().equals(d)) {
                return true;
            }
        }
        return false;
    }

    private void loadDates() {
        Gson gson = new Gson();
        String jsonPreferences = sharedPreferences.getString(KEY_PREFS_TURNOS, "");
        if (!jsonPreferences.equalsIgnoreCase("")) {
            Type type = new TypeToken<List<EventDecorator>>() {
            }.getType();
            calendarios = gson.fromJson(jsonPreferences, type);
            for (EventDecorator e : calendarios
            ) {
                mcv.addDecorator(e);
            }
            mcv.invalidateDecorators();
        }
    }

    private void savePreferences() {
        Gson gson = new Gson();
        String json = gson.toJson(calendarios);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(KEY_PREFS_TURNOS).apply();
        editor.putString(KEY_PREFS_TURNOS, json);
        editor.commit();

        Toast.makeText(MainActivity.this, "Valores guardados correctamente.", Toast.LENGTH_SHORT).show();
    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, permission + " is already granted.",
//                    Toast.LENGTH_SHORT).show();
        }
    }

}
