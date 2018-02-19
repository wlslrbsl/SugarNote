package com.isens.module;


/**
 * Created by admin on 2016-11-17.
 */

public class ByteQueue {
    //Queue bufffer;
    private byte mQArray[] = null;
    private int mQSize;
    private int mQHeader;
    private int mQTail;
    //private int mCount;
    public ByteQueue(int size){
        mQSize = size;
        mQArray = new byte[size];
        mQHeader = 0;
        mQTail = 0;
        //mCount = 0;
        //bufffer = new Queue();
    }

    public void Initial(){
        if(mQArray!=null){
            for(int i = 0; i<mQSize;i++){
                mQArray[i]=0;
            }
        }
        mQHeader = 0;
        mQTail = 0;
        //mCount = 0;
    }

    public boolean IsQFull(){
        if(mQTail==0){
            if(mQHeader==(mQSize-1)){
                return true;
            }else{
                return false;
            }
        }else{
            if(mQHeader==(mQTail-1)){
                return true;
            }else{
                return false;
            }
        }
    }

    public boolean IsEmpty(){
        if(mQHeader == mQTail)
            return true;
        else
            return false;

    }

    public int getRxSize(){
        int size;
        if(mQHeader>=mQTail){
            size=mQHeader-mQTail;
        }else{
            size=mQHeader+mQSize-mQTail;
        }
        return size;
    }
    public int getSize(){
        return mQSize;
    }

    public boolean enQueue (byte rxBuffer)
    {
        if(IsQFull()==true){
            return false;
        }
        mQArray[mQHeader] = rxBuffer;

        mQHeader++;
        if(mQHeader == mQSize){
            mQHeader = 0;
        }
        //mCount++;
        return true;
    }
    public boolean enQueue (byte[] rxBuffer){
        if(IsQFull()==true){
            return false;
        }
        if(rxBuffer.length+getRxSize()>mQSize){
            return false;
        }
        for(int i=0; i<rxBuffer.length;i++){
            enQueue(rxBuffer[i]);
            /*
            mQArray[mQHeader++] = rxBuffer[i];
            if(mQHeader == mQSize){
                mQHeader = 0;
            }
            mCount++;
            */
        }
        return true;
    }
    public boolean enQueue (byte[] rxBuffer,int size){
        if(IsQFull()==true){
            return false;
        }
        if(size+getRxSize()>mQSize){
            return false;
        }
        if(rxBuffer.length<size){
            return false;
        }
        for(int i=0; i<size;i++){
            enQueue(rxBuffer[i]);
            /*
            mQArray[mQHeader++] = rxBuffer[i];
            if(mQHeader == mQSize){
                mQHeader = 0;
            }
            mCount++;
            */
        }
        return true;
    }

    public byte deQueue(){
        byte rxData = 0;
        if(IsEmpty()==true){
            return 0;
        }
        rxData = mQArray[mQTail];
        mQTail++;
        if(mQTail == mQSize){
            mQTail = 0;
        }
        //mCount--;

        return rxData;
    }

    public byte[] deQueue(int size){
        byte rxData[];
        if(size>getRxSize()){
            return null;
        }
        rxData = new byte[size];
        for(int i=0; i<rxData.length;i++) {
            rxData[i]=deQueue();
        }

        return rxData;
    }

    public byte[] deQueueAll(){
        byte rxData[];
        if(getRxSize()<=0){
            return null;
        }
        rxData = deQueue(getRxSize());
        return rxData;
    }
    public byte readQueue(){
        byte rxData = 0;
        if(IsEmpty()==true){
            return 0;
        }
        rxData = mQArray[mQTail];
        return rxData;
    }
    public byte[] readQueue(int size){
        byte rxData[];
        int currentpoint=mQTail;
        if(size>getRxSize()){
            return null;
        }
        rxData = new byte[size];
        for(int i=0; i<rxData.length;i++) {
            rxData[i]=mQArray[currentpoint++];
            if(currentpoint==mQSize){
                currentpoint=0;
            }
        }
        return rxData;
    }
    public byte[] readQueueAll(){
        byte rxData[];
        if(getRxSize()<=0){
            return null;
        }
        rxData = readQueue(getRxSize());
        return rxData;
    }
}
