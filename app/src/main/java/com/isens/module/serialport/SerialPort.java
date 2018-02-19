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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
public class SerialPort extends DLog {
	static final String TAG = "Moudle.SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	
	public SerialPort(File device, int baudrate, int flags,int parity, int timeout) throws SecurityException, IOException {

		/* Check access permission */

		if (!device.canRead() || !device.canWrite()) {
			DLog.e(TAG, "native can not read");
			try {
				// Missing read/write permission, trying to chmod the file
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");// 
//				su = Runtime.getRuntime().exec("/system/xbin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}
		else
		{
			DLog.e(TAG, "native can read");
		}

		mFd = open(device.getAbsolutePath(), baudrate,8,1,parity,timeout, flags);
		DLog.e(TAG, device.getAbsolutePath());
		if (mFd == null) {
			DLog.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);		
	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	// JNI
	//private native static FileDescriptor open(String path, int baudrate, int flags);
	private native static FileDescriptor open(String path, int baudrate, int databits, int stopbits, int parity, int timeout, int flags);
	public native void close();
	static {
		System.loadLibrary("serial_port");
	}
}
