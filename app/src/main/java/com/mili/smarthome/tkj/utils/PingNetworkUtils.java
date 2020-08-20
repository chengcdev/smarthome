package com.mili.smarthome.tkj.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 检测是否可以连接到外网的工具类
 */
public class PingNetworkUtils {

    private static final String TAG = "PingNetworkUtils >>> ";
    private static PingNetWorkListener pingNetWorkListener;

    /**
     * ping监听器接口定义
     */
    public interface PingNetWorkListener {
        void onPingState(boolean isConnected);
    }

    /**
     * 设置ping结果反馈监听器
     * @param listener  监听器
     */
    public static void setPingListener(PingNetWorkListener listener) {
        pingNetWorkListener = listener;
    }

    /**
     * 开始ping www.baidu.com
     */
    public static void startPing() {
        Log.d(TAG, " ========= startPing ========== ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (pingNetWorkListener != null) {
                    int state = checkNetworkByHttp();
                    if (state == 2) {
                        boolean ret = checkNetworkByPing();
                        pingNetWorkListener.onPingState(ret);
                    } else if (state == 0){
                        pingNetWorkListener.onPingState(true);
                    } else {
                        pingNetWorkListener.onPingState(false);
                    }
                } else {
                    Log.d(TAG, "[startPing] pingNetWorkListener is null.");
                }
            }
        }).start();
    }

    /**
     * 检测网络是否能连接外网，通过http方式
     * @return  0 连通  1 不连通 2 异常
     */
    public static int checkNetworkByHttp() {
        Log.d(TAG, "start to connect www.baidu.com by http....");
        int isConnected;
        try {
            URL url = new URL("http://www.baidu.com/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if (httpURLConnection == null) {
                Log.d(TAG, "[checkNetworkByHttp] httpURLConnection is null!");
                return 1;
            }
            httpURLConnection.setConnectTimeout(2000);
            httpURLConnection.setReadTimeout(2000);
            InputStream inputStream = httpURLConnection.getInputStream();
            if (inputStream == null) {
                Log.d(TAG, "[checkNetworkByHttp] inputStream is null!");
                return 1;
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            if (bufferedReader.readLine().length() > 1) {
                isConnected = 0;
                Log.d(TAG, "connected baidu by http success!");
            } else {
                isConnected = 1;
                Log.d(TAG, "connected baidu by http failed!");
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            isConnected = 2;
            Log.d(TAG, "connected baidu wlan by http exception!");
            e.printStackTrace();
        }
        return isConnected;
    }

    /**
     * 检测网络是否能连接外网，通过ping方式
     * @return  true/false
     */
    public static boolean checkNetworkByPing() {
        Log.d(TAG, "start to connect www.baidu.com by ping....");
        try {
            // ping 10次 每次超时1秒，阻塞处理
            Process process = Runtime.getRuntime().exec("ping -c 10 -W 1 www.baidu.com");
            if (process == null) {
                Log.d(TAG, " ping process is null.");
                return false;
            }

            // 读取ping的内容
            InputStream input = process.getInputStream();
            if (input == null) {
                Log.d(TAG, " process InputStream is null.");
                return false;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuilder stringBuffer = new StringBuilder();
            String content;
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content).append("\n");
            }
            Log.d(TAG, "ping result : \n" + stringBuffer.toString());

            if (stringBuffer.length() > 0 && !stringBuffer.toString().contains("100% packet loss")) {
                return true;
            }

//            /* ping的状态, 不调用waitFor接口，会导致卡住异常 */
//            int status;
//            try {
//                status = process.waitFor();
//                if (status == 0) {
//                    isConnected = true;
//                    Log.d(TAG, "connected baidu by ping success.");
//                } else {
//                    isConnected = false;
//                    Log.d(TAG, "connected baidu by ping failed.");
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        } catch (IOException e) {
            Log.e(TAG, "connected baidu by ping failed because of IOException.");
            e.printStackTrace();
        }
        return false;
    }
}
