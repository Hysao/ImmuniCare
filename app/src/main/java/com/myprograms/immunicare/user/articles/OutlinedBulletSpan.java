package com.myprograms.immunicare.user.articles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.BulletSpan;

public class OutlinedBulletSpan extends BulletSpan {

    public OutlinedBulletSpan(int gapWidth, int color, int outlineColor) {
        super(gapWidth, color);
        // Store the outline color
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                  int top, int baseline, int bottom,
                                  CharSequence text, int start, int end,
                                  boolean first, Layout l) {
        // Draw the white bullet (call super method)
        super.drawLeadingMargin(c, p, x, dir, top, baseline, bottom, text, start, end, first, l);

        // Draw the black outline (slightly smaller circle)
        // ... (code to draw a circle with the outline color)
    }
}