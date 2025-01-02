package com.myprograms.immunicare.calendar;

import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class RedCircleDecorator implements DayViewDecorator {

    private final CalendarDay day;
    private final Drawable circleDrawable;

    public RedCircleDecorator(CalendarDay day, Drawable circleDrawable) {
        this.day = day;
        this.circleDrawable = circleDrawable;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return this.day.equals(day); // Compare to see if it's the date you want to highlight
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(circleDrawable); // Set the red circle background
    }
}