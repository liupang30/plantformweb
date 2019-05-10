package com.us.example.util;

import com.us.example.config.ThreadStatusMap;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public abstract  class SendTask implements Runnable{
    private String deviceId;//设备id
    private int frequency;//发送数据频次
    private String ip;
    private int port;
    private boolean isTCP;

    public boolean isRandom() {
        return isRandom;
    }

    private boolean isRandom;
    private String[] dataStr;
    public abstract List<byte[]> string2Byte(String[] dataStr);
    public SendTask(String deviceId,int frequency,String ip,int port,boolean isRandom,boolean isTCP,String...  data){
        this.deviceId = deviceId;
        this.frequency = frequency;
        this.ip = ip;
        this.port = port;
        this.isRandom=isRandom;
        this.isTCP = isTCP;
        dataStr = data;
    }

    @Override
    public void run() {
        InetAddress address = null;
        DatagramSocket udpSocket = null;
        Socket tcpSocket = null;

        try {
            address = InetAddress.getByName(ip);
            List<byte[]> datas = string2Byte(dataStr);
            List<DatagramPacket> dpList= new ArrayList<DatagramPacket>();
            for (byte[]data : datas){
                DatagramPacket packet = new DatagramPacket(data,data.length,address,port);
                dpList.add(packet);
            }
            if(!isTCP){
                udpSocket =new DatagramSocket();
            }
            while(ThreadStatusMap.map.get(deviceId)){//根据设备id获取设备线程状态，用于起停线程
                if(isTCP){
                    //tcp协议
                    tcpSocket = new Socket(ip,port);
                    OutputStream os = tcpSocket.getOutputStream();
                    List<byte[]> dataList = string2Byte(dataStr);
                    for(byte[] newData:dataList){
                        os.write(newData);
                    }
                    os.close();
                    tcpSocket.close();
                }else{
                    //udp协议
                    if(isRandom){
                        List<byte[]> randomDatas = string2Byte(dataStr);
                        for(byte[] newData:randomDatas){
                            udpSocket.send(new DatagramPacket(newData,newData.length,address,port));
                        }
                    }else{
                        //直接发送参数设置的数据
                        if(dpList!=null&&dpList.size()>0){
                            for (DatagramPacket packet : dpList){
                                udpSocket.send(packet);
                            }
                        }
                    }
                }
                Thread.currentThread().sleep(frequency*1000);//设置发送频次
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ThreadStatusMap.map.put(deviceId, false);
            if (udpSocket != null)
                udpSocket.close();
        }
    }
}
