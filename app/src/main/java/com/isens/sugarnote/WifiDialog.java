package com.isens.sugarnote;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BSPL on 2017-07-14.
 */

public class WifiDialog extends Dialog implements View.OnClickListener {

    private Button btn_wifi_scan, btn_wifi_ok, btn_wifi_back, btn_wifi_cancel;
    private Switch sw_wifi_dis;
    private WifiManager wifi;
    private ListView list_wifi;
    private TextView tv_wifiInfo;
    private List<ScanResult> results;
    private int size = 0;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private Context mContext;
    private EditText edt_pw;
    private String checkPassword = null;
    private Dialog dialog;

    public WifiDialog(@NonNull Context context) {
        super(context);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_wifi);

        tv_wifiInfo = (TextView) findViewById(R.id.tv_wifiInfo);
        list_wifi = (ListView) findViewById(R.id.list_wifi);
        sw_wifi_dis = (Switch) findViewById(R.id.sw_wifi_dis);
        btn_wifi_scan = (Button) findViewById(R.id.btn_wifi_scan);
        btn_wifi_back = (Button) findViewById(R.id.btn_wifi_back);
        btn_wifi_scan.setOnClickListener(this);
        btn_wifi_back.setOnClickListener(this);


        mContext = getContext();

        wifi = (WifiManager) mContext.getApplicationContext().getSystemService(mContext.WIFI_SERVICE);

        if (!wifi.isWifiEnabled()) {
            sw_wifi_dis.setChecked(false);
            tv_wifiInfo.setText("Wifi is disabled now");
        } else {
            sw_wifi_dis.setChecked(true);
            btn_wifi_scan.setActivated(false);
            refreshInfo(); // 타임딜레이를 주고 싶다
        }

        this.adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, arrayList);
        list_wifi.setAdapter(this.adapter);

        list_wifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectToWifi(position);
            }
        });

        sw_wifi_dis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sw_wifi_dis.isChecked()) {

                    wifi.setWifiEnabled(true);
                    Toast.makeText(mContext, "wifi is enabled", Toast.LENGTH_SHORT).show();
                    btn_wifi_scan.setActivated(false);
                    refreshInfo();
                } else {
                    list_wifi.setVisibility(View.INVISIBLE);
                    wifi.setWifiEnabled(false);
                    Toast.makeText(mContext, "wifi is disabled", Toast.LENGTH_SHORT).show();
                    tv_wifiInfo.setText("Wifi is disabled now");
                }
            }
        });

        list_wifi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "길게 눌렀네요!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    public void refreshInfo() {
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        String connectionInfo = wifiInfo.getNetworkId() + " : " + wifiInfo.getSSID() + " \n " + wifiInfo.getBSSID();
        tv_wifiInfo.setText(connectionInfo);
    }

    private void connectToWifi(final int position) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_connect);

        TextView tv_ssid = (TextView) dialog.findViewById(R.id.tv_ssid);
        TextView tv_bssid = (TextView) dialog.findViewById(R.id.tv_bssid);
        TextView tv_capa = (TextView) dialog.findViewById(R.id.tv_capa);

        btn_wifi_cancel = (Button) dialog.findViewById(R.id.btn_wifi_cancel);
        btn_wifi_ok = (Button) dialog.findViewById(R.id.btn_wifi_ok);
        edt_pw = (EditText) dialog.findViewById(R.id.edt_pw);

        tv_ssid.setText(results.get(position).SSID);
        tv_bssid.setText(results.get(position).BSSID);
        tv_capa.setText(results.get(position).capabilities);

        btn_wifi_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_wifi_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassword = edt_pw.getText().toString();
                finallyConnect(checkPassword, position);
                refreshInfo();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void finallyConnect(String checkPassword, int position) {
        String networkSSID = results.get(position).SSID;
        String networkPassword = checkPassword;

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPassword);

        // remember id
        int netId = wifi.addNetwork(wifiConfig);
        wifi.disconnect();
        wifi.enableNetwork(netId, true);
        wifi.reconnect();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + networkSSID + "\"\"";
        conf.preSharedKey = "\"" + networkPassword + "\"";
        wifi.addNetwork(conf);

        if (wifi.enableNetwork(netId, true)) {
            Toast.makeText(mContext, "wifi connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "check your PW", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_wifi_scan:
                list_wifi.setVisibility(View.VISIBLE);
                arrayList.clear();
                if(sw_wifi_dis.isChecked()) {
                    Toast.makeText(mContext, "Scanning...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Wifi is not working", Toast.LENGTH_SHORT).show();
                }
                mContext.registerReceiver(wifi_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifi.startScan();
                break;

            case R.id.btn_wifi_back:
                dismiss();
                break;
        }
    }

    BroadcastReceiver wifi_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifi.getScanResults();
            size = results.size();
            mContext.unregisterReceiver(this);

            try {
                while (size > 0) {
                    arrayList.add(results.get(results.size()-size).SSID);
                    adapter.notifyDataSetChanged();
                    size--;
                }
            } catch (Exception e) {

            }
        }
    };
}
