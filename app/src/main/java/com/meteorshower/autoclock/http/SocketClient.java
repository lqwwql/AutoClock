package com.meteorshower.autoclock.http;

import android.util.Log;

import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.util.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 版权：咸鱼信息科技有限公司 版权所有
 *
 * @author lqw
 * 创建日期：2020/5/30 0:35
 * 描述：
 */
public class SocketClient {

    private PrintWriter printWriter;
    private String cmd;
    private BufferedReader bufferedReader;
    private CmdExcSendListener cmdExcSendListener;

    public SocketClient(String cmd, CmdExcSendListener cmdExcSendListener) {
        this.cmd = cmd;
        this.cmdExcSendListener = cmdExcSendListener;
        connectSocket();
    }

    private void connectSocket() {
        try {
            Log.d(AppConstant.TAG, "connectSocket---------------");
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(AppConstant.SOCKET_URL, AppConstant.SOCKET_PORT));
            socket.setSoTimeout(3000);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new SocketClientThread(socket);
            sendCmd(cmd);
        } catch (Exception e) {
            Log.d(AppConstant.TAG, "connectSocket error :" + Log.getStackTraceString(e));
            cmdExcSendListener.getExcResult("connectSocket error :" + Log.getStackTraceString(e));
            LogUtils.getInstance().e("connectSocket error :" + Log.getStackTraceString(e));
        }
    }

    public void sendCmd(String cmd) {
        printWriter.println(cmd);
        printWriter.flush();
    }

    public interface CmdExcSendListener {
        void getExcResult(String result);
    }

    /**
     * 开个线程发送shell命令
     */
    class SocketClientThread extends Thread {

        private Socket socket;
        private InputStreamReader inputStreamReader;
        private BufferedReader bufferedReader;

        public SocketClientThread(Socket socket) {
            this.socket = socket;
            start();
        }

        @Override
        public void run() {
            try {
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    if (line != null) {
                        cmdExcSendListener.getExcResult(line);
                    }
                }
            } catch (Exception e) {
                Log.d(AppConstant.TAG, "SocketClientThread run error : " + Log.getStackTraceString(e));
                LogUtils.getInstance().e("SocketClientThread run error : " + Log.getStackTraceString(e));
            } finally {
                try {
                    bufferedReader.close();
                    printWriter.close();
                    socket.close();
                } catch (IOException e) {
                    Log.d(AppConstant.TAG, "SocketClientThread run error : " + Log.getStackTraceString(e));
                    LogUtils.getInstance().e("SocketClientThread run close error : " + Log.getStackTraceString(e));
                }
            }
        }
    }
}
