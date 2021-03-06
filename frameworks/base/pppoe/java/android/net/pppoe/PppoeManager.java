/*  
 *  Copyright(C), 2009-2010, Fuzhou Rockchip Co. ,Ltd.  All Rights Reserved.
 *
 *  File:   PppoeManager.java
 *  Desc:   
 *  Usage:
 *  Note:
 *  Author: cz@rock-chips.com
 *  Version:
 *          v1.0
 *  Log:
    ----Thu Sep 8 2011            v1.0
 */
package android.net.pppoe;

import android.annotation.SdkConstant;
import android.annotation.SdkConstant.SdkConstantType;
import android.net.DhcpInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

public class PppoeManager {
    private static final String TAG = "PppoeManager";
    public static final boolean DEBUG = true;
    private static void LOG(String msg) {
        if ( DEBUG ) {
            Log.d(TAG, msg);
        }
    }
    
    /**
     *  Broadcast intent action 
     *      indicating that PPPOE has been enabled, disabled, enabling, disabling, or unknown. 
     *  One extra provides current state as an int.
     *  Another extra provides the previous state, if available.
     * 
     * @see #EXTRA_PPPOE_STATE
     * @see #EXTRA_PREVIOUS_PPPOE_STATE
     */
    @SdkConstant(SdkConstantType.BROADCAST_INTENT_ACTION)
    public static final String PPPOE_STATE_CHANGED_ACTION 
        = "android.net.pppoe.PPPOE_STATE_CHANGED";
    /**
     *  Retrieve it with {@link android.content.Intent#getIntExtra(String,int)}.
     * 
     * @see #PPPOE_STATE_DISABLED
     * @see #PPPOE_STATE_DISABLING
     * @see #PPPOE_STATE_INIT_ENABLED
     * @see #PPPOE_STATE_ENABLING
     * @see #PPPOE_STATE_UNKNOWN
     */
    public static final String EXTRA_PPPOE_STATE = "pppoe_state";
    /**
     * The previous PPPOE state.
     * 
     * @see #EXTRA_PPPOE_STATE
     */
    public static final String EXTRA_PREVIOUS_PPPOE_STATE = "previous_pppoe_state";

    public static final int PPPOE_STATE_CONNECTING = 0;
    public static final int PPPOE_STATE_CONNECTED = 1;
    public static final int PPPOE_STATE_DISCONNECTING = 2;
    public static final int PPPOE_STATE_DISCONNECTED = 3;
    public static final int PPPOE_STATE_UNKNOWN = 4;

    IPppoeManager mService;

    Handler mHandler;
    private PppoeHandler mPppoeHandler;

    public PppoeManager(IPppoeManager service, Handler handler) {
        mService = service;
        mHandler = handler;

        if ( null == mPppoeHandler ) {
            LOG("PppoeManager() : start 'pppoe handle thread'.");
            HandlerThread handleThread = new HandlerThread("Pppoe Handler Thread");
            handleThread.start();
            mPppoeHandler = new PppoeHandler(handleThread.getLooper()/*, this*/);
        }
    }

    private class PppoeHandler extends Handler {
        private static final int COMMAND_START_PPPOE = 1;
        private static final int COMMAND_STOP_PPPOE = 2;

        private Handler mTarget;
        
        public PppoeHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {       
            
            int event;
            LOG("PppoeHandler::handleMessage() : Entered : msg = " + msg);

            switch (msg.what) {
                case COMMAND_START_PPPOE:
                    try {
                        mService.startPppoe();
                    } catch (RemoteException e) {
                        Log.e(TAG, "startPppoe failed");
                    }
                    break;
                case COMMAND_STOP_PPPOE:
                    try {
                        mService.stopPppoe();
                    } catch (RemoteException e) {
                        Log.e(TAG, "stopPppoe failed");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public int getPppoeState() {
        try {
            return mService.getPppoeState();
        } catch (RemoteException e) {
            Log.e(TAG, "stopPppoe failed");
            return -1;
        }
    }

    public boolean startPppoe() {
        return mPppoeHandler.sendEmptyMessage(PppoeHandler.COMMAND_START_PPPOE);
    }

    public boolean stopPppoe() {
        return mPppoeHandler.sendEmptyMessage(PppoeHandler.COMMAND_STOP_PPPOE);
    }

    public boolean setupPppoe(String user, String iface, String dns1, String dns2, String password) {
        try {
            return mService.setupPppoe(user, iface, dns1, dns2, password);
        } catch (RemoteException e) {
            return false;
        }
    }

    public String getPppoePhyIface() {
        try {
            return mService.getPppoePhyIface();
        } catch (RemoteException e) {
            return null;
        }
    }
}

