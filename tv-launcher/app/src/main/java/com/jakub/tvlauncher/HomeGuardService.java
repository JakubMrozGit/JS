package com.jakub.tvlauncher;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Dekoder Canal+ przejmuje przycisk Home przez com.canal.box (BoxMainActivity).
 * Gdy ten ekran wychodzi na wierzch, otwieramy własny launcher.
 */
public class HomeGuardService extends AccessibilityService {

    private static final String CANAL_HOME_ACTIVITY = "com.canal.box.ui.boot.BoxMainActivity";
    private static final long MIN_INTERVAL_MS = 1500;

    private static final String TAG = "HomeGuard";

    /** Do tego czasu nie reagujemy na ekran Canal+ (start Canal+ z naszego kafelka). */
    static volatile long suppressWindowRedirectUntil;

    private long lastLaunch;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return;
        CharSequence className = event.getClassName();
        if (className == null || !CANAL_HOME_ACTIVITY.contentEquals(className)) return;
        if (System.currentTimeMillis() < suppressWindowRedirectUntil) return;

        openLauncher();
    }

    /** Przechwytuje przycisk Home zanim dojdzie do Canal+, więc nie czekamy na jego ekran. */
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d(TAG, "key " + event.getKeyCode() + " action " + event.getAction());
        if (event.getKeyCode() != KeyEvent.KEYCODE_HOME) return false;
        if (event.getAction() == KeyEvent.ACTION_UP) openLauncher();
        return true;
    }

    private void openLauncher() {
        long now = System.currentTimeMillis();
        if (now - lastLaunch < MIN_INTERVAL_MS) return;
        lastLaunch = now;

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {
    }
}
