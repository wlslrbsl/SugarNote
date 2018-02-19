package com.isens.module.bloodglucosemonitor;

import java.io.File;
import java.io.FileReader;

import static com.isens.module.ParsingString.HexString2Bytes;

/**
 * Created by admin on 2016-12-21.
 */

public class BgmBootLoader {

    public int mVersion = 0;
    public byte[] mSuportedCommands;
    public byte[] mVerifyData;
    public byte[] debugbuffer;
    public int mID = 0;
    public byte mLatestCommand = 0;
    public int msubstatus = 0;
    public int mreceivenumber = 0;
    public int FWDownloadProgress = 0; // FWversion location which is last data address is 0x8039D00
    public int merasesize = 0x39e; // FWversion location which is last data address is 0x8039D00
    public int currentpage = 0; // FWversion location which is last data address is 0x8039D00
    public String FWPath;
    public String FWName;
    public String FWContent;
    public String FWContentSub;
    public String FWCheck=":00000001FF\r\n";
    public byte[] FWContentb;
    public boolean isDownloading=false;
    public File FWFile;
    public FileReader FWFileReader;
    public int currentAddress=0;
    public int nextAddress=0;
    public int startAddress=0;
    public int packetsize=0;
    public int totalSize=0;

    public boolean isFileloading=false;
    public static final int  BOOTLOADER_ADD_OFFSET =	0x8000000;

    public boolean bgmOpenFile(){
        if((FWName==null)||(FWPath==null)){
            return false;
        }
        FWFile = new File(FWPath,FWName);
        try {
            char[] FWReadBuffer;

            FWFileReader = new FileReader(FWFile);
            long size=FWFile.length();
            if(size>1000000){
                return false;
            }
            totalSize=(int)size;
            FWReadBuffer = new char[totalSize];
            if(FWFileReader.read(FWReadBuffer,0,totalSize)==-1){
                return false;
            }

            //File f = mBootLoader.FWFile;
            startAddress = 0;
            nextAddress=0;
            currentAddress=0;
            isFileloading = true;
            isDownloading = true;

            FWContent= new String(FWReadBuffer);
            String check = FWContent.substring(FWContent.length()-13,FWContent.length());
            if(check.equals(FWCheck)){
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void setBGMFW(String path, String fileName){
        FWPath=path;
        FWName=fileName;
    }
    public void resetBGMFW(){
        FWPath=null;
        FWName=null;
    }
    public byte[] deFile(){
        FWContentSub=FWContent.substring(1,FWContent.indexOf("\r\n"));
        FWContent=FWContent.substring(FWContent.indexOf("\r\n")+2,FWContent.length());

        FWContentb=HexString2Bytes(FWContentSub);
        //FWContentb=FWContentSub.getBytes();
        return FWContentb;
    }
    public byte[] readFile(){
        FWContentSub=FWContent.substring(1,FWContent.indexOf("\r\n"));
        FWContentb=HexString2Bytes(FWContentSub);
        //FWContentb=FWContentSub.getBytes();
        return FWContentb;
    }
    public int getNextaddress(){
        return nextAddress;
    }

    public boolean isFileAvailable(){
        if((FWName==null)||(FWPath==null)||(FWFile==null)||(FWContent==null)){
            return false;
        }
        return true;
    }
    public boolean checkIntelChecksum(byte[] data){
        byte checksum=0;
        for(int i=0;i<(data.length-1);i++){
            checksum+=data[i];
        }
        checksum=(byte)((byte)0-checksum);
        if((checksum)==data[data.length-1]){
            return true;
        }else{
            return false;
        }
    }

    public byte[] getData(int size){
        byte[] data = new byte[size];
        byte[] buffer;
        byte[] finaldata;
        int readsize=0;
        int length=0;
        packetsize=0;
        if(isFileAvailable()==false){
            return null;
        }
        while(readsize<size){

            buffer = deFile();
            if (buffer.length < 5) {
                return null;
            }
            if(checkIntelChecksum(buffer)==false){
                return null;
            }
            if(buffer[3] == 0x04) { // setting address
                if(readsize!=0){
                    nextAddress=(((int)buffer[4]<<24)&0xff000000)|(((int)buffer[5]<<16)&0xff0000);
                    buffer=readFile();
                    nextAddress=(nextAddress&0xffff0000)|((((int)buffer[1]<<8)&0xff00)|(((int)buffer[2])&0xff));
                    finaldata = new byte[readsize];
                    packetsize=readsize;
                    for(int i=0;i<readsize;i++){
                        finaldata[i]=data[i];
                    }
                    return finaldata;
                }else{
                    currentAddress=(((int)buffer[4]<<24)&0xff000000)|(((int)buffer[5]<<16)&0xff0000);
                }
            }else if(buffer[3] == 0x00){ // data receive
                int tempsize;
                currentAddress=(currentAddress&0xffff0000)|((((int)buffer[1]<<8)&0xff00)|(((int)buffer[2])&0xff));
                tempsize=buffer[0];
                if((tempsize+5)!=buffer.length){
                    return null;
                }
                for(int i=0;i<tempsize;i++){
                    data[readsize+i]=buffer[4+i];
                }
                nextAddress=currentAddress+tempsize;
                currentAddress=nextAddress-1;
                readsize+=buffer[0];

            }else if(buffer[3] == 0x05){
                startAddress=(((int)buffer[4]<<24)&0xff000000)|(((int)buffer[5]<<16)&0xff0000)|(((int)buffer[6]<<8)&0xff00)|(((int)buffer[7])&0xff);

            }else if(buffer[3] == 0x01){
                isFileloading=false;
                finaldata = new byte[readsize];
                for(int i=0;i<readsize;i++){
                    finaldata[i]=data[i];
                }
                packetsize=readsize;
                return finaldata;

            }else{
                return null;
            }

        }
        packetsize=readsize;
        finaldata = new byte[readsize];
        for(int i=0;i<readsize;i++){
            finaldata[i]=data[i];
        }
        return finaldata;
    }
}
