package com.us.example.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

public class ByteUtils {
	private static Logger logger = Logger.getLogger(ByteUtils.class);
	public static long byte2long(byte[] bytes) {
		long x = 0;

		for (int i = 0; i < bytes.length; i++) {
			x = x << 8;
			x += bytes[i]&0xff;
		}
		return x;
	}
	
	
	public static long byte2long(byte[] data,int offset,int length){
		long x = 0;

		for (int i = 0; i < length; i++) {
			x = x << 8;
			x += data[offset+i]&0xff;
		}
		return x;
	}

	

	public static int byte2int(byte[] bytes) {
		int x = 0;

		for (int i = 0; i < bytes.length; i++) {
			x = x << 8;
			x += bytes[i]&0xff;
		}
		return x;
	}
	

	public static int byte2int(byte[] data,int offset,int length) {
		int x = 0;

		for (int i = 0; i < length; i++) {
			x = x << 8;
			x += data[offset+i]&0xff;
		}
		return x;
	}

	public static byte[] long2byte(long l, int byteLength) {
		byte[] bytes = new byte[byteLength];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) ((l >> (8 * (bytes.length - 1 - i))) & 0xff);
		}
		return bytes;
	}

	public static byte[] long2byte(long l) {
		return long2byte(l, 8);
	}

	public static byte[] int2byte(int l, int byteLength) {
		byte[] bytes = new byte[byteLength];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) ((l >> (8 * (bytes.length - 1 - i))) & 0xff);
		}
		return bytes;
	}

	public static byte[] int2byte(int l) {
		return int2byte(l, 4);
	}

	public static float byte2float(byte[] bytes) {

		return ByteBuffer.wrap(bytes).getFloat();
	}
	

	public static float byte2float(byte[] bytes,int offset,int length) {

		return ByteBuffer.wrap(bytes,offset,length).getFloat();
	}

	public static byte[] float2byte(float fl) {
		return ByteBuffer.allocate(4).putFloat(fl).array();
	}
	public static String format(byte[] data) {
        StringBuilder builder = new StringBuilder();
        int n = 0;
        for(byte b: data) {
            if (n %16 == 0) {
                builder.append(String.format("%05x: ", n));
            }
            builder.append(String.format("%02x ", b));
            n ++;
            if (n % 16 == 0) {
                builder.append("\n");
            }
        }
        builder.append("\n");
        return builder.toString();
    }
	public static String formatBytes(byte[] data) {
        StringBuilder builder = new StringBuilder();
        int n = 0;
        for(byte b: data) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
	public static void main(String[] args) {
		System.out.println(byte2float(float2byte(3.14159266f)));
		System.out.println(byte2long(long2byte(5688787l)));
		System.out.println(byte2int(long2byte(56887871l)));

		Date now = new Date();
		SimpleDateFormat f=new SimpleDateFormat("yyMMddHHmmss");
		System.out.println(f.format(now));
		byte[] aa= {0x01,0x03, 0x00,0x00, 0x00, 0x01};
		byte[] result = CRC16_MODBUS_back(aa);
		System.out.println(formatBytes(result));
		
		System.out.println(formatBytes(aa));
	}
	 public static int CRC_XModem(byte[] bytes){  
	        int crc = 0x00;          // initial value  
	        int polynomial = 0x1021;    
	        for (int index = 0 ; index< bytes.length; index++) {  
	            byte b = bytes[index];  
	            for (int i = 0; i < 8; i++) {  
	                boolean bit = ((b   >> (7-i) & 1) == 1);  
	                boolean c15 = ((crc >> 15    & 1) == 1);  
	                crc <<= 1;  
	                if (c15 ^ bit) crc ^= polynomial;  
	             }  
	        }  
	        crc &= 0xffff;  
	        return crc;     
	}
	 public static byte[] int2byte_Little(int l, int byteLength) {
		byte[] bytes = new byte[byteLength];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) ((l >> (8 * i)) & 0xff);
		}
		return bytes;
	}
    public static int byte2int_Little(byte[] bytes) {
		int x = 0;

		for (int i = 0; i < bytes.length; i++) {
			x = x << 8;
			x += bytes[bytes.length-1-i]&0xff;
		}
		return x;
	}
	public static int getCRC(byte[] bytes) {
		int CRC = 0x0000ffff;
		int POLYNOMIAL = 0x0000a001;
		int i, j;
		for (i = 0; i < bytes.length; i++) {
			CRC ^= ((int) bytes[i] & 0x000000ff);
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00000001) != 0) {
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC >>= 1;
				}
			}
		}
		return CRC&0XFF;
	}
	
	public static byte[] byte2Byte(byte[] bytes){
		byte[] byte2 = null ;
		if(bytes.length > 0 && bytes.length%2 == 0){
			byte2 = new byte[bytes.length/2];
			for(int i = 0;i < bytes.length-1;i+=2){
				byte2[i/2] = (byte) (Character.digit(bytes[i],16) * 16+ Character.digit(bytes[i+1],16));
			}
		}
		return byte2;
	}
	public static byte[] byteMergerAll(byte[]... values) {
	    int length_byte = 0;
	        for (int i = 0; i < values.length; i++) {
	            length_byte += values[i].length;
	        }
	        byte[] all_byte = new byte[length_byte];
	        int countLength = 0;
	        for (int i = 0; i < values.length; i++) {
	            byte[] b = values[i];
	            System.arraycopy(b, 0, all_byte, countLength, b.length);
	            countLength += b.length;
	        }
	        return all_byte;
	}
	public static int GetIndexOf(byte[] b, byte[] bb){
        if (b == null || bb == null || b.length == 0 || bb.length == 0 || b.length<bb.length){
            return -1;
        }
        int i, j;
        for (i = 0; i < b.length - bb.length + 1; i++){
            if (b[i] == bb[0]){
                for (j = 1; j < bb.length; j++){
                    if (b[i + j] != bb[j])
                        break;
                }
                if (j == bb.length)
                    return i;
            }
        }
        return -1;
    }
	public static byte[] byteReplace(byte[] data,byte[] oldByte , byte[] newByte){
		if(GetIndexOf(data,oldByte)!=-1){
			byte[] activeData =  Arrays.copyOfRange(data,0,GetIndexOf(data,oldByte));
			byte[] backData =  Arrays.copyOfRange(data,GetIndexOf(data,oldByte)+oldByte.length,data.length);
			data = byteReplace(byteMergerAll(activeData,newByte,backData),oldByte,newByte);
		}
		return data;
	}
	//序列化 
    public static byte [] serialize(Object obj){
        ObjectOutputStream obi=null;
        ByteArrayOutputStream bai=null;
        try {
            bai=new ByteArrayOutputStream();
            obi=new ObjectOutputStream(bai);
            obi.writeObject(obj);
            byte[] byt=bai.toByteArray();
            return byt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //反序列化
    public static Object unserizlize(byte[] byt){
        ObjectInputStream oii=null;
        ByteArrayInputStream bis=null;
        bis=new ByteArrayInputStream(byt);
        try {
            oii=new ObjectInputStream(bis);
            Object obj=oii.readObject();
            return obj;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    
        
        return null;
    }

    //飞拓信达crc
    public static byte[] FTXDCRC(byte[] data){
		int crc = 0;
		for (int i = 0; i < data.length; i++) {
			crc +=data[i+2];
			if (i+2 == data.length-1) {
				break;
			}
		}
		byte[] crcBytes = ByteUtils.int2byte(crc, 2);
		
		return crcBytes;
    }
    public static byte HWCRC(byte[] data){
    	byte tb = 0x00;
    	for(int i=0;i<data.length;i++){
			 tb+=data[i];
		}
    	return tb;
    }
    public static byte[] CRC16_MODBUS(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return ByteUtils.int2byte(CRC,2);
    }
    public static byte[] CRC16_MODBUS_back(byte[] bytes) {
    	byte[] crc = CRC16_MODBUS(bytes);
    	byte[] crc2 = new byte[2];
    	crc2[0]=crc[1];crc2[1]=crc[0];
    	return crc2;
    }
	public static byte[] str2Bcd(String asc) {
		int len = asc.length();
		int mod = len % 2;

		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;

		for (int p = 0; p < asc.length()/2; p++) {
			if ( (abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ( (abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ( (abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ( (abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			}else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}
}
