/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.isens.module.serialport;

import com.isens.module.DLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * 
 *
 * @author futg
 * 2016-5-03
 */
public class SerialPortApi extends DLog {
	static final String TAG = "Moudle.SerialPortAPI";
	public static final int SERIAL_MODE_NORMAL = 1;
	public static final int SERIAL_MODE_QC = 2;
	public static final int SERIAL_MODE_BOOTLOADER = 3;

	private Application mApplication;
	private Application mApplication0;
	private SerialPort mSerialPort;
	private SerialPort mSerialPort0;
	private OutputStream mOutputStream;
	private OutputStream mOutputStream0;
	private InputStream mInputStream;
	private InputStream mInputStream0;

	public void openSerialPortBarCode() {
		mApplication = new Application();// (Application) getApplication()
		try {
			mSerialPort = mApplication.getSerialPort("/dev/ttyMT1", 9600,'s',1000);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

		} catch (SecurityException e) {
			DLog.e(TAG, "error security");
			//DisplayError(R.string.error_security);
		} catch (IOException e) {
			DLog.e(TAG, "error unknown");
			//DisplayError(R.string.error_unknown);
		}
	}
	public void openSerialPortBGM(int mode) {
		mApplication0 = new Application();// (Application) getApplication()
		try {
			if(mode==SERIAL_MODE_NORMAL) {
				mSerialPort0 = mApplication0.getSerialPort("/dev/ttyMT0", 115200, 's', 100);
			}else if(mode==SERIAL_MODE_BOOTLOADER){
				mSerialPort0 = mApplication0.getSerialPort("/dev/ttyMT0", 115200, 'e', 100);
			}else if(mode==SERIAL_MODE_QC){
				mSerialPort0 = mApplication0.getSerialPort("/dev/ttyMT0", 9600, 's', 1000);
			}
			mOutputStream0 = mSerialPort0.getOutputStream();
			mInputStream0 = mSerialPort0.getInputStream();

		} catch (SecurityException e) {
			DLog.e(TAG, "error security");
			//DisplayError(R.string.error_security);
		} catch (IOException e) {
			DLog.e(TAG, "error unknown");
			//DisplayError(R.string.error_unknown);
		}
	}

	//protected abstract void onDataReceived(final byte[] buffer, final int size);

	public void closeSerialPortBarCode() {
		if((mApplication==null)||(mSerialPort==null)){
			return;
		}
		mApplication.closeSerialPort();
		mSerialPort = null;
	}
	public void closeSerialPortBGM() {
		if((mApplication0==null)||(mSerialPort0==null)){
			return;
		}
		mApplication0.closeSerialPort();
		mSerialPort0 = null;
	}
	public boolean isAvailableSerialPortBarCode(){
		if(mSerialPort!=null){
			return true;
		}
		return false;
	}
	public boolean isAvailableSerialPortBGM(){
		if(mSerialPort0!=null){
			return true;
		}
		return false;
	}
	public OutputStream getOutputStreamBarcode() {
		return mOutputStream;
	}
	public OutputStream getOutputStreamBGM() {
		return mOutputStream0;
	}
	public InputStream getInputStreamBarcode() {
		return mInputStream;
	}
	public InputStream getInputStreamBGM() {
		return mInputStream0;
	}
}
