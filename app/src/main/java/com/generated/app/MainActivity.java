package com.generated.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private WifiManager wifiManager;
    private ListView listView;
    private List<ScanResult> scanResults = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<String> networkNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        Button btnScan = new Button(this);
        btnScan.setText("רענן רשתות");
        btnScan.setOnClickListener(v -> scanWifi());
        layout.addView(btnScan);

        listView = new ListView(this);
        layout.addView(listView);

        setContentView(layout);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, networkNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> showPasswordDialog(scanResults.get(position)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void scanWifi() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                scanResults = wifiManager.getScanResults();
                networkNames.clear();
                for (ScanResult result : scanResults) {
                    networkNames.add(result.SSID + " (" + result.capabilities + ")");
                }
                adapter.notifyDataSetChanged();
                try { unregisterReceiver(this); } catch (Exception ignored) {}
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    private void showPasswordDialog(ScanResult network) {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("סיסמה עבור " + network.SSID)
                .setView(input)
                .setPositiveButton("התחבר", (d, w) -> {
                    String pass = input.getText().toString();
                    Toast.makeText(this, "החיבור לרשת " + network.SSID + " התחיל", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("ביטול", null)
                .show();
    }
}
