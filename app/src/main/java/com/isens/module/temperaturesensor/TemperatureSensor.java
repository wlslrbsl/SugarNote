package com.isens.module.temperaturesensor;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by admin on 2016-06-28.
 */
public class TemperatureSensor {

    private String mCurrentValue = "0";

    public String getCurrentValue(){
        if (new File("/sys/devices/platform/battery_meter/FG_Sensor_Temperature").exists()) {

            final String filename = "/sys/devices/platform/battery_meter/FG_Sensor_Temperature";
            FileReader reader = null;
            try {
                reader = new FileReader(filename);
                char[] buf = new char[15];
                int n = reader.read(buf);

                mCurrentValue = new String(buf, 0, n-1);
                Log.e("tv_temp","mCurrentValue:"+mCurrentValue);
            } catch (IOException ex) {
                Log.e("tv_temp", "Couldn't read hdmi state from " + filename + ": " + ex);
            } catch (NumberFormatException ex) {
                Log.e("tv_temp", "Couldn't read hdmi state from " + filename + ": " + ex);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }
        return mCurrentValue;
    }
    public void enableTemperatureSensor() {
        // BGM enable GPIO setting
    }
    public void disableTemperatureSensor() {
        // BGM enable GPIO setting
    }
}
