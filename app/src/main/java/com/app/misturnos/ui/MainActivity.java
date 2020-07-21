package com.app.misturnos.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.app.misturnos.App;
import com.app.misturnos.EventDecorator;
import com.app.misturnos.R;
import com.app.misturnos.utils.ExcelExporter;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.File;
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
    @BindView(R.id.selectedColor)
    ImageView selectedColorBox;

    ArrayList<EventDecorator> calendarios = new ArrayList();

    @Inject
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ((App) getApplication()).getAppComponent().inject(this);

        setupObservers();
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST);
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXST);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mcv.removeDecorators();
        loadDates();
        refreshSpinner();
    }

    private void refreshSpinner() {
        textSpinner = spinner.getSelectedItem().toString();
        String strColor = "none";
        switch (textSpinner) {
            case "Libre":
                int libreColor = App.sharedPreferences.getInt("libre_color", Color.parseColor("#4caf50"));
                strColor = String.format("#%06X", 0xFFFFFF & libreColor);
                mcv.setSelectionColor(Color.parseColor(strColor));

                selectedColorBox.setBackgroundColor(Color.rgb(Color.red(App.sharedPreferences.getInt("libre_color", 0x4caf50)),
                        Color.green(App.sharedPreferences.getInt("libre_color", 0x4caf50)),
                        Color.blue(App.sharedPreferences.getInt("libre_color", 0x4caf50))));
                break;
            case "Mañanas":
                int mananaColor = App.sharedPreferences.getInt("manana_color", Color.parseColor("#03a9f4"));
                strColor = String.format("#%06X", 0xFFFFFF & mananaColor);
                mcv.setSelectionColor(Color.parseColor(strColor));
                selectedColorBox.setBackgroundColor(Color.rgb(Color.red(App.sharedPreferences.getInt("manana_color", 0x03a9f4)),
                        Color.green(App.sharedPreferences.getInt("manana_color", 0x03a9f4)),
                        Color.blue(App.sharedPreferences.getInt("manana_color", 0x03a9f4))));
                break;
            case "Tardes":
                int tardeColor = App.sharedPreferences.getInt("tarde_color", Color.parseColor("#ff9800"));
                strColor = String.format("#%06X", 0xFFFFFF & tardeColor);
                mcv.setSelectionColor(Color.parseColor(strColor));
                selectedColorBox.setBackgroundColor(Color.rgb(Color.red(App.sharedPreferences.getInt("tarde_color", 0xff9800)),
                        Color.green(App.sharedPreferences.getInt("tarde_color", 0xff9800)),
                        Color.blue(App.sharedPreferences.getInt("tarde_color", 0xff9800))));
                break;
            case "Noches":
                int nocheColor = App.sharedPreferences.getInt("noche_color", Color.parseColor("#BD1EE9"));
                strColor = String.format("#%06X", 0xFFFFFF & nocheColor);
                mcv.setSelectionColor(Color.parseColor(strColor));
                selectedColorBox.setBackgroundColor(Color.rgb(Color.red(App.sharedPreferences.getInt("noche_color", 0xBD1EE9)),
                        Color.green(App.sharedPreferences.getInt("noche_color", 0xBD1EE9)),
                        Color.blue(App.sharedPreferences.getInt("noche_color", 0xBD1EE9))));
                break;
        }
    }

    private void setupObservers() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcv.getSelectedDate() != null) {
                    saveDatesSelected();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcv.getSelectedDate() != null) {
                    clearDatesSelected();
                }
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportCalendar(v);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textSpinner = spinner.getSelectedItem().toString();
                String strColor = "none";
                switch (textSpinner) {
                    case "Libre":
                        int libreColor = App.sharedPreferences.getInt("libre_color", 0x4caf50);
                        strColor = String.format("#%06X", 0xFFFFFF & libreColor);
                        mcv.setSelectionColor(Color.parseColor(strColor));

                        selectedColorBox.setBackgroundColor(Color.rgb(Color.red(App.sharedPreferences.getInt("libre_color", 0x4caf50)),
                                Color.green(App.sharedPreferences.getInt("libre_color", 0x4caf50)),
                                Color.blue(App.sharedPreferences.getInt("libre_color", 0x4caf50))));
                        break;
                    case "Mañanas":
                        int mananaColor = App.sharedPreferences.getInt("manana_color", 0x03a9f4);
                        strColor = String.format("#%06X", 0xFFFFFF & mananaColor);
                        mcv.setSelectionColor(Color.parseColor(strColor));
                        selectedColorBox.setBackgroundColor(Color.rgb(Color.red(App.sharedPreferences.getInt("manana_color", 0x03a9f4)),
                                Color.green(App.sharedPreferences.getInt("manana_color", 0x03a9f4)),
                                Color.blue(App.sharedPreferences.getInt("manana_color", 0x03a9f4))));
                        break;
                    case "Tardes":
                        int tardeColor = App.sharedPreferences.getInt("tarde_color", 0xff9800);
                        strColor = String.format("#%06X", 0xFFFFFF & tardeColor);
                        mcv.setSelectionColor(Color.parseColor(strColor));
                        selectedColorBox.setBackgroundColor(Color.rgb(Color.red(App.sharedPreferences.getInt("tarde_color", 0xff9800)),
                                Color.green(App.sharedPreferences.getInt("tarde_color", 0xff9800)),
                                Color.blue(App.sharedPreferences.getInt("tarde_color", 0xff9800))));
                        break;
                    case "Noches":
                        int nocheColor = App.sharedPreferences.getInt("noche_color", 0xBD1EE9);
                        strColor = String.format("#%06X", 0xFFFFFF & nocheColor);
                        mcv.setSelectionColor(Color.parseColor(strColor));
                        selectedColorBox.setBackgroundColor(Color.rgb(Color.red(App.sharedPreferences.getInt("noche_color", 0xBD1EE9)),
                                Color.green(App.sharedPreferences.getInt("noche_color", 0xBD1EE9)),
                                Color.blue(App.sharedPreferences.getInt("noche_color", 0xBD1EE9))));
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

            for (int i = 0; i < calendarios.size(); i++) {
                EventDecorator eventDecorator = calendarios.get(i);
                if (eventDecorator.getTipo().equals("Libre")) {
                    eventDecorator.setColor(App.sharedPreferences.getInt("libre_color", Color.parseColor("#4caf50")));
                    calendarios.get(i).setColor(App.sharedPreferences.getInt("libre_color", Color.parseColor("#4caf50")));
                } else if (eventDecorator.getTipo().equals("Mañanas")) {
                    eventDecorator.setColor(App.sharedPreferences.getInt("manana_color", Color.parseColor("#03a9f4")));
                    calendarios.get(i).setColor(App.sharedPreferences.getInt("manana_color", Color.parseColor("#03a9f4")));
                } else if (eventDecorator.getTipo().equals("Tardes")) {
                    eventDecorator.setColor(App.sharedPreferences.getInt("tarde_color", Color.parseColor("#ff9800")));
                    calendarios.get(i).setColor(App.sharedPreferences.getInt("tarde_color", Color.parseColor("#ff9800")));
                } else if (eventDecorator.getTipo().equals("Noches")) {
                    eventDecorator.setColor(App.sharedPreferences.getInt("noche_color", Color.parseColor("#BD1EE9")));
                    calendarios.get(i).setColor(App.sharedPreferences.getInt("noche_color", Color.parseColor("#BD1EE9")));
                }
                mcv.addDecorator(eventDecorator);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendCalendarByEmail(String to, String sub, File fileAttachment) {
        try {
            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", fileAttachment);
            Intent mail = new Intent(Intent.ACTION_SEND);
            mail.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
            mail.putExtra(Intent.EXTRA_SUBJECT, sub);
            mail.putExtra(Intent.EXTRA_STREAM, uri);
            mail.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mail.setType("text/plain");
            startActivity(Intent.createChooser(mail, "Send email via:"));
        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }
    }

    private void saveDatesSelected() {
        for (CalendarDay c : mcv.getSelectedDates()
        ) {
            List<CalendarDay> dateAdd = new ArrayList<>();
            dateAdd.add(c);
            EventDecorator eventDecorator = new EventDecorator(mcv.getSelectionColor(), dateAdd, textSpinner);
            mcv.addDecorator(eventDecorator);
            calendarios.add(eventDecorator);
        }

        savePreferences();
        mcv.clearSelection();
    }

    private void clearDatesSelected() {
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

    private void exportCalendar(View v) {
        Calendar cal = new GregorianCalendar();
        cal.set(mcv.getCurrentDate().getYear(), mcv.getCurrentDate().getMonth() - 1, 1, 0, 0, 0);

        TreeMap<Date, String> cDlist = new TreeMap<>();
        for (EventDecorator e : calendarios
        ) {
            Date date = new GregorianCalendar(e.getDates().iterator().next().getYear(), e.getDates().iterator().next().getMonth() - 1, e.getDates().iterator().next().getDay()).getTime();

            if (e.getDates().iterator().next().getMonth() - 1 == mcv.getCurrentDate().getMonth() - 1) {
                String turno = "";
                if (e.getColor() == App.sharedPreferences.getInt("libre_color", Color.parseColor("#4caf50"))) {
                    turno = "Libre";
                } else if (e.getColor() == App.sharedPreferences.getInt("manana_color", Color.parseColor("#03a9f4"))) {
                    turno = "Mañanas";
                } else if (e.getColor() == App.sharedPreferences.getInt("noche_color", Color.parseColor("#BD1EE9"))) {
                    turno = "Noches";
                } else {
                    turno = "Tardes";
                }

                cDlist.put(date, turno);
            }

        }

        int myMonth = cal.get(Calendar.MONTH);
        int myYear = cal.get(Calendar.YEAR);

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

        ExcelExporter xlsExporter = new ExcelExporter(cDlistFinal, MainActivity.this, myMonth, myYear);
        xlsExporter.export();
        Snackbar.make(v, "File: " + xlsExporter.getFileGenerated().getAbsolutePath(), Snackbar.LENGTH_LONG)
                .show();
        boolean sendEmail = App.sharedPreferences.getBoolean("sendMail", false);
        if (sendEmail) {
            String emailAddr = App.sharedPreferences.getString("mailAddr", "");
            if (TextUtils.isEmpty(emailAddr)) {
                Toast.makeText(MainActivity.this, "Introduce un email en opciones.", Toast.LENGTH_SHORT).show();
            } else {
                sendCalendarByEmail(emailAddr, "Calendario MisTurnos", xlsExporter.getFileGenerated());
            }
        }
    }


}
