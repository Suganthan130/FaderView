package com.suganthan.faderview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FaderView extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap bitmap;
    private double gain;
    private int height;
    private OnFaderChangeListener listener;
    private boolean lock;
    private Paint paint;
    private RectF roundedRect;
    private int viewHeight;
    private int viewWidth;
    private int width;
    private int x;
    private int y;

    public FaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint = new Paint();
        this.x = -1;
        this.y = -1;
        this.lock = false;
        this.gain = Double.NEGATIVE_INFINITY;
        this.listener = null;
        setZOrderOnTop(true);
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.setFormat(-2);
        getHolder().addCallback(this);
        setFocusable(true);
        this.bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_fast_rewind_24);
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
    }

    public void setOnChange(OnFaderChangeListener l) {
        this.listener = l;
    }

    public boolean changeLockStatus() {
        if (this.lock) {
            this.lock = false;
        } else {
            this.lock = true;
        }
        return this.lock;
    }

    public double getGain() {
        return this.gain;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.lock) {
            return false;
        }
        if (event.getAction() != 2) {
            performClick();
        }
        this.y = (int) event.getY();
        if (this.y < this.height / 2) {
            this.y = this.height / 2;
        }
        if (this.y > this.viewHeight - (this.height / 2)) {
            this.y = this.viewHeight - (this.height / 2);
        }
        if (this.listener != null) {
            double ampl = 32768.0d - (((this.y - (this.height / 2)) * 32768.0d) / (this.viewHeight - this.height));
            this.gain = 20.0d * Math.log10(ampl / 32768.0d);
            this.listener.onChange(this.gain);
        }
        update();
        return true;
    }

    @Override // android.view.View
    public boolean performClick() {
        return super.performClick();
    }

    private void update() {
        if (this.x == -1) {
            this.x = this.viewWidth / 2;
            this.y = this.viewHeight - (this.height / 2);
        }
        Canvas canvas = null;
        try {
            getHolder().setFormat(-3);
            canvas = getHolder().lockCanvas(null);
            if (canvas != null) {
                synchronized (getHolder()) {
                    draw(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder) {
        this.viewWidth = getWidth();
        this.viewHeight = getHeight();
        this.roundedRect = new RectF((this.viewWidth / 2) - 15, 20.0f, (this.viewWidth / 2) + 15, this.viewHeight - 20);
        update();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        this.paint.setColor(-5592406);
        this.paint.setStrokeWidth(3.0f);
        this.paint.setStyle(Paint.Style.STROKE);
        int h = 65;
        int w = 15;
        canvas.drawLine(15.0f, 65, (this.viewWidth / 2) - 25, 65, this.paint);
        canvas.drawLine((this.viewWidth / 2) + 25, 65, this.viewWidth - 15, 65, this.paint);
        canvas.drawLine(30.0f, this.viewHeight - 65, (this.viewWidth / 2) - 25, this.viewHeight - 65, this.paint);
        canvas.drawLine((this.viewWidth / 2) + 25, this.viewHeight - 65, this.viewWidth - 30, this.viewHeight - 65, this.paint);
        int step = 6;
        if (this.viewHeight > 300) {
            step = 8;
        }
        if (this.viewHeight > 400) {
            step = 10;
        }
        if (this.viewHeight > 500) {
            step = 12;
        }
        if (this.viewHeight > 600) {
            step = 14;
        }
        if (this.viewHeight > 800) {
            step = 16;
        }
        int hstep = (this.viewHeight - 130) / step;
        int wstep = 20 / step;
        for (int i = 1; i != step; i++) {
            h += hstep;
            w += wstep;
            if (i == step / 2) {
                this.paint.setStrokeWidth(3.0f);
            } else {
                this.paint.setStrokeWidth(1.0f);
            }
            canvas.drawLine(w, h, (this.viewWidth / 2) - 25, h, this.paint);
            canvas.drawLine((this.viewWidth / 2) + 25, h, this.viewWidth - w, h, this.paint);
        }
        this.paint.setColor(-1879048192);
        this.paint.setStrokeWidth(0.0f);
        this.paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(this.roundedRect, 5.0f, 5.0f, this.paint);
        this.paint.setColor(-14540254);
        this.paint.setStrokeWidth(5.0f);
        this.paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(this.roundedRect, 5.0f, 5.0f, this.paint);
        canvas.drawBitmap(this.bitmap, this.x - (this.width / 2), this.y - (this.height / 2), this.paint);
    }
}
