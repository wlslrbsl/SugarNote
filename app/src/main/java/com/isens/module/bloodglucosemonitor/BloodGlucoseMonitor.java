package com.isens.module.bloodglucosemonitor;

import com.isens.module.ByteQueue;
import com.isens.module.DLog;
import com.isens.module.ParsingString;
import com.isens.module.serialport.SerialPortApi;
import com.mediatek.engineermode.io.EmGpio;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2016-06-27.
 */

public class BloodGlucoseMonitor extends DLog {
    static final String TAG = "Moudle.BGM";
    EmGpio memGpio;
    private BloodGlucoseMonitorCallBack bloodGlucoseMonitorCallBack;
    ParsingString mParsingString;

    SerialPortApi mSerialPortApi;
    private boolean needMoreByte=false;
    private boolean bgmisrun = false;
    private boolean bootNeedAck = false;
    private int bootTimeOutcnt = 0;
    private static final int  BOOT_TIME_OUT =	1000;   //1sec
    private boolean bgmIntoOn = false;
    private boolean bgmControlSolutionMode = false;
    public static final boolean BGM_CONTROLSOLUTION_MODE = true;
    public static final boolean BGM_BLOOD_MODE = false;
    private int bgmPreStatus=0;
    private int stripInfo = 0;
    private int protocollenth=0;
    private int protocolcommand = 0;
    private int downloadAddress = 0;
    private int downloadPercent = 0;
    public  BgmBootLoader mBootLoader;
    private static BgmResults mBgmResults;
    private static BgmVersion mBgmVersion;
    boolean bootNeedCallback=false;
    String bootLoaderAck;

    private byte validStrip=(byte)0x1e;
    private byte rawDataSend=0;

    public static final int  STRIP_INFO_NO_STRIP =	0xff;
    public static final int  STRIP_INFO_INSERTED_STRIP =	0xfe;
    public static final int  STRIP_INFO_BAROZEN =	0x00;
    public static final int  STRIP_INFO_PRO =		0x01;
    public static final int  STRIP_INFO_KETONE_PRO =	0x02;
    public static final int  STRIP_INFO_KETONE_BAROZEN =	0x03;

    public static final byte  STRIP_SET_BAROZEN =	0x02;
    public static final byte  STRIP_SET_PRO =		0x04;
    public static final byte  STRIP_SET_KETONE_PRO =	0x08;
    public static final byte  STRIP_SET_KETONE_BAROZEN =	0x10;
    private static final byte  RAWDATA_SET =	(byte)0x02;
    public static final byte  STRIP_SET_ALL =	(byte)0x1e;

    public static final boolean BGM_INT_ON = true;
    public static final boolean BGM_INT_OFF = false;
    public static final boolean BGM_INT_SWITCH = BGM_INT_OFF;

    public static final int BGM_MODE_NORMAL = 1;
    public static final int BGM_MODE_QC = 2;
    public static final int BGM_MODE_BOOTLOADER = 3;

    public static final String BGM_BOOT_COMPLETE = "DOWN_COMP\r\n";
    public static final String BGM_BOOT_ERASE = "ERAS_STRT\r\n";
    public static final String BGM_BOOT_DOWNLOAD_START = "DOWN_STRT\r\n";
    public static final String BGM_BOOT_DOWNLOADING = "DWNI_";
    public static final String BGM_BOOT_VERIFYING = "VERI_";
    public static final String BGM_BOOT_ERROR = "ERRR_";

    public static final int BGM_PROBLEM_NOTCAL = 1;
    public static final int BGM_PROBLEM_NOTQC = 2;
    public static final int BGM_PROBLEM_QCERROR = 3;
    public static final int BGM_PROBLEM_QCSUCESS = 4;
    public static final int BGM_PROBLEM_FLASHFAIL = 5;
    public static final int BGM_PROBLEM_INQC_NOCODING = 6;
    public static final int BGM_PROBLEM_INQC_CURRENT = 7;
    public static final int BGM_PROBLEM_QCCOMPLETE = 8;


    public static final int BGM_STATUS_SLEEP = 0;
    public static final int BGM_STATUS_INSERT_STRIP = 1;
    public static final int BGM_STATUS_OUT_STRIP = 2;
    public static final int BGM_STATUS_DROP_BLOOD = 3;
    public static final int BGM_STATUS_PROCESS_START = 5;
    public static final int BGM_STATUS_RESULT_TEMPERATURE = 6;
    public static final int BGM_STATUS_RESULT_GLUCOSE = 7;
    public static final int BGM_STATUS_RESULT_CONTROLSOLUTION = 8;
    public static final int BGM_STATUS_RESULT_CURRENT = 9;
    public static final int BGM_STATUS_RESULT_KETONE = 10;
    public static final int BGM_STATUS_RESULT_KETONE_CS = 11;
    public static final int BGM_STATUS_NOCODING = 12;
    public static final int BGM_STATUS_RAWDATA = 13;
    public static final int BGM_STATUS_ERROR = 100;
    public static final int BGM_STATUS_PARSE_ERROR = 101;
    public static final int BGM_STATUS_READ_VERSION = 102;
    public static final int BGM_STATUS_QC_MODE_READ = 200;
    public static final int BGM_STATUS_QC_MODE_WRITE = 201;
    public static final int BGM_STATUS_QC_MODE_ECO = 202;
    public static final int BGM_STATUS_QC_MODE_RESET = 203;
    public static final int BGM_STATUS_QC_MODE_READ_SN = 204;
    public static final int BGM_STATUS_BOOTLOADER = 205;
    public static final int BGM_STATUS_METER_PROBLEM = 206;
    public static final int BGM_ERROR_1 = 1;
    public static final int BGM_ERROR_2 = 2;
    public static final int BGM_ERROR_3 = 3;
    public static final int BGM_ERROR_4 = 4;
    public static final int BGM_ERROR_5 = 5;
    public static final int BGM_ERROR_6 = 6;
    public static final int BGM_ERROR_7 = 7;
    public static final int BGM_ERROR_8 = 8;

    private int bgmOperatingMode = 0;

    private static final byte BGM_PROTOCOL_HEADER = 0x24;
    private static final byte BGM_PROTOCOL_TAIL = 0x3b;

    private static final byte MODULE_DATA_COMM_DLE =0x10;
    private static final byte MODULE_DATA_COMM_STX =0x02;
    private static final byte MODULE_DATA_COMM_ETX =0x03;

    private static final byte BGM_CMD_METER_PROBLEM = 0x02;
    private static final byte BGM_CMD_STRIP_OUT = 0x09;
    private static final byte BGM_CMD_STRIP_IN = 0x10;
    private static final byte BGM_CMD_DROP_BLOOD = 0x12;
    private static final byte BGM_CMD_PROCESS = 0x13;
    private static final byte BGM_CMD_MEASUREMENT = 0x18;
    private static final byte BGM_CMD_TEMPERATURE = 0x19;
    private static final byte BGM_CMD_CURRENT = 0x1A;
    private static final byte BGM_CMD_CONTROL_SOLUTION = 0x1B;
    private static final byte BGM_CMD_MEASUREMENT_KETONE = 0x35;
    private static final byte BGM_CMD_CONTROL_SOLUTION_KETONE = 0x36;
    private static final byte BGM_CMD_NOCODING = (byte)0xE0;
    private static final byte BGM_CMD_RAW_SEND_START = (byte)0xE1;
    private static final byte BGM_CMD_RAW_SEND = (byte)0xE2;
    private static final byte BGM_CMD_COM_ACK = (byte)0x06;
    private static final byte READ_VERSION = (byte)0x14;
    private static final byte BGM_CMD_ERROR = (byte)0xF0;

    private static final byte BGM_CMD_QC_ECO =(byte)0x80;
    private static final byte BGM_CMD_QC_START_MEASURE =(byte)0x81;
    private static final byte BGM_CMD_QC_READ_FLASH =(byte)0x8b;
    private static final byte BGM_CMD_QC_WRITE_FLASH =(byte)0x8c;
    private static final byte BGM_CMD_QC_UARTRESET =(byte)0x8d;
    private static final byte BGM_CMD_QC_STX =(byte)0x02;
    private static final byte BGM_CMD_QC_ETX =(byte)0x03;
    private static final byte BGM_CMD_QC_UPPER_FLAG =(byte)0x10;
    private static final byte BGM_CMD_QC_LOWER_FLAG =(byte)0x20;

