package com.nativecode.serialport;


import android.os.Bundle;
import cordova.plugin.nativeconnector.NativeConnector;
import com.jhxd.serial.serialService;
import android.os.Message;
import android.os.Handler;
import java.io.UnsupportedEncodingException;
import org.apache.cordova.PluginResult ;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class keycardClass {
  private int fd = -1;
  private int readsize = 0;
  public String messege = "";
  private boolean isOpen = false;
  public CallbackContext context;
    public String init(CallbackContext callback){
      context = callback;
      if(isOpen == false){
        if ((fd = serialService.serialOpen("/dev/ttyS4")) < 0) {
          System.out.println( "can't open /dev/ttyS4 port");
          return "can't open /dev/ttyS4 port";
        }else{
          new recDataThread().start();
          isOpen = true;
          System.out.println( "can open /dev/ttyS4 port");
          return "can open /dev/ttyS4 port";
        }
      }else{
        return "";
      }


    }
  class recDataThread extends Thread {
    byte[] readdata = new byte[1024];
    int readlen = 1024;

    public void run() {
      while (fd>0) {
        if (fd < 0) {
          System.out.println("------Close Rece Thread------");
          break;
        }

        try {
          readsize = serialService.serialRead(fd, readdata, readlen);
          if (readsize > 0) {
            System.out.println("------readSize:" + String.valueOf(readsize) + "------");

            byte[] tempBytes = new byte[readsize];
            for (int i = 0; i < tempBytes.length; i++) {
              tempBytes[i] = readdata[i];
            }

            String recvdataString = new String(tempBytes, "GBK");

            System.out.println("------recvData:" + recvdataString + "------");



            Message msg = new Message();
            msg.obj = recvdataString;
            msg.what = 1;
            messege = messege + "" + recvdataString;

          }
          Thread.sleep(100);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if(messege!=null|messege!=""){
          PluginResult result = new PluginResult(PluginResult.Status.OK, messege);
          // PluginResult result = new PluginResult(PluginResult.Status.ERROR, "YOUR_ERROR_MESSAGE");
          result.setKeepCallback(true);
          context.sendPluginResult(result);
        }
 // Keep callback
      }

    };
  };

}
