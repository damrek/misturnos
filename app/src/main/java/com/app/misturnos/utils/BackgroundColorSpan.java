package com.app.misturnos.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.style.LineBackgroundSpan;

public class BackgroundColorSpan implements LineBackgroundSpan {
    private float padding;
    private float radius;

    private RectF rect = new RectF();
    private Paint paint = new Paint();
    private Paint paintStroke = new Paint();
    private Path path = new Path();

    private float prevWidth = -1f;
    private float prevLeft = -1f;
    private float prevRight = -1f;
    private float prevBottom = -1f;
    private float prevTop = -1f;


    public BackgroundColorSpan(int backgroundColor,
                               float padding,
                               float radius) {
        this.padding = padding;
        this.radius = radius;

        paint.setColor(backgroundColor);
        //paintStroke.setStyle(Paint.Style.STROKE);
        //paintStroke.setStrokeWidth(5f);
        paintStroke.setColor(backgroundColor);
    }

    @Override
    public void drawBackground(
            final Canvas c,
            final Paint p,
            final int left,
            final int right,
            final int top,
            final int baseline,
            final int bottom,
            final CharSequence text,
            final int start,
            final int end,
            final int lnum) {

        float width = p.measureText(text, start, end) + 2f * padding;
        float shift = (right - width) / 2f;

        rect.set(shift, top, right - shift, bottom);

        if (lnum == 0) {
            c.drawRoundRect(rect, radius, radius, paint);
        } else {
            path.reset();
            float dr = width - prevWidth;
            float diff = -Math.signum(dr) * Math.min(2f * radius, Math.abs(dr/2f))/2f;
            path.moveTo(
                    prevLeft, prevBottom - radius
            );

            path.cubicTo(
                    prevLeft, prevBottom - radius,
                    prevLeft, rect.top,
                    prevLeft + diff, rect.top
            );
            path.lineTo(
                    rect.left - diff, rect.top
            );
            path.cubicTo(
                    rect.left - diff, rect.top,
                    rect.left, rect.top,
                    rect.left, rect.top + radius
            );
            path.lineTo(
                    rect.left, rect.bottom - radius
            );
            path.cubicTo(
                    rect.left, rect.bottom - radius,
                    rect.left, rect.bottom,
                    rect.left + radius, rect.bottom
            );
            path.lineTo(
                    rect.right - radius, rect.bottom
            );
            path.cubicTo(
                    rect.right - radius, rect.bottom,
                    rect.right, rect.bottom,
                    rect.right, rect.bottom - radius
            );
            path.lineTo(
                    rect.right, rect.top + radius
            );
            path.cubicTo(
                    rect.right, rect.top + radius,
                    rect.right, rect.top,
                    rect.right + diff, rect.top
            );
            path.lineTo(
                    prevRight - diff, rect.top
            );
            path.cubicTo(
                    prevRight - diff, rect.top,
                    prevRight, rect.top,
                    prevRight, prevBottom - radius
            );
            path.cubicTo(
                    prevRight, prevBottom - radius,
                    prevRight, prevBottom,
                    prevRight - radius, prevBottom

            );
            path.lineTo(
                    prevLeft + radius, prevBottom
            );
            path.cubicTo(
                    prevLeft + radius, prevBottom,
                    prevLeft, prevBottom,
                    prevLeft, rect.top - radius
            );
            c.drawPath(path, paintStroke);
        }

        prevWidth = width;
        prevLeft = rect.left;
        prevRight = rect.right;
        prevBottom = rect.bottom;
        prevTop = rect.top;
    }
}