    private static final byte BGM_CMD_BOOT_ECO =(byte)0x7F;
    private static final byte BGM_CMD_BOOT_GET =(byte)0x00;
    private static final byte BGM_CMD_BOOT_GET_STATUS =(byte)0x01;
    private static final byte BGM_CMD_BOOT_GET_ID =(byte)0x02;
    private static final byte BGM_CMD_BOOT_READ_MEMORY =(byte)0x11;
    private static final byte BGM_CMD_BOOT_GO =(byte)0x21;
    private static final byte BGM_CMD_BOOT_WRITE_MEMORY =(byte)0x31;
    private static final byte BGM_CMD_BOOT_ERASE =(byte)0x43;
    private static final byte BGM_CMD_BOOT_EXT_ERASE =(byte)0x44;
    private static final byte BGM_CMD_BOOT_WRITE_PROTECT =(byte)0x63;
    private static final byte BGM_CMD_BOOT_WRITE_UNPROTECT =(byte)0x73;
    private static final byte BGM_CMD_BOOT_READ_PROTECT =(byte)0x82;
    private static final byte BGM_CMD_BOOT_READ_UNPROTECT =(byte)0x92;

    private static final byte BGM_CMD_BOOT_RSP_ACK =(byte)0x79;
    private static final byte BGM_CMD_BOOT_RSP_NACK =(byte)0x1F;

    private static final byte BGM_BOOTSTATUS =(byte)0x00;
    private static final byte BGM_BOOTSTATUS_SEND_ADDRESS =(byte)0x01;
    private static final byte BGM_BOOTSTATUS_SEND_NUMBER_BYTE =(byte)0x02;
    private static final byte BGM_BOOTSTATUS_RECEIVE_DATA =(byte)0x03;
    private static final byte BGM_BOOTSTATUS_WRITE_DATA =(byte)0x04;


