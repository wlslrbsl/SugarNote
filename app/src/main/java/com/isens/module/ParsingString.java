package com.isens.module;

/**
 * Created by admin on 2016-06-28.
 */
public class ParsingString {

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
                .byteValue(); //
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
                .byteValue();//
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public static byte[] HexString2Bytes(String src) {
        if(src.length()==0){
            return null;
        }
        if(src.length()<2){
            byte[] ret = new byte[2];
            byte[] tmp2 = src.getBytes();
            byte[] tmp = new byte[src.length()+src.length()%2];
            tmp[0]='0';
            for(int i=src.length()%2;i<(src.length()+src.length()%2);i++){
                tmp[i] = tmp2[i-src.length()%2];
            }

            ret[0]=0;
            ret[1] = uniteBytes(tmp[0], tmp[1]);
            return ret;
        }else{
        byte[] ret = new byte[src.length() / 2];
            byte[] tmp2 = src.getBytes();
            byte[] tmp = new byte[src.length()+src.length()%2];
            tmp[0]='0';
            for(int i=src.length()%2;i<(src.length()+src.length()%2);i++){
                tmp[i] = tmp2[i-src.length()%2];
            }
        for (int i = 0; i < tmp.length / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }
    }

    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    public static short CRC_Calcurate(byte[] DataPacket,byte size)
    {
        short crc = (short)0xffff;

        for(byte index = 0; index <size; index++)
        {
            crc = (short)(((crc >> 8)&0xff) | (short)(crc << 8));
            crc ^= ((short)DataPacket[index]&0x00ff);
            crc ^= ((crc & 0xff) >> 4)&0xff;
            crc ^= (short)((short)(crc << 8) << 4);
            crc ^= (short)((short)((crc & 0xff) << 4) << 1);
        }
        return crc;
    }

}
