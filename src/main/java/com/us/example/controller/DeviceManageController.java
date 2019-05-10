package com.us.example.controller;

import com.us.example.config.ThreadStatusMap;
import com.us.example.domain.ResponseInfo;
import com.us.example.util.ByteUtils;
import com.us.example.util.SendTask;
import com.us.example.util.redis.JedisProxy;
import io.swagger.annotations.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/device")
@Api("设备管理API：DeviceManageController")
public class DeviceManageController {
    @Autowired
    public  ThreadPoolExecutor  threadPool;
    private static final Log logger = LogFactory.getLog(DeviceManageController.class);

    @ApiOperation("启动线程模拟风力终端向服务器发送数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query",name="dtuId",dataType="String",required=true,value="dtu设备id",defaultValue="8dcc01"),
            @ApiImplicitParam(paramType="query",name="speedId",dataType="String",required=true,value="风速设备地址",defaultValue="04"),
            @ApiImplicitParam(paramType="query",name="windSpeed",dataType="String",required=true,value="风速值（m/s）",defaultValue="10"),
            @ApiImplicitParam(paramType="query",name="driId",dataType="String",required=true,value="风向设备地址",defaultValue="03"),
            @ApiImplicitParam(paramType="query",name="windDri",dataType="String",required=true,value="风向值（°）",defaultValue="24"),
            @ApiImplicitParam(paramType="query",name="frequency",dataType="String",required=true,value="发送频次（s）",defaultValue="5"),
            @ApiImplicitParam(paramType="query",name="isRandom",dataType="boolean",required=true,value="是否取随机数",defaultValue="false"),
            @ApiImplicitParam(paramType="query",name="isTCP",dataType="boolean",required=true,value="是否TCP",defaultValue="false"),
            @ApiImplicitParam(paramType="query",name="ip",dataType="String",required=true,value="要发送的ip地址",defaultValue="36.110.98.226"),
            @ApiImplicitParam(paramType="query",name="port",dataType="String",required=true,value="端口",defaultValue="54300")
    })
    @ApiResponses({
            @ApiResponse(code=400,message="请求参数没填好"),
            @ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
    })
    @RequestMapping(value="/startSendDataThread",method= RequestMethod.GET)
    public ResponseInfo<?> startSendDataThread(@RequestParam("dtuId") String dtuId, @RequestParam("speedId") String speedId,@RequestParam("windSpeed") String windSpeed,
                                               @RequestParam("driId") String driId,@RequestParam("windDri") String windDri, @RequestParam("frequency") String frequency,
                                               @RequestParam("isRandom") boolean isRandom,@RequestParam("isTCP") boolean isTCP,@RequestParam("ip") String ip, @RequestParam("port") String port) {
        ResponseInfo response = new ResponseInfo(ResponseInfo.Status.SUCCEED, null);
        System.out.println("设备id"+dtuId);
        System.out.println("风速id"+speedId);
        System.out.println("风速"+windSpeed);
        System.out.println("风向id"+driId);
        System.out.println("风向"+windDri);
        System.out.println("频次"+frequency);
        System.out.println("发送目标ip"+ip);
        System.out.println("发送目标端口"+port);
        windSpeed = (int)(Float.parseFloat(windSpeed)*10)+"";
        if (ThreadStatusMap.map.get(dtuId)!=null&& ThreadStatusMap.map.get(dtuId)){//判断设备发送数据线程是否正在执行，如果是直接返回
            response.setStatus(ResponseInfo.Status.FAILURE.status);
            response.setMessage("dtu设备："+dtuId+"的发送数据线程正在执行中。");
            return response;
        }
        ThreadStatusMap.map.put(dtuId,true);
        ThreadStatusMap.dtuIdDevice.put(dtuId, Arrays.asList(speedId,driId));

        //TODO 启动线程并设置线程状态启动
        threadPool.execute(new SendTask(dtuId,Integer.parseInt(frequency),ip,Integer.parseInt(port),isRandom,isTCP,speedId,windSpeed,driId,windDri){
            //重写实现业务方法把风速风向转成byte数组
            @Override
            public List<byte[]> string2Byte(String[] dataStr) {
                /*拼装数据包：
                * dataStr中包含两个数据dataStr[0],dataStr[1],dataStr[2]和dataStr[3]分别是风速地址，风速值，风向地址和风向值
                * 当dataStr[1]和dataStr[3]的值都是-1的时候，从redis根据dataStr[0]和dataStr[2]的单双数判断是风向还是风速数据，获取相应的随机数，
                * 按照协议拼装数据包,其中第5个字节为设备ID，8，9字节为数据
                * 由于只是个DEMO模拟程序所以直接写下controller里得了。
                * */
                this.isRandom();
                List<byte[]> datas = new ArrayList<byte[]>();
                byte[] dataSpeed = {(byte)0xfe,(byte)0xef,0x00,0x00,0x00,0x03,0x02,0x00,0x00,(byte)0xbb,(byte)0xbb};
                byte[] dataDri = {(byte)0xfe,(byte)0xef,0x00,0x00,0x00,0x03,0x02,0x00,0x00,(byte)0xbb,(byte)0xbb};
                byte[] sId = ByteUtils.str2Bcd(dataStr[0]);
                byte[] dId = ByteUtils.str2Bcd(dataStr[2]);
                dataSpeed[4] = sId[0];
                dataDri[4] = dId[0];

                if(this.isRandom()){
                    //从redis里随机获取
                    int number = new Random().nextInt(10)+1;
;                   String speedStr = JedisProxy.getInstance().createProxy().lindex("speed",number);
                    String driStr = JedisProxy.getInstance().createProxy().lindex("dir",number);

                    byte[] driByte = ByteUtils.int2byte(Integer.parseInt(driStr==null?"0":driStr),2);
                    dataDri[7]= driByte[0];
                    dataDri[8]= driByte[1];
                    byte[] speedByte = ByteUtils.int2byte(Integer.parseInt(speedStr==null?"0":speedStr),2);
                    dataSpeed[7]= speedByte[0];
                    dataSpeed[8]= speedByte[1];
                }else{
                    //通过参数获取
                    byte[] sdata = ByteUtils.int2byte(Integer.parseInt(dataStr[1]),2);
                    byte[] ddata = ByteUtils.int2byte(Integer.parseInt(dataStr[3]),2);
                    dataSpeed[7] = sdata[0];
                    dataSpeed[8] = sdata[1];
                    dataDri[7] = ddata[0];
                    dataDri[8] = ddata[1];
                }
                datas.add(dataSpeed);
                datas.add(dataDri);

                return datas;
            }
        });
        System.out.println("线程池中线程数目："+threadPool.getPoolSize()+"，队列中等待执行的任务数目："+
                threadPool.getQueue().size()+"，已执行玩别的任务数目："+threadPool.getCompletedTaskCount());

        return response;
    }

    @ApiOperation("启动线程模拟贴片温度终端向服务器发送数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query",name="dtuId",dataType="String",required=true,value="设备id",defaultValue="000000100003"),
            @ApiImplicitParam(paramType="query",name="deviceId",dataType="String",required=true,value="设备地址",defaultValue="0103"),
            @ApiImplicitParam(paramType="query",name="node1",dataType="String",required=true,value="节点一温度",defaultValue="03"),
            @ApiImplicitParam(paramType="query",name="node2",dataType="String",required=true,value="节点二温度",defaultValue="10"),
            @ApiImplicitParam(paramType="query",name="node3",dataType="String",required=true,value="节点三温度",defaultValue="18"),
            @ApiImplicitParam(paramType="query",name="node4",dataType="String",required=true,value="节点四温度",defaultValue="26"),
            @ApiImplicitParam(paramType="query",name="node5",dataType="String",required=true,value="节点五温度",defaultValue="85"),
            @ApiImplicitParam(paramType="query",name="node6",dataType="String",required=true,value="节点六温度",defaultValue="0fff"),
            @ApiImplicitParam(paramType="query",name="node7",dataType="String",required=true,value="节点七温度",defaultValue="0fff"),
            @ApiImplicitParam(paramType="query",name="frequency",dataType="String",required=true,value="发送频次（s）",defaultValue="5"),
            @ApiImplicitParam(paramType="query",name="isRandom",dataType="boolean",required=true,value="是否取随机数",defaultValue="false"),
            @ApiImplicitParam(paramType="query",name="isTCP",dataType="boolean",required=true,value="是否TCP",defaultValue="true"),
            @ApiImplicitParam(paramType="query",name="ip",dataType="String",required=true,value="要发送的ip地址",defaultValue="36.110.98.226"),
            @ApiImplicitParam(paramType="query",name="port",dataType="String",required=true,value="端口",defaultValue="53900")
    })
    @ApiResponses({
            @ApiResponse(code=400,message="请求参数没填好"),
            @ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
    })
    @RequestMapping(value="/startSendDataThreadForTempture",method= RequestMethod.GET)
    public ResponseInfo<?> startSendDataThreadForTempture(@RequestParam("dtuId") String dtuId, @RequestParam("deviceId") String deviceId,@RequestParam("node1") String node1,
                                               @RequestParam("node2") String node2,@RequestParam("node3") String node3,@RequestParam("node4") String node4,
                                               @RequestParam("node5") String node5,@RequestParam("node6") String node6,@RequestParam("node7") String node7,@RequestParam("frequency") String frequency,
                                               @RequestParam("isRandom") boolean isRandom,@RequestParam("isTCP") boolean isTCP,@RequestParam("ip") String ip, @RequestParam("port") String port) {
        ResponseInfo response = new ResponseInfo(ResponseInfo.Status.SUCCEED, null);
        System.out.println("设备id"+dtuId);
        System.out.println("频次"+frequency);
        System.out.println("发送目标ip"+ip);
        System.out.println("发送目标端口"+port);
        if(!"0fff".equals(node1))
         node1 = ByteUtils.formatBytes(ByteUtils.int2byte((Integer.parseInt(node1))*10,2));
        if(!"0fff".equals(node2))
            node2 = ByteUtils.formatBytes(ByteUtils.int2byte((Integer.parseInt(node2))*10,2));
        if(!"0fff".equals(node3))
            node3 = ByteUtils.formatBytes(ByteUtils.int2byte((Integer.parseInt(node3))*10,2));
        if(!"0fff".equals(node4))
            node4 = ByteUtils.formatBytes(ByteUtils.int2byte((Integer.parseInt(node4))*10,2));
        if(!"0fff".equals(node5))
            node5 = ByteUtils.formatBytes(ByteUtils.int2byte((Integer.parseInt(node5))*10,2));
        if(!"0fff".equals(node6))
            node6 = ByteUtils.formatBytes(ByteUtils.int2byte((Integer.parseInt(node6))*10,2));
        if(!"0fff".equals(node7))
            node7 = ByteUtils.formatBytes(ByteUtils.int2byte((Integer.parseInt(node7))*10,2));
        if (ThreadStatusMap.map.get(dtuId)!=null&& ThreadStatusMap.map.get(dtuId)){//判断设备发送数据线程是否正在执行，如果是直接返回
            response.setStatus(ResponseInfo.Status.FAILURE.status);
            response.setMessage("dtu设备："+dtuId+"的发送数据线程正在执行中。");
            return response;
        }
        ThreadStatusMap.map.put(dtuId,true);
        ThreadStatusMap.dtuIdDevice.put(dtuId, Arrays.asList(deviceId));
        //TODO 启动线程并设置线程状态启动
        threadPool.execute(new SendTask(dtuId,Integer.parseInt(frequency),ip,Integer.parseInt(port),isRandom,isTCP,node1,node2,node3,node4,node5,node6,node7){
            //重写实现业务方法把风速风向转成byte数组
            @Override
            public List<byte[]> string2Byte(String[] dataStr) {
                /*拼装数据包：
                 * dataStr中包含node1~node7
                 * 由于只是个DEMO模拟程序所以直接写下controller里得了。
                 * */
                this.isRandom();
                List<byte[]> datas = new ArrayList<byte[]>();
                byte[] data = {0x00,0x00,0x00,0x10,0x00,0x03,0x01,0x03,0x0e,0x0f,(byte)0xff,0x0f,
                        (byte)0xff,0x0f,(byte)0xff,0x0f,(byte)0xff,0x0f,(byte)0xff,0x0f,(byte)0xff,
                        0x0f,(byte)0xff,(byte)0xf7,(byte)0x99};
                String d = Arrays.toString(dataStr);
                System.out.println("数据是："+d);
                byte[] nodesBytes = ByteUtils.str2Bcd(d.replace(",","").replace("[","").replace("]","").replace(" ",""));
                System.out.println("数据是："+ByteUtils.formatBytes(nodesBytes)+"length="+nodesBytes.length);
                if(nodesBytes.length==14){
                    System.arraycopy(nodesBytes,0,data,9,14);
                    System.out.println("数据是："+ByteUtils.formatBytes(data));
                    System.out.println("数据是："+ByteUtils.formatBytes(nodesBytes));
                }

                if(this.isRandom()){
                    //从redis里随机获取,暂时不实现
                    int number = new Random().nextInt(10)+1;
                    String speedStr = JedisProxy.getInstance().createProxy().lindex("speed",number);
                    String driStr = JedisProxy.getInstance().createProxy().lindex("dir",number);
                }
                datas.add(data);
                logger.info("************进入业务方法");
                logger.error("************进入业务方法");
                logger.debug("************进入业务方法");
                return datas;
            }
        });
        System.out.println("线程池中线程数目："+threadPool.getPoolSize()+"，队列中等待执行的任务数目："+
                threadPool.getQueue().size()+"，已执行玩别的任务数目："+threadPool.getCompletedTaskCount());

        return response;
    }
    @ApiOperation("停止发送数据线程")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query",name="dtuId",dataType="String",required=true,value="dtu设备id",defaultValue="01")
    })
    @ApiResponses({
            @ApiResponse(code=400,message="请求参数没填好"),
            @ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
    })
    @RequestMapping(value="/stopSendDataThread",method= RequestMethod.GET)
    public ResponseInfo<?> stopSendDataThread(@RequestParam("dtuId") String dtuId) {
        ResponseInfo response = new ResponseInfo(ResponseInfo.Status.SUCCEED, null);
        response.setMessage("关闭"+dtuId+"的数据发送线程");
        ThreadStatusMap.map.put(dtuId,false);
        ThreadStatusMap.dtuIdDevice.remove(dtuId);
        return response;
    }
    @ApiOperation("查看当前线程池情况")

    @ApiResponses({
            @ApiResponse(code=400,message="请求参数没填好"),
            @ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
    })
    @RequestMapping(value="/showSendDataThread",method= RequestMethod.GET)
    public ResponseInfo<?> showSendDataThread() {
        ResponseInfo response = new ResponseInfo(ResponseInfo.Status.SUCCEED, null);

        response.setMessage("线程池中线程数目："+threadPool.getPoolSize()+"，线程池中的活跃线程数："+threadPool.getActiveCount()+",线程队列中的ThreadMap状态："+ ThreadStatusMap.map+"，ditId映射设备Id列表"+ThreadStatusMap.dtuIdDevice+",队列中等待执行的任务数目："+
                threadPool.getQueue().size()+"，已执行玩别的任务数目："+threadPool.getCompletedTaskCount());
        return response;
    }
}
