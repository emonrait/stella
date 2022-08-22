package com.raihan.stella.model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.raihan.stella.activity.LogOutTimerUtil;
import com.raihan.stella.activity.MainActivity;

import java.util.Calendar;
import java.util.Date;


public class AutoLogout extends AppCompatActivity implements View.OnTouchListener, LogOutTimerUtil.LogOutListener {

    public Runnable mRunnable;
    public Handler mHandler;
    // private long mTime = 3 * 60 * 1000;
    private int count;
    Activity activity;
    private Boolean isPause = false;
    private Date pauseTime;
    private Date currentTime;
    long pasusMillisecon = 0L;


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        //globalVariable.pauseTime = 0;

        LogOutTimerUtil.startLogoutTimer(this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimerUtil.startLogoutTimer(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
        long time = System.currentTimeMillis();
        pauseTime = Calendar.getInstance().getTime();
        pasusMillisecon = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            isPause = false;
            // var currentTime = System.currentTimeMillis()
            currentTime = Calendar.getInstance().getTime();

            // val diffInMs: Long = currentTime.getTime() - pauseTime.getTime()
            long difm = System.currentTimeMillis() - pasusMillisecon;
            //   resumeTime.
            //  val difi = resumeTime - pauseTime
            // Log.e("idl time onResume-->",diffInMs.toString())
            //  val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
            // Log.e("idl diffInSec resume-->",diffInSec.toString())
            Intent intent = new Intent(this, MainActivity.class);
            // CustomActivityClear.logoutExpireTime(intent, this, difm, LogOutTimerUtil.LOGOUT_TIME)

            if (!isFinishing()) {
                try {
                    DialogCustom.logoutExpireTime(
                            intent,
                            this,
                            difm,
                            LogOutTimerUtil.LOGOUT_TIME
                    );
                } catch (WindowManager.BadTokenException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    hideKeyboard(this);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void doLogout() {
        Intent intent = new Intent(this, MainActivity.class);
        Activity activity = this;
       /* mHandler = new Handler(Looper.getMainLooper());

        mRunnable = () -> {
            count++;
            if (count == 1) {
                Toast.makeText(getApplicationContext(),
                        "User inactive for" + String.valueOf(new long[]{mTime / 1000}) + "secs!",
                        Toast.LENGTH_SHORT).show();
                Log.e("Called-------", String.valueOf(mTime));
                stopHandler();
                //startActivity(intent)
                DialogCustom.doClearActivityWithSession(intent, this);
            }
            stopHandler();
        };
        startHandler();*/

        //startActivity(intent);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    try {
                        DialogCustom.doClearActivityWithSession(intent, activity);
                    } catch (WindowManager.BadTokenException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
