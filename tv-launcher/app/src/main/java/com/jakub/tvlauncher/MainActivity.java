package com.jakub.tvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {

    // Kolejność kafelków na ekranie głównym. Zainstalowane aplikacje pokazują się same.
    private static final String[] FEATURED = {
            "com.canal.box",
            "pl.canalplus.tvod.atv",
            "com.netflix.ninja",
            "com.google.android.youtube.tv",
            "com.disney.disneyplus",
            "com.wbd.stream",
            "pl.tvn.player.tv",
            "com.elevensportsnetwork.elevenpoland",
            "com.spotify.tv.android",
            "tv.twitch.android.app",
            "com.android.vending",
    };

    private static class Entry {
        final String label;
        final Drawable icon;
        final Intent intent;
        final boolean showAll;

        Entry(String label, Drawable icon, Intent intent, boolean showAll) {
            this.label = label;
            this.icon = icon;
            this.intent = intent;
            this.showAll = showAll;
        }
    }

    private final List<Entry> entries = new ArrayList<>();
    private GridView grid;
    private TextView title;
    private EntryAdapter adapter;
    private boolean allAppsMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#0E1217"));

        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(64), dp(40), dp(64), dp(24));

        FrameLayout header = new FrameLayout(this);
        title = new TextView(this);
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        header.addView(title, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.START | Gravity.CENTER_VERTICAL));

        TextClock clock = new TextClock(this);
        clock.setFormat24Hour("HH:mm");
        clock.setFormat12Hour("HH:mm");
        clock.setTextColor(Color.parseColor("#B8C0CC"));
        clock.setTextSize(28);
        header.addView(clock, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.END | Gravity.CENTER_VERTICAL));
        column.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        grid = new GridView(this);
        grid.setNumColumns(5);
        grid.setHorizontalSpacing(dp(16));
        grid.setVerticalSpacing(dp(16));
        grid.setSelector(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        grid.setClipToPadding(false);
        grid.setPadding(0, dp(32), 0, 0);
        adapter = new EntryAdapter();
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                open(entries.get(position));
            }
        });
        column.addView(grid, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(column, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);
        showHome();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Odśwież listę po instalacji lub odinstalowaniu aplikacji.
        if (allAppsMode) {
            showAll();
        } else {
            showHome();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Przycisk Home zawsze wraca na ekran główny.
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            showHome();
            grid.setSelection(0);
        }
    }

    @Override
    public void onBackPressed() {
        if (allAppsMode) {
            showHome();
        }
        // Na ekranie głównym launchera Back nic nie robi.
    }

    private void open(Entry entry) {
        if (entry.showAll) {
            showAll();
            return;
        }
        try {
            startActivity(entry.intent);
        } catch (Exception ignored) {
            // Aplikacja mogła zostać usunięta lub wyłączona.
        }
    }

    private void showHome() {
        allAppsMode = false;
        title.setText("Start");
        entries.clear();
        PackageManager pm = getPackageManager();
        for (String pkg : FEATURED) {
            Intent launch = launchIntent(pm, pkg);
            if (launch == null) continue;
            try {
                entries.add(new Entry(
                        pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString(),
                        pm.getApplicationIcon(pkg), launch, false));
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        entries.add(new Entry("Wszystkie aplikacje", pm.getDefaultActivityIcon(), null, true));
        Intent settings = new Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        entries.add(new Entry("Ustawienia", pm.getDefaultActivityIcon(), settings, false));
        adapter.notifyDataSetChanged();
    }

    private void showAll() {
        allAppsMode = true;
        title.setText("Wszystkie aplikacje");
        entries.clear();
        final PackageManager pm = getPackageManager();
        List<ResolveInfo> found = new ArrayList<>();
        found.addAll(pm.queryIntentActivities(
                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER), 0));
        for (ResolveInfo ri : pm.queryIntentActivities(
                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)) {
            boolean dup = false;
            for (ResolveInfo f : found) {
                if (f.activityInfo.packageName.equals(ri.activityInfo.packageName)) {
                    dup = true;
                    break;
                }
            }
            if (!dup) found.add(ri);
        }
        Collections.sort(found, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                return a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString());
            }
        });
        for (ResolveInfo ri : found) {
            if (ri.activityInfo.packageName.equals(getPackageName())) continue;
            Intent launch = launchIntent(pm, ri.activityInfo.packageName);
            if (launch == null) continue;
            entries.add(new Entry(ri.loadLabel(pm).toString(), ri.loadIcon(pm), launch, false));
        }
        adapter.notifyDataSetChanged();
        grid.setSelection(0);
    }

    private static Intent launchIntent(PackageManager pm, String pkg) {
        Intent launch = pm.getLeanbackLaunchIntentForPackage(pkg);
        if (launch == null) launch = pm.getLaunchIntentForPackage(pkg);
        if (launch != null) launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return launch;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private Drawable tileBackground() {
        GradientDrawable normal = new GradientDrawable();
        normal.setColor(Color.parseColor("#1A212B"));
        normal.setCornerRadius(dp(14));
        GradientDrawable selected = new GradientDrawable();
        selected.setColor(Color.parseColor("#2C3A4D"));
        selected.setCornerRadius(dp(14));
        selected.setStroke(dp(3), Color.WHITE);
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_selected}, selected);
        states.addState(new int[]{android.R.attr.state_focused}, selected);
        states.addState(new int[]{}, normal);
        return states;
    }

    private class EntryAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public Object getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout tile;
            ImageView icon;
            TextView label;
            if (convertView == null) {
                tile = new LinearLayout(MainActivity.this);
                tile.setOrientation(LinearLayout.VERTICAL);
                tile.setGravity(Gravity.CENTER);
                tile.setPadding(dp(12), dp(16), dp(12), dp(16));
                tile.setBackground(tileBackground());
                icon = new ImageView(MainActivity.this);
                tile.addView(icon, new LinearLayout.LayoutParams(dp(72), dp(72)));
                label = new TextView(MainActivity.this);
                label.setTextColor(Color.WHITE);
                label.setTextSize(16);
                label.setGravity(Gravity.CENTER);
                label.setSingleLine(true);
                label.setPadding(0, dp(10), 0, 0);
                tile.addView(label);
                tile.setLayoutParams(new AbsListViewLayoutParams(dp(150)));
            } else {
                tile = (LinearLayout) convertView;
                icon = (ImageView) tile.getChildAt(0);
                label = (TextView) tile.getChildAt(1);
            }
            Entry entry = entries.get(position);
            icon.setImageDrawable(entry.icon);
            label.setText(entry.label);
            return tile;
        }
    }

    private static class AbsListViewLayoutParams extends android.widget.AbsListView.LayoutParams {
        AbsListViewLayoutParams(int height) {
            super(ViewGroup.LayoutParams.MATCH_PARENT, height);
        }
    }
}
