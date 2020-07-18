package com.app.misturnos.utils;

import android.content.Context;
import android.widget.Toast;

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
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelExporter {

    TreeMap<Date, String> cDlistFinal;
    Context context;
    String month;
    String year;

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
        String path = context.getExternalFilesDir(null).getAbsolutePath() +  "/MisTurnos/";

        String csvFile = "MisTurnos_"+month+"_"+year+".xls";

        File directory = new File(path);

        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale ( "es" , "ES" ));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);

            //Excel sheetA first sheetA
            WritableSheet sheetA = workbook.createSheet("MisTurnos", 0);

            Iterator it = cDlistFinal.entrySet().iterator();
            int conta = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                WritableCellFormat newFormat = new WritableCellFormat(new jxl.write.DateFormat("dd-mm-yyyy"));

                WritableCell c = new jxl.write.DateTime(0, conta, (Date) pair.getKey(), newFormat);
                switch (pair.getValue().toString()){
                    case "Mañanas":
                        newFormat.setBackground(Colour.AQUA);
                        break;
                    case "Tardes":
                        newFormat.setBackground(Colour.ORANGE);
                        break;
                    case "Noches":
                        newFormat.setBackground(Colour.PINK);
                        break;
                    default:
//                        newFormat.setBackground(Colour.WHITE);
                        break;
                }

                c.setCellFormat(newFormat);
                sheetA.addCell(c);
                conta++;
            }

            // close workbook
            workbook.write();
            workbook.close();
            Toast.makeText(this.context, "Fichero generado en: " + directory.getAbsolutePath() + "/" + csvFile, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}