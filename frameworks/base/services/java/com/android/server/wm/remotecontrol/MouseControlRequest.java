/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Filename:    MouseControlRequest.java  
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2011-12-20 ÏÂÎç05:55:30  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2011-12-20      xwf         1.0         create
*******************************************************************/   


package com.android.server.wm.remotecontrol;

public class MouseControlRequest extends RemoteControlRequest {

	private boolean isAbsolute;
	private int pointCount;
	private int[] pointerIds;
	private float[] mouseX;
	private float[] mouseY;
	private int actionCode;
	private int screenWidth;
	private int screenHeight;
	
	public MouseControlRequest(){
		setControlType(TypeConstants.TYPE_MOUSE);
	}
	
	public MouseControlRequest(UDPPacket packet){
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
		/**
		 * isabsolute 1byte,
		 * pointerCount 2byte
		 * pointerIds (2*Pcount) byte
		 *  x-y (Pcount * 2 * 4)byte
		 *  action 4byte
		 *  screen w-h 8byte
		 */

		byte[] data = new byte[15+pointCount * 10];
		data[0] = isAbsolute ? ((byte)1) : (byte)0;
		byte [] tmp = DataTypesConvert.changeIntToByte(pointCount, 2);
		fillData(data, tmp, 1, 2);
		for (int i = 0; i < pointCount; i++){
			tmp = DataTypesConvert.changeIntToByte(pointerIds[i], 2);
			fillData(data, tmp, 3+10*i, 4+10*i);
			tmp = DataTypesConvert.floatToByte(mouseX[i]);
			fillData(data, tmp, 5+10*i, 8+10*i);
			tmp = DataTypesConvert.floatToByte(mouseY[i]);
			fillData(data, tmp, 9+10*i, 12+10*i);
		}
		int newIndex = 3+10*pointCount;
		tmp = DataTypesConvert.changeIntToByte(actionCode, 4);
		fillData(data, tmp, newIndex, newIndex+3);
		
		//screenWidth
		newIndex = newIndex + 4;
		tmp = DataTypesConvert.changeIntToByte(screenWidth, 4);
		fillData(data, tmp, newIndex, newIndex+3);
		//screenHeight
		newIndex = newIndex + 4;
		tmp = DataTypesConvert.changeIntToByte(screenHeight, 4);
		fillData(data, tmp, newIndex, newIndex+3);
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
		isAbsolute = (data[0]==1?true:false);
		byte[] tmp = fetchData(data, 1, 2);
		pointCount = DataTypesConvert.changeByteToInt(tmp);
		mouseX = new float[pointCount];
		mouseY = new float[pointCount];
		pointerIds = new int[pointCount];
		for (int i = 0; i < pointCount; i++){
//			tmp = fetchData(data, 3+10*i, 4+10*i);
			pointerIds[i] = DataTypesConvert.changeByteToInt(data, 3+10*i, 4+10*i);
			tmp = fetchData(data, 5+10*i, 8+10*i);
			mouseX[i] = DataTypesConvert.byteToFloat(tmp);
			tmp = fetchData(data, 9+10*i, 12+10*i);
			mouseY[i] = DataTypesConvert.byteToFloat(tmp);
		}
		int newIndex = 3+10*pointCount;
		actionCode = DataTypesConvert.changeByteToInt(data, newIndex, newIndex+3);
		newIndex = newIndex+4;
		screenWidth = DataTypesConvert.changeByteToInt(data, newIndex, newIndex+3);
		newIndex = newIndex+4;
		screenHeight = DataTypesConvert.changeByteToInt(data, newIndex, newIndex+3);
		super.decodeData(data);
	}

	public int getPointerCount(){
		return pointCount;
	}
	
	public void setPointerCount(int count){
		pointCount = count;
	}
	
	public int[] getPointerIds(){
		return pointerIds;
	}
	
	public void setPointerIds(int[] pointerIds){
		this.pointerIds = pointerIds;
	}
	
	public float[] getMouseX() {
		return mouseX;
	}

	public void setMouseX(float[] mouseX) {
		this.mouseX = mouseX;
	}

	public int getActionCode() {
		return actionCode;
	}

	public void setActionCode(int actionCode) {
		this.actionCode = actionCode;
	}

	public float[] getMouseY() {
		return mouseY;
	}

	public void setMouseY(float[] mouseY) {
		this.mouseY = mouseY;
	}

	public boolean isAbsolute() {
		return isAbsolute;
	}

	public void setAbsolute(boolean isAbsolute) {
		this.isAbsolute = isAbsolute;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}
}
