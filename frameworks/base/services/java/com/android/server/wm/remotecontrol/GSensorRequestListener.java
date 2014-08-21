/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Filename:    GSensorRequestListener.java  
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2011-12-20 ÏÂÎç12:07:11  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2011-12-20      xwf         1.0         create
*******************************************************************/   

package com.android.server.wm.remotecontrol;

import java.lang.reflect.Method;

import android.hardware.Sensor;
import android.os.IBinder;
import android.util.Log;


public class GSensorRequestListener implements ControlSocket.RequestListener {
	private void LOG(String msg){
		Log.d("GsensorRequestListener",msg);
	}
//	private Object sensorService;
//	private Object sensorManager;
//	private Method injectSensorEvent;
//	private Method setRemoteSensorEnabled;
	private SensorManagerService sensorManagerService;
	private boolean isEnabled = false;
	
	public GSensorRequestListener(){
//		sensorService = ReflectUtils.invokeStaticMethod("android.os.ServiceManager", "getService", "remotesensor");
//		sensorManager = ReflectUtils.invokeStaticMethod("android.hardware.ISensorManager$Stub", "asInterface", new Class[]{IBinder.class}, sensorService);
//		injectSensorEvent = ReflectUtils.getMethod("android.hardware.ISensorManager", "injectSensorEvent", float[].class, int.class, long.class, int.class); 
//		setRemoteSensorEnabled = ReflectUtils.getMethod("android.hardware.ISensorManager", "setRemoteSensorEnabled", boolean.class);
		sensorManagerService = new SensorManagerService();
	}
	
	/** 
	 * <p>Title: requestRecieved</p> 
	 * <p>Description: </p> 
	 * @param packet 
	 * @see com.android.rockchip.remotecontrol.protocol.ControlSocket.RequestListener#requestRecieved(com.android.rockchip.common.core.udp.UDPPacket) 
	 */
	@Override
	public void requestRecieved(UDPPacket packet) {
		try {
			GSensorControlRequest request = new GSensorControlRequest(packet);
			int controlType = request.getControlType();
			if(TypeConstants.TYPE_GSENSOR_ENABLED == controlType){
				sensorManagerService.setRemoteSensorEnabled(true);
				isEnabled = true;
				LOG("Enable Remote GSensor");
			}else if(TypeConstants.TYPE_GSENSOR_DISABLED == controlType){
				sensorManagerService.setRemoteSensorEnabled(false);
				isEnabled = false;
				LOG("Disable Remote GSensor. ");
			}else if(TypeConstants.TYPE_GSENSOR == controlType){
				
				float x = request.getGx();
				float y = request.getGy();
				float z = request.getGz();
				float[] values = {x, y, z};
//				LOG("gsensor:"+x+","+y+","+z);
				sensorManagerService.injectSensorEvent(values, request.getAccuracy(), System.nanoTime(), Sensor.TYPE_ACCELEROMETER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SensorManagerService getSensorManager(){
		return sensorManagerService;
	}
}