    Thread threadbgm;
    class threadBgm implements Runnable{
        public void run()  {
            try {
                if(uniqueInstance.bgmOperatingMode==BGM_MODE_NORMAL) {
                uniqueInstance.listenerBGM();
                }else if(uniqueInstance.bgmOperatingMode==BGM_MODE_QC){
                    uniqueInstance.listenerBGM_QC();
                }else if(uniqueInstance.bgmOperatingMode==BGM_MODE_BOOTLOADER){
                    uniqueInstance.listenerBGM_BootLoader();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if (uniqueInstance.mSerialPortApi.getInputStreamBGM() != null) {
                        uniqueInstance.mSerialPortApi.getInputStreamBGM().close();
                    }
                    if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                        uniqueInstance.mSerialPortApi.getOutputStreamBGM().close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    threadBgm threadbgm_1;
    private volatile static BloodGlucoseMonitor uniqueInstance;
    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    private BloodGlucoseMonitor() {   }

    public static BloodGlucoseMonitor getInstance() {
        if (uniqueInstance == null) {
            synchronized (BloodGlucoseMonitor.class) {
                    if (uniqueInstance == null) {
                        uniqueInstance = new BloodGlucoseMonitor();
                        if(uniqueInstance.memGpio==null){
                            uniqueInstance.memGpio=new EmGpio();
                            uniqueInstance.memGpio.gpioInit();
                        }
                        if(uniqueInstance.mSerialPortApi==null){
                            uniqueInstance.mSerialPortApi= new SerialPortApi();
                        }
                        if(uniqueInstance.mParsingString==null){
                            uniqueInstance.mParsingString = new ParsingString();
                        }
                        if(mBgmResults==null){
                            mBgmResults = new BgmResults();
                        }
                        if(uniqueInstance.mBootLoader==null){
                            uniqueInstance.mBootLoader = new BgmBootLoader();
                        }
                        uniqueInstance.stripInfo=STRIP_INFO_NO_STRIP;
                        uniqueInstance.mBgmVersion= new BgmVersion();
                        uniqueInstance.setBGMOperatingMode(BGM_MODE_NORMAL);
                        //uniqueInstance.threadbgm.setPriority(1);

                        /*
                        new Thread(new Runnable() {
                                @Override
                            public void run() {
                                uniqueInstance.listenerBGM();
                            }
                        }).start();
                        */
                    }
                }
            }
        return uniqueInstance;
    }
    public static void setCallbackInterface(final BloodGlucoseMonitorCallBack nbgmcallback) {
        if(uniqueInstance!=null){
            uniqueInstance.bloodGlucoseMonitorCallBack = nbgmcallback;
        }

    }
    public void enableBGM(boolean intmode) {
        if(bgmisrun)
            return;
        // BGM enable GPIO setting
        bgmIntoOn=intmode;
        if(memGpio==null){
            memGpio=new EmGpio();
            memGpio.gpioInit();
        }
        bgmisrun=true;

        //memGpio.setGpioOutput(memGpio.GPIO_BGM_BOOT);
        //memGpio.setGpioDataHigh(memGpio.GPIO_BGM_BOOT);
        memGpio.setGpioOutput(memGpio.GPIO_TEMPERATURE);
        memGpio.setGpioDataHigh(memGpio.GPIO_TEMPERATURE);
        if(bgmIntoOn==true){
            memGpio.eintMask(memGpio.GPIO_BGM_INT);//@wanc modify
        }
        memGpio.setGpioOutput(memGpio.GPIO_BGM_POWER);
        memGpio.setGpioDataHigh(memGpio.GPIO_BGM_POWER);
        //EmGpio.setGpioInput(GPIO_BGM_INT);

        memGpio.setGpioOutput(memGpio.GPIO_1D_EN);
        memGpio.setGpioDataHigh(memGpio.GPIO_1D_EN);

        if(getBGMOperatingMode()==BGM_MODE_BOOTLOADER){
            memGpio.setGpioOutput(memGpio.GPIO_BGM_BOOT);
            memGpio.setGpioDataHigh(memGpio.GPIO_BGM_BOOT);
        }else{
            memGpio.setGpioOutput(memGpio.GPIO_BGM_BOOT);
            memGpio.setGpioDataLow(memGpio.GPIO_BGM_BOOT);
        }


        memGpio.setGpioOutput(memGpio.GPIO_BGM_ENABLE);
        memGpio.setGpioDataHigh(memGpio.GPIO_BGM_ENABLE);

        memGpio.setGpioOutput(memGpio.GPIO_BGM_CAL);
        if(getBGMOperatingMode()==BGM_MODE_QC){
            memGpio.setGpioDataLow(memGpio.GPIO_BGM_CAL);
        }else{
            memGpio.setGpioDataHigh(memGpio.GPIO_BGM_CAL);
        }

        memGpio.setGpioOutput(memGpio.GPIO_BGM_MKEY);
        memGpio.setGpioDataHigh(memGpio.GPIO_BGM_MKEY);
        if(bgmIntoOn==true) {
            memGpio.eintSetPolarity(memGpio.GPIO_BGM_INT);//@wanc modify
        }
		
        if(uniqueInstance.mSerialPortApi.isAvailableSerialPortBGM()==false){
            if(getBGMOperatingMode()==BGM_MODE_QC){
                mSerialPortApi.openSerialPortBGM(mSerialPortApi.SERIAL_MODE_QC);
            }else if(getBGMOperatingMode()==BGM_MODE_BOOTLOADER){
                mSerialPortApi.openSerialPortBGM(mSerialPortApi.SERIAL_MODE_BOOTLOADER);
            }else{
                mSerialPortApi.openSerialPortBGM(mSerialPortApi.SERIAL_MODE_NORMAL);
            }
        }
        if(uniqueInstance.threadbgm != null){
            uniqueInstance.threadbgm.interrupt();
            uniqueInstance.threadbgm = null;
        }
        if(uniqueInstance.threadbgm==null){
            uniqueInstance.threadbgm_1 = new threadBgm();
            uniqueInstance.threadbgm = new Thread(uniqueInstance.threadbgm_1);
            uniqueInstance.threadbgm.setPriority(Thread.MAX_PRIORITY);
            uniqueInstance.threadbgm.start();
        }
//        else{
//            if(!uniqueInstance.threadbgm.isAlive()){
//                uniqueInstance.threadbgm_1 = new threadBgm();
//                uniqueInstance.threadbgm = new Thread(uniqueInstance.threadbgm_1);
//                uniqueInstance.threadbgm.setPriority(Thread.MAX_PRIORITY);
//                uniqueInstance.threadbgm.start();
//            }
//        }
        if(getBGMOperatingMode()!=BGM_MODE_BOOTLOADER){
            wakeUpBGM();
        }
/*
        if(uniqueInstance.threadbgm==null){
            uniqueInstance.threadbgm = new Thread("threadbgm")  {
                public void run()  {
                    uniqueInstance.listenerBGM();
                }
            };
            uniqueInstance.threadbgm.start();
        }else{
            if(!uniqueInstance.threadbgm.isAlive()){
                uniqueInstance.threadbgm = new Thread("threadbgm") {
                    public void run() {
                        uniqueInstance.listenerBGM();
                    }
                };
                uniqueInstance.threadbgm.start();
            }
        }
*/
    }


    public void disableBGM(boolean intmode) {
        bgmIntoOn=intmode;
        // BGM disable GPIO setting
        bgmisrun=false;
        if(memGpio==null){
            return;
        }

        if(uniqueInstance.threadbgm != null) {
            uniqueInstance.threadbgm.interrupt();
            uniqueInstance.threadbgm = null;
        }
        try {
            if (uniqueInstance.mSerialPortApi.getInputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getInputStreamBGM().close();
            }
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(uniqueInstance.mSerialPortApi!=null){
            uniqueInstance.mSerialPortApi.closeSerialPortBGM();
        }
       // memGpio.gpioInit();
        //memGpio.setGpioInput(memGpio.GPIO_BGM_CAL);
        //memGpio.setGpioInput(memGpio.GPIO_BGM_MKEY);
        //memGpio.setGpioInput(memGpio.GPIO_BGM_ENABLE);
        //memGpio.setGpioInput(memGpio.GPIO_BGM_POWER);
        if(bgmIntoOn==true) {
            memGpio.eintMask(memGpio.GPIO_BGM_INT);//@wanc modify
        }
        memGpio.setGpioOutput(memGpio.GPIO_BGM_POWER);
        memGpio.setGpioDataLow(memGpio.GPIO_BGM_POWER);
        memGpio.setGpioOutput(memGpio.GPIO_BGM_ENABLE);
        memGpio.setGpioDataLow(memGpio.GPIO_BGM_ENABLE);
        memGpio.setGpioOutput(memGpio.GPIO_BGM_CAL);
        memGpio.setGpioDataLow(memGpio.GPIO_BGM_CAL);
        memGpio.setGpioOutput(memGpio.GPIO_BGM_MKEY);
        memGpio.setGpioDataLow(memGpio.GPIO_BGM_MKEY);
        memGpio.setGpioOutput(memGpio.GPIO_TEMPERATURE);
        memGpio.setGpioDataHigh(memGpio.GPIO_TEMPERATURE);
        memGpio.setGpioOutput(memGpio.GPIO_1D_EN);
        memGpio.setGpioDataLow(memGpio.GPIO_1D_EN);
        //memGpio.gpioUnInit();
        mBgmResults.bgmStatus=BGM_STATUS_SLEEP;
        uniqueInstance.stripInfo=STRIP_INFO_NO_STRIP;
        //memGpio.gpioUnInit();
    }


    public boolean sleepBGM(boolean intmode) {
        // BGM sleep GPIO setting
        if(getBGMOperatingMode()==BGM_MODE_BOOTLOADER){
            return false;
        }
        bgmIntoOn=intmode;
        bgmisrun=false;
        if(memGpio==null){
            return false;
        }
        if(uniqueInstance.threadbgm != null) {
            uniqueInstance.threadbgm.interrupt();
            uniqueInstance.threadbgm = null;
        }
        try {
            if (uniqueInstance.mSerialPortApi.getInputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getInputStreamBGM().close();
            }
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(uniqueInstance.mSerialPortApi!=null){
            uniqueInstance.mSerialPortApi.closeSerialPortBGM();
        }
        //memGpio.gpioInit();
        //memGpio.setGpioInput(memGpio.GPIO_BGM_CAL);
        //memGpio.setGpioInput(memGpio.GPIO_BGM_MKEY);
        //memGpio.setGpioInput(memGpio.GPIO_BGM_ENABLE);
        //memGpio.setGpioInput(memGpio.GPIO_BGM_POWER);
        mBgmResults.bgmStatus=BGM_STATUS_SLEEP;
        stripInfo=STRIP_INFO_NO_STRIP;
        if(bgmIntoOn==false) {
            memGpio.setGpioDataLow(memGpio.GPIO_BGM_POWER);
            memGpio.setGpioOutput(memGpio.GPIO_1D_EN);
            memGpio.setGpioDataLow(memGpio.GPIO_1D_EN);
        }
        return true;
    }
    public static int changeByteToUnsignedInt(byte signedvalue){
        int value=0;
        if(signedvalue<0){
            value=(int)signedvalue+256;
        }
        else{
            value=(int)signedvalue;
        }
        return value;
    }
    private void listenerBGM(){
        int length = 0;
        ByteQueue rxQueue = new ByteQueue(6000);

        try {

            while(bgmisrun) {

            //while(!uniqueInstance.threadbgm.isInterrupted()) {
                if (uniqueInstance.mSerialPortApi.getInputStreamBGM() != null) {
                    //DLog.e(TAG, "BGM running");
                    if(uniqueInstance.mSerialPortApi.getInputStreamBGM().available()>0) {
                    byte[] buffer;
                        buffer = new byte[3000];//
                    //int Lenth=mInputStream0.available();
                    length=uniqueInstance.mSerialPortApi.getInputStreamBGM().read(buffer);
                    rxQueue.enQueue(buffer,length);

                    while(rxQueue.IsEmpty()==false) {
                        byte[] buffer_parse = rxQueue.readQueueAll();

                        DLog.e(TAG, "receiveֵΪ" + buffer_parse);
                        //					inputInfo0 = new String(buffer, "utf-8");//
                        needMoreByte=false;
                        protocollenth=0;
                        if(buffer_parse.length>0) {

                            if (parseBGM(buffer_parse, buffer_parse.length) == false) {
                                if (needMoreByte == false) {
                                    mBgmResults.bgmStatus = BGM_STATUS_PARSE_ERROR;
                                }
                            }
                        }
                        if(needMoreByte==true){
                            break;
                        }else{
                            byte[] tempBuffer;
                            tempBuffer=rxQueue.deQueue(protocollenth);
                            if(tempBuffer.length>0) {

                                mBgmResults.bgmString = ParsingString.bytesToHexString(tempBuffer);
                            }
                        }

                        if (bloodGlucoseMonitorCallBack != null) {
                                if((mBgmResults.bgmStatus==BGM_STATUS_NOCODING)||(mBgmResults.bgmStatus==BGM_STATUS_METER_PROBLEM)){
                                    bloodGlucoseMonitorCallBack.bgmcallBackMethod(mBgmResults.bgmString, mBgmResults.bgmStatus, mBgmResults.bgmValue);
                                    bgmPreStatus = mBgmResults.bgmStatus;
                                    mBgmResults.bgmStatus = BGM_STATUS_RAWDATA;
                                }else if (mBgmResults.bgmStatus == BGM_STATUS_RAWDATA){
                                    if(protocolcommand == BGM_CMD_RAW_SEND_START){
                                        bloodGlucoseMonitorCallBack.bgmcallBackMethod(mBgmResults.bgmString, mBgmResults.bgmStatus, mBgmResults.bgmValue);
                                        bgmPreStatus = mBgmResults.bgmStatus;
                                    }else{
                                    }
                                }
                                else if (bgmPreStatus != mBgmResults.bgmStatus) {
                                bloodGlucoseMonitorCallBack.bgmcallBackMethod(mBgmResults.bgmString, mBgmResults.bgmStatus, mBgmResults.bgmValue);
                                if (mBgmResults.bgmStatus == BGM_STATUS_OUT_STRIP) {
                                    mBgmResults.bgmStatus = BGM_STATUS_SLEEP;
                                }else if(mBgmResults.bgmStatus == BGM_STATUS_READ_VERSION){
                                    mBgmResults.bgmStatus = bgmPreStatus;
                                }
                                bgmPreStatus=mBgmResults.bgmStatus;
                            }
                        }

                        DLog.e(TAG, mBgmResults.bgmString);
                    }
                    }else{
                        uniqueInstance.threadbgm.sleep(10);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (uniqueInstance.mSerialPortApi.getInputStreamBGM() != null) {
                    uniqueInstance.mSerialPortApi.getInputStreamBGM().close();
                }
                if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                    uniqueInstance.mSerialPortApi.getOutputStreamBGM().close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void listenerBGM_QC(){
        int length = 0;
        mBgmResults.bgmStatus = BGM_STATUS_QC_MODE_READ;
        ByteQueue rxQueue = new ByteQueue(6000);
        try {
            while(bgmisrun) {
                //while(!uniqueInstance.threadbgm.isInterrupted()) {
                if (uniqueInstance.mSerialPortApi.getInputStreamBGM() != null) {
                    if(uniqueInstance.mSerialPortApi.getInputStreamBGM().available()>0) {
                        byte[] buffer;
                        buffer = new byte[3000];//
                        //int Lenth=mInputStream0.available();
                        length = uniqueInstance.mSerialPortApi.getInputStreamBGM().read(buffer);
                        rxQueue.enQueue(buffer, length);
                        while (rxQueue.IsEmpty() == false) {
                            byte[] buffer_parse = rxQueue.readQueueAll();

                            DLog.e(TAG, "receiveֵΪ" + buffer_parse);
                            //					inputInfo0 = new String(buffer, "utf-8");//
                            needMoreByte = false;
                            protocollenth = 0;
                            if (buffer_parse.length > 0) {

                                if (parseBGM_QC(buffer_parse, buffer_parse.length) == false) {
                                    if (needMoreByte == false) {
                                        mBgmResults.bgmStatus = BGM_STATUS_PARSE_ERROR;
                                    }
                                }
                            }
                            if (needMoreByte == true) {
                                break;
                            } else {
                                byte[] tempBuffer;
                                tempBuffer = rxQueue.deQueue(protocollenth);
                                if (tempBuffer.length > 0) {

                                    mBgmResults.bgmString = ParsingString.bytesToHexString(tempBuffer);
                                }
                            }

                            if (bloodGlucoseMonitorCallBack != null) {
                                bloodGlucoseMonitorCallBack.bgmcallBackMethod(mBgmResults.bgmString, mBgmResults.bgmStatus, mBgmResults.bgmValue);
                                bgmPreStatus=mBgmResults.bgmStatus;
                            }
                            DLog.e(TAG, mBgmResults.bgmString);
                        }
                    }else{
                        uniqueInstance.threadbgm.sleep(100);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (uniqueInstance.mSerialPortApi.getInputStreamBGM() != null) {
                    uniqueInstance.mSerialPortApi.getInputStreamBGM().close();
                }
                if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                    uniqueInstance.mSerialPortApi.getOutputStreamBGM().close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void listenerBGM_BootLoader(){
        int length = 0;
        ByteQueue rxQueue = new ByteQueue(6000);
        mBootLoader.isDownloading=false;
        try {

            while(bgmisrun) {

                //while(!uniqueInstance.threadbgm.isInterrupted()) {
                if (uniqueInstance.mSerialPortApi.getInputStreamBGM() != null) {
                    if(uniqueInstance.mSerialPortApi.getInputStreamBGM().available()>0) {
                        byte[] buffer;
                        buffer = new byte[3000];//
                        //int Lenth=mInputStream0.available();
                        length = uniqueInstance.mSerialPortApi.getInputStreamBGM().read(buffer);
                        rxQueue.enQueue(buffer, length);

                        while (rxQueue.IsEmpty() == false) {
                            byte[] buffer_parse = rxQueue.readQueueAll();

                            DLog.e(TAG, "receiveֵΪ" + buffer_parse);
                            //					inputInfo0 = new String(buffer, "utf-8");//
                            needMoreByte = false;
                            protocollenth = 0;
                            if (buffer_parse.length > 0) {

                                if (parseBGM_BootLoader(buffer_parse, buffer_parse.length) == false) {
                                    if (needMoreByte == false) {
                                        bootNeedAck=false;
                                        protocollenth=buffer_parse.length;
                                        mBgmResults.bgmStatus = BGM_STATUS_PARSE_ERROR;
                                    }
                                }
                            }
                            if (needMoreByte == true) {
                                break;
                            } else {
                                byte[] tempBuffer;
                                bootNeedAck=false;
                                tempBuffer = rxQueue.deQueue(protocollenth);
                                if (tempBuffer.length > 0) {

                                    mBgmResults.bgmString = ParsingString.bytesToHexString(tempBuffer);
                                }
                            }

                            if (bloodGlucoseMonitorCallBack != null) {

                                    //UtilModule.logBgm("--listener_bootloader_string: " + mBgmResults.bgmString);
                                    //UtilModule.logBgm("--listener_bootloader_status: " + mBootLoader.mstatus);
                                    //UtilModule.logBgm("--listener_bootloader_latest_command: " + mBootLoader.mLatestCommand);
                                    if((bootNeedCallback==true)||(mBgmResults.bgmStatus == BGM_STATUS_PARSE_ERROR)){
                                        mBgmResults.bgmStatus = BGM_STATUS_BOOTLOADER;
                                        bloodGlucoseMonitorCallBack.bgmBootLoadercallBackMethod(bootLoaderAck, mBgmResults.bgmStatus, mBootLoader);
                                        bootNeedCallback=false;
                                    }

                            }
                            DLog.e(TAG, mBgmResults.bgmString);
                        }
                    }else{
                        if(bootNeedAck==true){
                            if(bootTimeOutcnt>BOOT_TIME_OUT){
                                bootTimeOutcnt=0;
                                bootNeedAck=false;
                                if (bloodGlucoseMonitorCallBack != null){
                                    mBgmResults.bgmStatus = BGM_STATUS_BOOTLOADER;
                                    DLog.e(TAG, "BGM Boot time out");
                                    bootLoaderAck=BGM_BOOT_ERROR+"time out \r\n";
                                    bloodGlucoseMonitorCallBack.bgmBootLoadercallBackMethod(bootLoaderAck, mBgmResults.bgmStatus, mBootLoader);
                                }
                            }else{
                                bootTimeOutcnt++;
                            }
                        }
                        uniqueInstance.threadbgm.sleep(1);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (uniqueInstance.mSerialPortApi.getInputStreamBGM() != null) {
                    uniqueInstance.mSerialPortApi.getInputStreamBGM().close();
                }
                if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                    uniqueInstance.mSerialPortApi.getOutputStreamBGM().close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean parseBGM(byte[] buffer,int length){

        int etxPointer=0;
        short crc_compare;

        if(buffer[0]!=MODULE_DATA_COMM_STX){
            protocollenth=length;
            return false;
        }
        if(length<4){
            needMoreByte=true;
            return false;
        }
        if(buffer[2]==0){
            protocollenth=length;
            return false;
        }
        etxPointer=5+buffer[2];
        if(length<=etxPointer){
            needMoreByte=true;
            return false;
        }
        protocollenth=6+buffer[2];
        if(buffer[etxPointer]!=MODULE_DATA_COMM_ETX){
            protocollenth=length;
            return false;
        }
        byte[] buffer_crc;
        byte crc_size=(byte)(buffer[2]+2);
        buffer_crc = new byte[crc_size];//
        for (int i = 0; i < crc_size; i++) {
            buffer_crc[i] = buffer[i+1];
        }
        crc_compare=ParsingString.CRC_Calcurate(buffer_crc,crc_size);
        short crc= (short)((((short)buffer[3+buffer[2]]<<8)&((short)0xff00))|((short)buffer[4+buffer[2]]&(short)0x00ff));

        if(crc_compare!=crc){
            protocollenth=length;
            return false;
        }
        byte command=buffer[1];
        protocolcommand=command;
        switch(command){
            case BGM_CMD_METER_PROBLEM:
                mBgmResults.bgmStatus=BGM_STATUS_METER_PROBLEM;
                mBgmResults.bgmValue=buffer[3];
                sendBGMAck();
                break;
            case BGM_CMD_STRIP_OUT:
                mBgmResults.bgmStatus=BGM_STATUS_OUT_STRIP;
                stripInfo=STRIP_INFO_NO_STRIP;
                mBgmResults.bgmValue=0;
                sendBGMAck();
                break;
            case BGM_CMD_STRIP_IN:
                mBgmResults.bgmStatus=BGM_STATUS_INSERT_STRIP;
                mBgmResults.bgmValue=0;
                stripInfo=STRIP_INFO_INSERTED_STRIP;
                sendBGMAck();
                break;
            case BGM_CMD_DROP_BLOOD:{
                mBgmResults.bgmStatus=BGM_STATUS_DROP_BLOOD;
                mBgmResults.bgmValue=buffer[3];
                stripInfo=mBgmResults.bgmValue;
                //sendBGMAck();
            }
                break;
            case BGM_CMD_NOCODING:{
                byte[] value = new byte[2];
                value[0] = buffer[4];
                value[1] = buffer[5];
                mBgmResults.bgmValue = confrimValue(value);
                mBgmResults.bgmValue = (mBgmResults.bgmValue << 8) + changeByteToUnsignedInt(buffer[3]);
                mBgmResults.bgmStatus = BGM_STATUS_NOCODING;
                //sendBGMAck();
            }
            break;
            case BGM_CMD_RAW_SEND:
            case BGM_CMD_RAW_SEND_START:{
                byte[] value = new byte[2];
                value[0] = buffer[3];
                value[1] = buffer[4];
                mBgmResults.bgmValue = confrimValue(value);
                mBgmResults.bgmStatus = BGM_STATUS_RAWDATA;
                //sendBGMAck();
            }
            break;
            case BGM_CMD_PROCESS:
                mBgmResults.bgmStatus=BGM_STATUS_PROCESS_START;
                mBgmResults.bgmValue=buffer[3];
                //sendBGMAck();
                break;
            case BGM_CMD_MEASUREMENT: {
                byte[] value = new byte[2];
                value[0] = buffer[3];
                value[1] = buffer[4];
                mBgmResults.bgmValue = confrimValue(value);
                if (mBgmResults != null) {
                    mBgmResults.bgmGlucoseResult = mBgmResults.bgmValue;
                }
                mBgmResults.bgmStatus = BGM_STATUS_RESULT_GLUCOSE;
                sendBGMAck();
            }
            break;
            case BGM_CMD_TEMPERATURE: {
                byte[] value = new byte[2];
                value[0] = buffer[3];
                value[1] = buffer[4];
                mBgmResults.bgmValue = confrimValue(value);

                if (mBgmResults != null) {
                    mBgmResults.bgmTemperatureResult = mBgmResults.bgmValue;
                }
                mBgmResults.bgmStatus = BGM_STATUS_RESULT_TEMPERATURE;
                sendBGMAck();
            }
            break;
            case BGM_CMD_CURRENT: {
                byte[] value = new byte[2];
                value[0] = buffer[3];
                value[1] = buffer[4];
                mBgmResults.bgmValue = confrimValue(value);

                if (mBgmResults != null) {
                    mBgmResults.bgmCurrentResult = mBgmResults.bgmValue;
                }
                mBgmResults.bgmStatus = BGM_STATUS_RESULT_CURRENT;
                sendBGMAck();
            }
            break;
            case BGM_CMD_CONTROL_SOLUTION: {
                byte[] value = new byte[2];
                value[0] = buffer[3];
                value[1] = buffer[4];
                mBgmResults.bgmValue = confrimValue(value);

                if (mBgmResults != null) {
                    mBgmResults.bgmControlSolutionResult = mBgmResults.bgmValue;
                }
                mBgmResults.bgmStatus = BGM_STATUS_RESULT_CONTROLSOLUTION;
                sendBGMAck();
            }
            break;

            case BGM_CMD_MEASUREMENT_KETONE: {
                byte[] value = new byte[2];
                value[0] = buffer[3];
                value[1] = buffer[4];
                mBgmResults.bgmValue = confrimValue(value);

                if (mBgmResults != null) {
                    mBgmResults.bgmKetoneResult = mBgmResults.bgmValue;
                }
                mBgmResults.bgmStatus = BGM_STATUS_RESULT_KETONE;
                sendBGMAck();
            }
            break;
            case BGM_CMD_CONTROL_SOLUTION_KETONE: {
                byte[] value = new byte[2];
                value[0] = buffer[3];
                value[1] = buffer[4];
                mBgmResults.bgmValue = confrimValue(value);

                if (mBgmResults != null) {
                    mBgmResults.bgmKetoneCSResult = mBgmResults.bgmValue;
                }
                mBgmResults.bgmStatus = BGM_STATUS_RESULT_KETONE_CS;
                sendBGMAck();
            }
            break;
            case BGM_CMD_ERROR:
                mBgmResults.bgmStatus=BGM_STATUS_ERROR;
                mBgmResults.bgmValue=buffer[3]+1;
                if(mBgmResults!=null) {
                    mBgmResults.bgmError = mBgmResults.bgmValue;
                }
                sendBGMAck();
                break;
            case READ_VERSION:
                mBgmResults.bgmStatus=BGM_STATUS_READ_VERSION;
                mBgmResults.bgmValue=0;
                if(mBgmVersion!=null){

                    mBgmVersion.bgmFWversionDigit1 = changeByteToUnsignedInt(buffer[3]);
                    mBgmVersion.bgmFWversionDigit2 = changeByteToUnsignedInt(buffer[4]);
                    mBgmVersion.bgmFWversionDigit3 = changeByteToUnsignedInt(buffer[5]);
                    mBgmVersion.bgmFWversionDigit4 = changeByteToUnsignedInt(buffer[6]);
                    mBgmVersion.bgmHWversion = changeByteToUnsignedInt(buffer[7]);

                    if(buffer[2]==7) {
                        mBgmVersion.bgmCompileDate = changeByteToUnsignedInt(buffer[8]);
                        mBgmVersion.bgmCompileDate = mBgmVersion.bgmCompileDate | ((changeByteToUnsignedInt(buffer[9]) << 8) & 0xff00);
                    }else if(buffer[2]==16){
                        mBgmVersion.bgmCompileDate = changeByteToUnsignedInt(buffer[8]);
                        mBgmVersion.bgmCompileDate = mBgmVersion.bgmCompileDate | ((changeByteToUnsignedInt(buffer[9]) << 8) & 0xff00);
                        mBgmVersion.bgmFlagVersion = changeByteToUnsignedInt(buffer[10]);
                        mBgmVersion.bgmFlagUseStrip1 = changeByteToUnsignedInt(buffer[11]);
                        mBgmVersion.bgmFlagUseStrip2 = changeByteToUnsignedInt(buffer[12]);
                        mBgmVersion.bgmFlagUseStrip3 = changeByteToUnsignedInt(buffer[13]);
                        mBgmVersion.bgmFlagUseStrip4 = changeByteToUnsignedInt(buffer[14]);
                        mBgmVersion.reserved1 = changeByteToUnsignedInt(buffer[15]);
                        mBgmVersion.reserved2 = changeByteToUnsignedInt(buffer[16]);
                        mBgmVersion.reserved3 = changeByteToUnsignedInt(buffer[17]);
                        mBgmVersion.reserved4 = changeByteToUnsignedInt(buffer[18]);
                    }
                }
                break;
            default:
                mBgmResults.bgmValue=0;
                protocollenth=length;
                return false;
        }
        return true;
    }


    private boolean parseBGM_QC(byte[] buffer,int length){
        if(length==0){
            protocollenth=length;
            return false;
        }
        byte command=buffer[0];
        protocolcommand=command;
        switch(command){
            case BGM_CMD_QC_WRITE_FLASH:
            case BGM_CMD_QC_READ_FLASH:
            case BGM_CMD_QC_ECO: {
                if (length < 3) {
                    needMoreByte = true;
                    break;
                }
                if(checkUpperLowerFlag(buffer, 1)==false){
                    protocollenth=length;
                    return false;
                }
                if(command==BGM_CMD_QC_READ_FLASH){
                    mBgmResults.bgmStatus = BGM_STATUS_QC_MODE_READ;
                }else if(command==BGM_CMD_QC_WRITE_FLASH){
                    mBgmResults.bgmStatus = BGM_STATUS_QC_MODE_WRITE;
                }else{
                    mBgmResults.bgmStatus = BGM_STATUS_QC_MODE_ECO;
                }

                byte[] value = new byte[2];
                value[0] = buffer[1];
                value[1] = buffer[2];
                mBgmResults.bgmValue = getQcProtocol1byte(value);
                protocollenth=3;
                }
                break;
            case BGM_CMD_QC_START_MEASURE:
                if(length<3){
                    needMoreByte = true;
                    break;
                }
                if(checkUpperLowerFlag(buffer, 1)==false){
                    protocollenth=length;
                    return false;
                }
                mBgmResults.bgmValue=0;
                protocollenth=3;
                break;
            case BGM_CMD_QC_UARTRESET:
                if(length<3){
                    needMoreByte = true;
                    break;
                }
                if(checkUpperLowerFlag(buffer, 1)==false){
                    protocollenth=length;
                    return false;
                }
                mBgmResults.bgmStatus = BGM_STATUS_QC_MODE_RESET;
                mBgmResults.bgmValue=0;
                protocollenth=3;
                break;
            case BGM_CMD_QC_STX: {
                if (length < 5) {
                    needMoreByte = true;
                    break;
                }
                if (checkPCProtocolHeader(buffer) == false) {
                    protocollenth=length;
                    return false;
                }
                if (length < (buffer[5] + 6)) {
                    needMoreByte = true;
                    break;
                }
                if (checkPCProtocolCommand_RSNB(buffer) == false) {
                    protocollenth=length;
                    return false;
                }
                if (buffer[5+buffer[5]] != BGM_CMD_QC_ETX) {
                    protocollenth=length;
                    return false;
                }
                mBgmResults.bgmStatus = BGM_STATUS_QC_MODE_READ_SN;
                short crc_compare;
                byte[] buffer_crc;
                byte crc_size=(byte)(buffer[5]+4);
                buffer_crc = new byte[crc_size];//
                for (int i = 0; i < (crc_size-1); i++) {
                    buffer_crc[i] = buffer[i];
                }
                buffer_crc[crc_size-1]=buffer[5+buffer[5]];
                crc_compare=ParsingString.CRC_Calcurate(buffer_crc,crc_size);
                short crc= (short)((((short)buffer[3+buffer[5]]<<8)&((short)0xff00))|((short)buffer[4+buffer[5]]&(short)0x00ff));

                if(crc_compare!=crc){
                    protocollenth=length;
                    return false;
                }

                int SNsize=buffer[5]-8;
                byte[] serialnumber= new byte[SNsize];
                for(int i=0;i<SNsize;i++){
                    serialnumber[i] = buffer[11+i];
                }
                try{
                    mBgmVersion.bgmSerialNumber = new String(serialnumber, "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mBgmResults.bgmValue = 0;
                protocollenth = 6+buffer[5];
            }
                break;
            default:
                mBgmResults.bgmValue=0;
                protocollenth=length;
                return false;
        }
        return true;
    }


    private boolean parseBGM_BootLoader(byte[] buffer,int length){

        if(length==0)
            return false;
        if(buffer[0]!=BGM_CMD_BOOT_RSP_ACK)
            return false;
        switch(mBootLoader.mLatestCommand) {
            case BGM_CMD_BOOT_ECO:
                protocollenth = 1;
                bootCommand(BGM_CMD_BOOT_GET, BGM_BOOTSTATUS);
                mBootLoader.msubstatus = 0;
                break;
            case BGM_CMD_BOOT_GET: {
                int size;
                size = buffer[1] + 4;
                if (size > length) {
                    needMoreByte = true;
                    return false;
                }
                if (buffer[size - 1] != BGM_CMD_BOOT_RSP_ACK) {
                    return false;
                }
                mBootLoader.mVersion = buffer[2];
                byte[] commands = new byte[buffer[1]];
                for (int i = 0; i < buffer[1]; i++) {
                    commands[i] = buffer[i + 3];
                }
                mBootLoader.mSuportedCommands = commands;

                bootCommand(BGM_CMD_BOOT_GET_ID, BGM_BOOTSTATUS);
                protocollenth = buffer[1] + 4;
            }
            break;
            case BGM_CMD_BOOT_GET_STATUS:
                break;
            case BGM_CMD_BOOT_GET_ID:
                int size;
                size = buffer[1] + 4;
                if (size > length) {
                    needMoreByte = true;
                    return false;
                }
                if (buffer[size - 1] != BGM_CMD_BOOT_RSP_ACK) {
                    return false;
                }
                mBootLoader.mID = ((int) buffer[2] & 0xff) | (((int) buffer[3] << 8) & 0xff00);
                protocollenth = buffer[1] + 4;
                mBootLoader.currentpage = 0;
                bootCommand(BGM_CMD_BOOT_EXT_ERASE, BGM_BOOTSTATUS_SEND_ADDRESS);
                bootNeedCallback=true;
                bootLoaderAck=BGM_BOOT_ERASE;
                break;
            case BGM_CMD_BOOT_READ_MEMORY:
                switch (mBootLoader.msubstatus) {
                    case BGM_BOOTSTATUS:
                        break;
                    case BGM_BOOTSTATUS_SEND_ADDRESS:
                        bootSendAddress(downloadAddress, BGM_BOOTSTATUS_SEND_NUMBER_BYTE);
                        protocollenth = 1;
                        break;
                    case BGM_BOOTSTATUS_SEND_NUMBER_BYTE: {
                        mBootLoader.mVerifyData = mBootLoader.getData(128);
                        protocollenth = 1;
                        if(mBootLoader.mVerifyData==null){
                            bootNeedCallback=true;
                            bootLoaderAck=BGM_BOOT_ERROR+"get data fail\r\n";
                            break;
                        }
                        mBootLoader.mreceivenumber = mBootLoader.mVerifyData.length;
                        if(downloadAddress==mBootLoader.BOOTLOADER_ADD_OFFSET+0xF00){
                            mBootLoader.mreceivenumber = mBootLoader.mVerifyData.length;
                        }
                        bootSendVerifySize((byte)(mBootLoader.mreceivenumber-1), BGM_BOOTSTATUS_RECEIVE_DATA);
                    }
                        break;
                    case BGM_BOOTSTATUS_RECEIVE_DATA:
                        downloadAddress=mBootLoader.nextAddress;
                        mBootLoader.debugbuffer=buffer;
                        if(buffer.length!=(mBootLoader.mreceivenumber+1)){
                            needMoreByte = true;
                            break;
                        }
                        protocollenth=buffer.length;
                        if(bootVerifyData(buffer,mBootLoader.mVerifyData)==false){
                            bootNeedCallback=true;

                            bootLoaderAck=BGM_BOOT_ERROR+"verify fail \r\n";
                            break;
                        }
                        if(mBootLoader.isFileloading==true) {
                            int percent;
                            percent = (downloadAddress-mBootLoader.BOOTLOADER_ADD_OFFSET)/(mBootLoader.totalSize/300);
                            if(percent>=100){
                                percent=100;
                            }
                            if(downloadPercent!=percent){
                                bootNeedCallback=true;
                                bootLoaderAck=BGM_BOOT_VERIFYING+percent+"\r\n";
                            }
                            bootCommand(BGM_CMD_BOOT_READ_MEMORY, BGM_BOOTSTATUS_SEND_ADDRESS);
                        }else{
                            bootCommand(BGM_CMD_BOOT_GO,BGM_BOOTSTATUS_SEND_ADDRESS);
                            mBootLoader.isFileloading=false;
                        }
                        break;
                }
                break;
            case BGM_CMD_BOOT_GO:
                switch (mBootLoader.msubstatus){
                    case BGM_BOOTSTATUS:
                        protocollenth=1;
                        break;
                    case BGM_BOOTSTATUS_SEND_ADDRESS:
                        bootSendAddress(downloadAddress, BGM_BOOTSTATUS);
                        mBootLoader.isDownloading=false;
                        bootNeedCallback=true;

                        bootLoaderAck=BGM_BOOT_COMPLETE;
                        protocollenth=1;
                        break;
                }
                break;

            case BGM_CMD_BOOT_WRITE_MEMORY:
                switch(mBootLoader.msubstatus) {
                    case BGM_BOOTSTATUS:
                        if(mBootLoader.isFileloading==true) {
                            bootCommand(BGM_CMD_BOOT_WRITE_MEMORY, BGM_BOOTSTATUS_SEND_ADDRESS);
                            protocollenth=1;
                        }else{
                            mBootLoader.isFileloading=false;
                            mBootLoader.isDownloading=false;
                            protocollenth=1;
                            if(mBootLoader.bgmOpenFile()==false){
                                bootNeedCallback=true;
                                bootLoaderAck=BGM_BOOT_ERROR+"file open fail \r\n";
                                break;
                            }
                            bootCommand(BGM_CMD_BOOT_READ_MEMORY,BGM_BOOTSTATUS_SEND_ADDRESS);
                            downloadAddress=mBootLoader.BOOTLOADER_ADD_OFFSET;
                        }
                        break;
                    case BGM_BOOTSTATUS_SEND_ADDRESS: {
                        int percent;
                        percent = (downloadAddress-mBootLoader.BOOTLOADER_ADD_OFFSET)/(mBootLoader.totalSize/300);
                        if(percent>=100){
                            percent=100;
                        }
                        if(downloadPercent!=percent){
                            bootNeedCallback=true;
                            bootLoaderAck=BGM_BOOT_DOWNLOADING+percent+"\r\n";
                        }
                        mBootLoader.FWDownloadProgress=percent;
                        downloadPercent=percent;
                        bootSendAddress(downloadAddress,BGM_BOOTSTATUS_WRITE_DATA);
                        protocollenth=1;
                    }
                    break;
                    case BGM_BOOTSTATUS_WRITE_DATA:{
                        byte[] tempbuffer;
                        protocollenth=1;
                        tempbuffer=mBootLoader.getData(128);
                        if(tempbuffer==null){
                            bootNeedCallback=true;
                            bootLoaderAck=BGM_BOOT_ERROR+"get data fail\r\n";
                            break;
                        }
                        bootSendDownloadData(tempbuffer,BGM_BOOTSTATUS);
                        downloadAddress=mBootLoader.nextAddress;
                        protocollenth=1;
                    }
                        break;

                }
                break;
            case BGM_CMD_BOOT_ERASE:
                break;
            case BGM_CMD_BOOT_EXT_ERASE:
                switch(mBootLoader.msubstatus) {
                    case BGM_BOOTSTATUS:
                        if(mBootLoader.currentpage<mBootLoader.merasesize){
                            bootCommand(BGM_CMD_BOOT_EXT_ERASE,BGM_BOOTSTATUS_SEND_ADDRESS);
                        }else{
                            downloadAddress=mBootLoader.BOOTLOADER_ADD_OFFSET;
                            bootCommand(BGM_CMD_BOOT_WRITE_MEMORY,BGM_BOOTSTATUS_SEND_ADDRESS);
                            bootNeedCallback=true;
                            bootLoaderAck=BGM_BOOT_DOWNLOAD_START;
                            downloadPercent=1000;
                        }
                        protocollenth=1;
                        break;
                    case BGM_BOOTSTATUS_SEND_ADDRESS: {
                        if((mBootLoader.currentpage+10)<=mBootLoader.merasesize){
                            bootSendpage(mBootLoader.currentpage,(mBootLoader.currentpage+10),BGM_BOOTSTATUS);
                            mBootLoader.currentpage+=10;
                        }else{
                            bootSendpage(mBootLoader.currentpage,mBootLoader.merasesize,BGM_BOOTSTATUS);
                            mBootLoader.currentpage=mBootLoader.merasesize;
                        }
                        protocollenth=1;
                        break;
                    }
                }
                break;
            case BGM_CMD_BOOT_WRITE_PROTECT:
                break;
            case BGM_CMD_BOOT_WRITE_UNPROTECT:
                break;
            case BGM_CMD_BOOT_READ_PROTECT:
                break;
            case BGM_CMD_BOOT_READ_UNPROTECT:
                break;
            default:
                mBgmResults.bgmValue=0;
                protocollenth=length;
                return false;
        }
        return true;
    }

    private void readBGMVersion(){
        try {
            byte[] buffer;
            buffer = new byte[12];//
            mBgmResults.bgmReadTemperature=0; //use module's temperature sensor
            buffer[0]=MODULE_DATA_COMM_STX;
            buffer[1]=READ_VERSION;
            buffer[2]=4;
            buffer[3]=0;
            buffer[4]=0;
            buffer[5]=0;
            buffer[6]=0;

            byte[] buffer_crc;
            byte crc_size=(byte)(buffer[2]+2);
            buffer_crc = new byte[crc_size];//
            for (int i = 0; i < crc_size; i++) {
                buffer_crc[i] = buffer[i+1];
            }
            short crc=ParsingString.CRC_Calcurate(buffer_crc,crc_size);
            buffer[3+buffer[2]] = (byte)((crc>>8)&0x00ff);
            buffer[4+buffer[2]] = (byte)((crc)&0x00ff);

            buffer[5+buffer[2]]=MODULE_DATA_COMM_ETX;

            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , 6+buffer[2]);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->BGM Read Version");
            } else {
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBGMAck(){
        try {
            byte[] buffer;
            buffer = new byte[11];//
            mBgmResults.bgmReadTemperature=0; //use module's temperature sensor
            buffer[0]=MODULE_DATA_COMM_STX;
            buffer[1]=BGM_CMD_COM_ACK;
            buffer[2]=4;
            buffer[3]=(byte)(mBgmResults.bgmReadTemperature&0xff);
            buffer[4]=(byte)((mBgmResults.bgmReadTemperature>>8)&0xff);
            //buffer[2]=(byte)0xfa;
            //buffer[3]=0x00;

            if(bgmControlSolutionMode==true){
                buffer[5]=0x01;
            }
            else{
                buffer[5]=0x00;
            }
            buffer[5]=(byte)(rawDataSend|buffer[5]);
            buffer[6]=0;

            byte[] buffer_crc;
            byte crc_size=(byte)(buffer[2]+2);
            buffer_crc = new byte[crc_size];//
            for (int i = 0; i < crc_size; i++) {
                buffer_crc[i] = buffer[i+1];
            }
            short crc=ParsingString.CRC_Calcurate(buffer_crc,crc_size);
            buffer[3+buffer[2]] = (byte)((crc>>8)&0x00ff);
            buffer[4+buffer[2]] = (byte)((crc)&0x00ff);

            buffer[5+buffer[2]]=MODULE_DATA_COMM_ETX;

            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , 6+buffer[2]);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->BGM SendingAck");
            } else {
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendBGMQCEcho(){
        try {
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, String.valueOf(0x80));
            } else {
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setBGMControlSolutionMode(boolean mode) {
            bgmControlSolutionMode = mode;
            sendBGMAck();
    }
    public boolean getBGMControlSolutionMode() {
        return bgmControlSolutionMode;
    }
    public void sendReadBGMVersion() {
        memGpio.setGpioOutput(memGpio.GPIO_BGM_MKEY);
        memGpio.setGpioDataLow(memGpio.GPIO_BGM_MKEY);
        Runnable taskRetry = new Runnable() {
            public void run() {
                memGpio.setGpioOutput(memGpio.GPIO_BGM_MKEY);
                memGpio.setGpioDataHigh(memGpio.GPIO_BGM_MKEY);
                readBGMVersion();
            }
        };
        worker.schedule(taskRetry, 500, TimeUnit.MILLISECONDS);
    }
    public void wakeUpBGM() {
        Runnable taskRetry = new Runnable() {
            public void run() {
                memGpio.setGpioOutput(memGpio.GPIO_BGM_MKEY);
                memGpio.setGpioDataLow(memGpio.GPIO_BGM_MKEY);
                Runnable taskRetry2 = new Runnable() {
                    public void run() {
                        memGpio.setGpioOutput(memGpio.GPIO_BGM_MKEY);
                        memGpio.setGpioDataHigh(memGpio.GPIO_BGM_MKEY);
                    }
                };
                worker.schedule(taskRetry2, 100, TimeUnit.MILLISECONDS);
            }
        };
        worker.schedule(taskRetry, 100, TimeUnit.MILLISECONDS);
    }
    public void resetPowerBGM() {
            memGpio.setGpioOutput(memGpio.GPIO_BGM_POWER);
            memGpio.setGpioDataLow(memGpio.GPIO_BGM_POWER);
            Runnable taskRetry = new Runnable() {
                public void run() {
                    memGpio.setGpioOutput(memGpio.GPIO_BGM_POWER);
                    memGpio.setGpioDataHigh(memGpio.GPIO_BGM_POWER);
                    Runnable taskRetry1 = new Runnable() {
                        public void run() {
                            memGpio.setGpioOutput(memGpio.GPIO_BGM_MKEY);
                            memGpio.setGpioDataLow(memGpio.GPIO_BGM_MKEY);
                            Runnable taskRetry2 = new Runnable() {
                                public void run() {
                                    memGpio.setGpioOutput(memGpio.GPIO_BGM_MKEY);
                                    memGpio.setGpioDataHigh(memGpio.GPIO_BGM_MKEY);
                                }
                            };
                            worker.schedule(taskRetry2, 100, TimeUnit.MILLISECONDS);
                        }
                    };
                    worker.schedule(taskRetry1, 100, TimeUnit.MILLISECONDS);
                }
            };
            worker.schedule(taskRetry, 100, TimeUnit.MILLISECONDS);
    }
    public BgmVersion getBGMVersion() {
        return mBgmVersion;
    }
    public int getBGMOperatingMode() {
        return bgmOperatingMode;
    }
    public int getStripInfomation() {
        return stripInfo;
    }
    public void setBGMOperatingMode(int mode) {
        bgmOperatingMode=mode;
    }

    public int getBGMStatus() {
        return mBgmResults.bgmStatus;
    }
    public BgmResults getLatestBGMResults() {
        return mBgmResults;
    }


    // strip
    // if use all strip : setValidStrip((byte)(STRIP_SET_BAROZEN|STRIP_SET_KETONE_PRO|STRIP_SET_KETONE_PRO|STRIP_SET_KETONE_BAROZEN));
    public void setValidStripForCareSens() {
        setValidStrip((byte)(STRIP_SET_PRO|STRIP_SET_KETONE_PRO));
    }
    public void setValidStripForBAROzen() {
        setValidStrip((byte)(STRIP_SET_BAROZEN|STRIP_SET_KETONE_PRO));
    }
    public void setValidStrip(byte strip){
        validStrip = strip;
    }
    public void setRawDataOption(boolean option){
        if(option==true){
            rawDataSend = RAWDATA_SET;
        }else{
            rawDataSend = 0;
        }
    }
    public boolean getRawDataOption(){
        if(rawDataSend==RAWDATA_SET){
            return true;
        }else{
            return false;
        }
    }
    public byte getValidStrip(){
        return validStrip;
    }
    /*
    class BGMSendingThread extends Thread {

        @Override
        public void run() {
            if(bgmOperatingMode==BGM_NORMAL_MODE){
                sendBGMAck();
            }
            else{
                sendBGMQCEcho();
            }
        }
    }
    */
    //new BGMSendingThread().start();//
    /////////////////////////////// BootLoader Utility //////////////////////////////////////
    public boolean bootStartFWDownload() {
        if(mBootLoader.bgmOpenFile()==false){
            return false;
        }
        try {

            byte[] databuffer;
            databuffer = new byte[1];
            mBootLoader.mLatestCommand=BGM_CMD_BOOT_ECO;
            databuffer[0] = BGM_CMD_BOOT_ECO;
            mBootLoader.msubstatus =0;
            bootNeedAck=true;
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(databuffer, 0, databuffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->BOOT : ECO");
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void bootCommand(byte command,byte status) {
        try {
            byte[] databuffer;
            databuffer = new byte[2];
            mBootLoader.mLatestCommand=command;
            mBootLoader.msubstatus = status;
            databuffer[0] = command;
            databuffer[1] = (byte)((command^(byte)0xff)&(byte)0xff);
            bootNeedAck=true;
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(databuffer, 0, databuffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->BOOT : Command"+command);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bootSendVerifySize(byte size,byte status) {
        try {
            byte[] databuffer;
            databuffer = new byte[2];
            mBootLoader.msubstatus = status;
            databuffer[0] = size;
            databuffer[1] = (byte)((size^(byte)0xff)&(byte)0xff);
            bootNeedAck=true;
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(databuffer, 0, databuffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->BOOT : Verify Size"+size);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bootSendAddress(int address,byte status) {
        try {
            byte[] databuffer;
            databuffer = new byte[5];
            mBootLoader.msubstatus = status;
            databuffer[0] = (byte)((address>>24)&0xff);
            databuffer[1] = (byte)((address>>16)&0xff);
            databuffer[2] = (byte)((address>>8)&0xff);
            databuffer[3] = (byte)((address)&0xff);

            databuffer[4] = 0;

            for(int i=0;i<(databuffer.length-1);i++){
                databuffer[4] = (byte)((databuffer[4]^databuffer[i])&0xff);
            }
            bootNeedAck=true;
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(databuffer, 0, databuffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->BOOT : Send Address"+address);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bootSendpage(int startpage,int endpage,byte status) {
        try {
            byte[] databuffer;
            int size;
            byte checksum=0;
            size =endpage-startpage;
            databuffer = new byte[(size+1)*2+1];
            mBootLoader.msubstatus = status;

            databuffer[0] = (byte)(((size-1)>>8)&0xff);
            databuffer[1] = (byte)((size-1)&0xff);

            checksum=(byte)(checksum^databuffer[0]);
            checksum=(byte)(checksum^databuffer[1]);

            for(int i=0;i <size; i++){
                databuffer[2+i*2]=(byte)(((startpage+i)>>8)&0xff);
                databuffer[2+i*2+1]=(byte)(((startpage+i))&0xff);
                checksum=(byte)(checksum^databuffer[2+i*2]);
                checksum=(byte)(checksum^databuffer[2+i*2+1]);
            }
            databuffer[(size+1)*2] = checksum;
            bootNeedAck=true;
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(databuffer, 0, databuffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->BOOT : Erase Page");
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean bootVerifyData(byte[] data,byte[] verifydata) {

        if(data.length!=(verifydata.length+1)){
            return false;
        }

        for(int i=0; i<verifydata.length;i++){
            if(data[1+i]!=verifydata[i]){
                return false;
            }
        }

        return true;
    }

    public void bootSendDownloadData(byte[] data,byte status) {
        try {
            byte[] databuffer;
            byte checksum=0;
            databuffer = new byte[data.length+2];
            mBootLoader.msubstatus = status;
            databuffer[0] = (byte)(data.length-1);
            checksum=(byte)(checksum^databuffer[0]);
            for(int i=0; i<data.length;i++){
                databuffer[1+i]=data[i];
                checksum=(byte)(checksum^data[i]);
            }
            databuffer[data.length+1]=checksum;
            bootNeedAck=true;
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(databuffer, 0, databuffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->BOOT : Write Data");
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /////////////////////////////// QC Utility //////////////////////////////////////
    private static boolean checkUpperLowerFlag(byte[] buffer,int size) {
        int value = 0;
        for (int i = 0; i < size; i++) {
            if ((buffer[i*2+1] & ((byte) 0xF0)) != (byte) BGM_CMD_QC_UPPER_FLAG) {
                return false;
            }
            if ((buffer[i*2+2] & ((byte) 0xF0)) != (byte) BGM_CMD_QC_LOWER_FLAG) {
                return false;
            }
        }
        return true;
    }
    private static byte removeUpperLowerFlag(byte[] buffer){
        byte bvalue=0;

        bvalue= (byte)((buffer[0]&0x0F)<<4);
        bvalue|= (byte)(buffer[1]&0x0F);
        return bvalue;
    }
    private static byte[] setQcProtocol(byte command,byte[] data,int size){
        byte[] buffer;
        buffer= new byte[size*2+1];
        buffer[0]=command;
        for(int i=0;i<size;i++){
            buffer[i*2+1]=(byte)((((data[i]&(byte)0xF0)>>4)&0x0F)|BGM_CMD_QC_UPPER_FLAG);
            buffer[i*2+2]=(byte)((data[i]&(byte)0x0F)|BGM_CMD_QC_LOWER_FLAG);
        }
        return buffer;
    }

    public void readQCFlash(int address,int size) {
        try {
            byte[] buffer;
            byte[] databuffer;
            databuffer = new byte[3];
            databuffer[1] = (byte) (address & 0xff);
            databuffer[0] = (byte) ((address >> 8) & 0xff);
            databuffer[2] = (byte) (size & 0xff);
            buffer = setQcProtocol(BGM_CMD_QC_READ_FLASH, databuffer, 3);
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(buffer, 0, buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->QC : Read Flash"+address+":"+size);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeQCFlash(int address,int value) {
        try {
            byte[] buffer;
            byte[] databuffer;
            databuffer = new byte[4];
            if((address>=0x1000)&&(address<=0x107F)){
                databuffer[0] = 1;
            }else if((address>=0x1080)&&(address<=0x10FF)){
                databuffer[0] = 0;
            }else{
                return;
            }
            databuffer[1] = (byte) (((address%0x80)/2)&0xff);
            databuffer[3] = (byte) (value & 0xff);
            databuffer[2] = (byte) ((value >> 8) & 0xff);
            buffer = setQcProtocol(BGM_CMD_QC_WRITE_FLASH, databuffer, 4);
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(buffer, 0, buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->QC : Read Flash"+address+":"+value);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uartReset() {
        try {
            byte[] buffer;
            byte[] databuffer;
            databuffer = new byte[1];
            databuffer[0] = 0;
            buffer = setQcProtocol(BGM_CMD_QC_UARTRESET, databuffer, 1);
            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(buffer, 0, buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->QC : Uart Reset");
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void readSerialNumber(){
        try {
            byte[] buffer;
            buffer = new byte[13];

            buffer[0]=BGM_CMD_QC_STX;
            buffer[1]='i';
            buffer[2]='S';
            buffer[3]='P';
            buffer[4]='c';
            buffer[5]=7;
            buffer[6]='R';
            buffer[7]='S';
            buffer[8]='N';
            buffer[9]='B';
            buffer[10]=BGM_CMD_QC_ETX;
            byte[] buffer_crc;
            byte crc_size=(byte)(buffer[5]+4);
            buffer_crc = new byte[crc_size];//
            for (int i = 0; i < (crc_size); i++) {
                buffer_crc[i] = buffer[i];
            }
            short crc=ParsingString.CRC_Calcurate(buffer_crc,crc_size);
            buffer[3+buffer[5]] = (byte)((crc>>8)&0x00ff);
            buffer[4+buffer[5]] = (byte)((crc)&0x00ff);
            buffer[5+buffer[5]]=BGM_CMD_QC_ETX;

            if (uniqueInstance.mSerialPortApi.getOutputStreamBGM() != null) {
                uniqueInstance.mSerialPortApi.getOutputStreamBGM().write(buffer, 0, buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(buffer, 0 , buffer.length);
                //mSerialPortApi.getOutputStreamBGM().write(0x80);
                DLog.e(TAG, "Main->QC : Read Serial Number");
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean checkPCProtocolHeader(byte[] buffer){
        if((buffer[1]!='i')||(buffer[2]!='S')||(buffer[3]!='P')||(buffer[4]!='c')){
            return false;
        }
        return true;
    }
    private boolean checkPCProtocolCommand_RSNB(byte[] buffer){
        if((buffer[6]!='R')||(buffer[7]!='S')||(buffer[8]!='N')||(buffer[9]!='B')){
            return false;
        }
        return true;
    }
    private static int getQcProtocol1byte(byte[] buffer){
        byte bvalue=0;
        bvalue= (byte)((buffer[0]&0x0F)<<4);
        bvalue|= (byte)(buffer[1]&0x0F);
        return bvalue;
    }
    BgmBootLoader getBOOTLoader(){
        return mBootLoader;
    }
    private static int getQcProtocol2byte(byte[] buffer){
        int value=0;
        value= (((int)buffer[0]&0x0F)<<12);
        value|= (((int)buffer[1]&0x0F)<<8);
        value|= (((int)buffer[2]&0x0F)<<4);
        value|= ((int)buffer[3]&0x0F);
        return value;
    }
    private static int confrimValue(byte[] buffer){
        int value=0;
        value=changeByteToUnsignedInt(buffer[0]);
        value=value|(changeByteToUnsignedInt(buffer[1])<<8);
        return value;
    }
}

