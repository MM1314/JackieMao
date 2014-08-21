/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Filename:    GSensorControlRequest.java  
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2011-12-19 下午10:36:50  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2011-12-19      xwf         1.0         create
*******************************************************************/   


package com.android.server.wm.remotecontrol;

public class GSensorControlRequest extends RemoteControlRequest {
	
	private float gx;
	private float gy;
	private float gz;
	private int accuracy;

	public GSensorControlRequest(){
		setControlType(TypeConstants.TYPE_GSENSOR);
	}
	
	public GSensorControlRequest(UDPPacket packet){
		super(packet);
	}
	
	/** 
	 * <p>Title: encodeData</p> 
	 * <p>Description: </p> 
	 * @return 
	 * @see com.android.rockchip.remotecontrol.protocol.RemoteControlRequest#encodeData() 
	 */
	@Override
	protected byte[] encodeData() {
		byte[] data = new byte[16];
		byte[] tmp = DataTypesConvert.floatToByte(gx);
		fillData(data, tmp, 0, 3);
		tmp = DataTypesConvert.floatToByte(gy);
		fillData(data, tmp, 4, 7);
		tmp = DataTypesConvert.floatToByte(gz);
		fillData(data, tmp, 8, 11);
		tmp = DataTypesConvert.changeIntToByte(accuracy, 4);
		fillData(data, tmp, 12, 15);
		return data;
	}

	/** 
	 * <p>Title: decodeData</p> 
	 * <p>Description: </p> 
	 * @param data 
	 * @see com.android.rockchip.remotecontrol.protocol.RemoteControlRequest#decodeData(byte[]) 
	 */
	@Override
	protected void decodeData(byte[] data) {
		byte[] tmp = fetchData(data, 0, 3);
		gx = DataTypesConvert.byteToFloat(tmp);
		tmp = fetchData(data, 4, 7);
		gy = DataTypesConvert.byteToFloat(tmp);
		tmp = fetchData(data, 8, 11);
		gz = DataTypesConvert.byteToFloat(tmp);
		accuracy = DataTypesConvert.changeByteToInt(data, 12, 15);
	}

	public float getGx() {
		return gx;
	}

	public void setGx(float gx) {
		this.gx = gx;
	}

	public float getGy() {
		return gy;
	}

	public void setGy(float gy) {
		this.gy = gy;
	}

	public float getGz() {
		return gz;
	}

	public void setGz(float gz) {
		this.gz = gz;
	}

	public int getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}
}
