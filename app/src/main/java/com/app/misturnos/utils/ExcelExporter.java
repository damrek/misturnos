package com.app.misturnos.utils;

import android.content.Context;
import android.graphics.Color;

import com.app.misturnos.App;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelExporter {

    TreeMap<Date, String> cDlistFinal;
    Context context;
    String month;
    String year;
    private File fileGenerated;

    String[] monthName = {"Enero", "Febrero",
            "Marzo", "Abril", "Mayo", "Junio", "Julio",
            "Agosto", "Septiembre", "Octubre", "Noviembre",
            "Diciembre"};

    public ExcelExporter(TreeMap<Date, String> cDlistFinal, Context context, Integer month, Integer year) {
        this.cDlistFinal = cDlistFinal;
        this.context = context;
        this.month = monthName[month];
        this.year = year.toString();
    }

    public void export() {
        String path = context.getExternalFilesDir(null).getAbsolutePath() + "/MisTurnos/";

        String csvFile = "MisTurnos_" + month + "_" + year + ".xls";

        File directory = new File(path);

        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("es", "ES"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);

            //Excel sheetA first sheetA
            WritableSheet sheetA = workbook.createSheet(month + "_" + year, 0);

            Iterator it = cDlistFinal.entrySet().iterator();
            int conta = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                WritableCellFormat newFormat = new WritableCellFormat(new jxl.write.DateFormat("dd/mm/yyyy"));

                WritableCell c = new jxl.write.DateTime(0, conta, (Date) pair.getKey(), newFormat);
                WritableCell cNameTurno = new jxl.write.Label(1, conta, (String) pair.getValue().toString());
                int color = 0;
                switch (pair.getValue().toString()) {
                    case "Libre":
                        color = App.sharedPreferences.getInt("libre_color", 0x4caf50);
                        workbook.setColourRGB(Colour.LIGHT_TURQUOISE, Color.red(color), Color.green(color), Color.blue(color));
                        newFormat.setBackground(Colour.LIGHT_TURQUOISE);
                        break;
                    case "Mañanas":
                        color = App.sharedPreferences.getInt("manana_color", 0x03a9f4);
                        workbook.setColourRGB(Colour.LIGHT_TURQUOISE2, Color.red(color), Color.green(color), Color.blue(color));
                        newFormat.setBackground(Colour.LIGHT_TURQUOISE2);
                        break;
                    case "Tardes":
                        color = App.sharedPreferences.getInt("tarde_color", 0xff9800);
                        workbook.setColourRGB(Colour.LIGHT_GREEN, Color.red(color), Color.green(color), Color.blue(color));
                        newFormat.setBackground(Colour.LIGHT_GREEN);
                        break;
                    case "Noches":
                        color = App.sharedPreferences.getInt("noche_color", 0xBD1EE9);
                        workbook.setColourRGB(Colour.LIGHT_BLUE, Color.red(color), Color.green(color), Color.blue(color));
                        newFormat.setBackground(Colour.LIGHT_BLUE);
                        break;
                    default:
//                        newFormat.setBackground(Colour.WHITE);
                        break;
                }

                c.setCellFormat(newFormat);
                sheetA.addCell(c);
                cNameTurno.setCellFormat(new WritableCellFormat(new jxl.write.WritableFont(new WritableFont(WritableFont.ARIAL))));
                sheetA.addCell(cNameTurno);
                conta++;
            }

            // close workbook
            workbook.write();
            workbook.close();
            fileGenerated = file;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFileGenerated() {
        return fileGenerated;
    }
}