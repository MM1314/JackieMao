/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Filename:    SensorManagerService.java  
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2011-12-17 ����01:03:04  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2011-12-17      xwf         1.0         create
*******************************************************************/   


package com.android.server.wm.remotecontrol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import android.hardware.ISensorManager;
import android.hardware.SensorParcel;
import android.os.RemoteException;
import android.util.Log;

/**
  * Implement Remote Sensor 
  */
public class SensorManagerService extends ISensorManager.Stub {
	private void LOG(String msg){
		Log.d("SensorManagerService",msg);
	}
	private final static String TAG = "SensorManagerService";
	private boolean isUseRemoteSensor = false;
	private static final int MAX_WAIT_TIME = 3000;
	private static final int MAX_QUEUE_SIZE = 15;
	private int mQueue = 1;
	private Map<Integer, LinkedList<SensorParcel>> sensorDataMap;

	public SensorManagerService(){
		sensorDataMap = new HashMap<Integer, LinkedList<SensorParcel>>();
	}
	
	/**
	 * Enable Remote Sensor 
	 * Disable Remote Sensor
	 */
	public boolean setRemoteSensorEnabled(boolean enable){
		isUseRemoteSensor = enable;
		if(!enable){
			Set<Integer> keySet = sensorDataMap.keySet();
			for(Integer queue : keySet){
				LinkedList<SensorParcel> sensorDataList = sensorDataMap.get(queue);
				synchronized(sensorDataList){
					sensorDataList.notify();
				}
			}
			sensorDataMap.clear();
		}
		Log.d("TAG", "setRemoteSensorEnabled: "+enable);
		return true;
	}

	public boolean getRemoteSensorEnabled(){
		return isUseRemoteSensor;
	}
	
	/**
	 * Create Queue
	 * @return
	 */
	public int createSensorQueue(){
		synchronized(this){
//			LOG("createSensorQueue");
			int queue = mQueue;
			sensorDataMap.put(queue, new LinkedList<SensorParcel>());
			mQueue++;
			Log.d(TAG, "Create one sensor queue, queue id is " + queue);
			return queue;
		}
	}
	
	/**
	 * Destroy Queue
	 * @param queue
	 */
	public void destroySensorQueue(int queue){
		synchronized(this){
			sensorDataMap.remove(queue);
			Log.d(TAG, "Destroy one sensor queue, queue id is " + queue);
		}
	}

	/** 
	 * <p>Title: injectSensorEvent</p> 
	 * <p>Description: </p> 
	 * @param values
	 * @param accuracy
	 * @param timestamp
	 * @param sensorType
	 * @return
	 * @throws RemoteException 
	 * @see android.hardware.ISensorManager#injectSensorEvent(float[], int, long, int) 
	 */
	public boolean injectSensorEvent(float[] values, int accuracy,
			long timestamp, int sensorType) throws RemoteException {
//		LOG("injectSensorEvent");
		SensorParcel sensor = new SensorParcel();
		sensor.values = values;
		sensor.accuracy = accuracy;
		sensor.timestamp = timestamp;
		sensor.sensorType = sensorType;
		Set<Integer> keySet = sensorDataMap.keySet();
		for(Integer queue : keySet){
			LinkedList<SensorParcel> sensorDataList = sensorDataMap.get(queue);
			synchronized(sensorDataList){
				sensorDataList.add(sensor);
				if(sensorDataList.size()>MAX_QUEUE_SIZE){
					sensor = sensorDataList.poll();
				}
				sensorDataList.notify();
			}
		}
		return true;
	}

	/** 
	 * <p>Title: obtainSensorEvent</p> 
	 * <p>Description: </p> 
	 * @return
	 * @throws RemoteException 
	 * @see android.hardware.ISensorManager#obtainSensorEvent() 
	 */
	public SensorParcel obtainSensorEvent(int queue) throws RemoteException {
//		LOG("obtainSensorEvent");
		LinkedList<SensorParcel> sensorDataList = sensorDataMap.get(queue);
		if(sensorDataList==null){
			if(queue>0)
				sensorDataMap.put(queue, new LinkedList<SensorParcel>());
			return null;	
		}
		
		SensorParcel returnSensor = null;
		synchronized(sensorDataList){
			if(sensorDataList.isEmpty()){
				try{
					sensorDataList.wait(10000);
				}catch(Exception ex){
				}
			}
			returnSensor = sensorDataList.poll();
		}
		return returnSensor;
	}
}
