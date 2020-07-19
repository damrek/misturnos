package com.app.misturnos;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.app.misturnos.ui.MainActivity;
import com.app.misturnos.utils.BackgroundColorSpan;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

/**
 * Decorate several days with a dot
 */
public class EventDecorator implements DayViewDecorator {

    private int color;
    private HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        String eventStyle = App.sharedPreferences.getString("eventStyle", "bg");
        if(eventStyle.equalsIgnoreCase("bg")){
            view.addSpan(new BackgroundColorSpan(color, 16, 16));
            view.addSpan(new ForegroundColorSpan(Color.WHITE));
        }else{
            view.addSpan(new DotSpan(16, color));
        }
    }

    public int getColor() {
        return color;
    }

    public HashSet<CalendarDay> getDates() {
        return dates;
    }

}