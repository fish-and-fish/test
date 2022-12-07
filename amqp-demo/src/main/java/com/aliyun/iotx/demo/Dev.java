package com.aliyun.iotx.demo;

import java.util.HashMap;
import java.util.Map;

import com.aliyun.alink.apiclient.CommonRequest;
import com.aliyun.alink.apiclient.CommonResponse;
import com.aliyun.alink.apiclient.IoTCallback;
import com.aliyun.alink.apiclient.utils.StringUtils;
import com.aliyun.alink.dm.api.DeviceInfo;
import com.aliyun.alink.dm.api.InitResult;
import com.aliyun.alink.dm.model.ResponseModel;
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener;
import com.aliyun.alink.linkkit.api.IoTMqttClientConfig;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linkkit.api.LinkKitInitParams;
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper;
import com.aliyun.alink.linksdk.tmp.listener.IPublishResourceListener;
import com.aliyun.alink.linksdk.tools.AError;
import com.aliyun.alink.linksdk.tools.ALog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Dev {

    private static final String TAG = "tag";

    private static final String pk = "i1d4ovw9QTz";
    private static final String dn = "dev1";
    private static final String ps = "JMafFEL9mSPWcOv2";
    private static String ds = "5f2b4f61362e427910244ce0a0bf1a28";



    public static void main(String[] args) {
        //  #######  一型一密动态注册接口 ######
        /**
         * 注意：动态注册成功，设备上线之后，不能再次执行动态注册。如果设备上线后再次注册，物联网平台返回已注册。
         */
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.productKey = "i1d4ovw9QTz"; //必填
        deviceInfo.deviceName = "dev1"; //必填
        deviceInfo.productSecret = "JMafFEL9mSPWcOv2"; //必填
        LinkKitInitParams params = new LinkKitInitParams();
        IoTMqttClientConfig config = new IoTMqttClientConfig();
        config.productKey = deviceInfo.productKey;
        config.deviceName = deviceInfo.deviceName;
        params.mqttClientConfig = config;
        params.deviceInfo = deviceInfo;
        final CommonRequest request = new CommonRequest();
        request.setPath("/auth/register/device");
        LinkKit.getInstance().deviceRegister(params, request, new IoTCallback() {
            public void onFailure(CommonRequest commonRequest, Exception e) {
                ALog.e(TAG, "动态注册失败 " + e);
            }

            public void onResponse(CommonRequest commonRequest, CommonResponse commonResponse) {
                if (commonResponse == null || StringUtils.isEmptyString(commonResponse.getData())) {
                    ALog.e(TAG, "动态注册失败 response=null");
                    return;
                }
                try {
                    ResponseModel<Map<String, String>> response = new Gson().fromJson(commonResponse.getData(), new TypeToken<ResponseModel<Map<String, String>>>() {
                    }.getType());
                    if (response != null && "200".equals(response.code)) {
                        ALog.d(TAG, "动态注册成功" + (commonResponse == null ? "" : commonResponse.getData()));
                        /**  获取 deviceSecret, 存储到本地，然后执行初始化建联
                         * 该流程仅一次，获取DeviceSecret后，下次启动需要读取本地存储的设备认证信息，
                         * 直接执行初始化建立连接，不可再次动态初始化
                         */
                        ds = response.data.get("deviceSecret");
//                        init(deviceInfo.productKey, deviceInfo.deviceName, deviceSecret);
                        return;
                    }
                } catch (Exception e) {
                }
                ALog.d(TAG, "动态注册失败" + commonResponse.getData());
            }
        });






        //  ####### 一型一密动态注册接口结束  ######

        // 设备上报
        Map<String, ValueWrapper> reportData = new HashMap<>();
        // identifier 是云端定义的属性的唯一标识，valueWrapper是属性的值
        // 以上报整型数据为例，我们构造如下valueWrapper
        // ValueWrapper valueWrapper = new ValueWrapper.IntValueWrapper(1);
        // reportData.put(identifier, valueWrapper);  // 参考示例，更多使用可参考demo
        LinkKit.getInstance().getDeviceThing().thingPropertyPost(reportData, new IPublishResourceListener() {
            public void onSuccess(String s, Object o) {
                // 属性上报成功
            }

            public void onError(String s, AError aError) {
                // 属性上报失败
            }
        });

    }

    public static void f(){
        LinkKitInitParams params = new LinkKitInitParams();
        /**
         * 设置MQTT初始化参数
         */
        IoTMqttClientConfig config = new IoTMqttClientConfig();
        config.productKey = pk;
        config.deviceName = dn;
        config.deviceSecret = ds;
        /*
         *是否接受离线消息
         *对应MQTT的cleanSession字段
         */
        config.receiveOfflineMsg = false;
        params.mqttClientConfig = config;
        /**
         *设置初始化设备认证信息
         */
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.productKey = pk;
        deviceInfo.deviceName = dn;
        deviceInfo.deviceSecret = ds;
        params.deviceInfo = deviceInfo;
        /**
         *设置设备当前的初始状态值，属性需要和物联网平台创建的物模型属性一致
         *如果此处值为空，物模型就无当前设备相关属性的初始值。
         *调用物模型上报接口后，物模型会有相关数据缓存。
         */
        Map propertyValues = new HashMap(); // 示例// propertyValues.put("LightSwitch”, new ValueWrapper.BooleanValueWrapper(0));params.propertyValues = propertyValues;
        LinkKit.getInstance().init(params, new ILinkKitConnectListener() {
            public void onError(AError aError) {
                ALog.e(TAG, "Init Error error = "+aError);
            }
            public void onInitDone(InitResult initResult) {
                ALog.i(TAG, "onInitDone result=" + initResult);
            }
        });
    }
}
