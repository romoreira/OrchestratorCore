package com.mycompany.app;

public class Host extends Fibre {

	private String ID_HOST;
	private String IP_HOST;
	private String IP_HOST_VLAN;
	private String VLAN;
	private String MAC;
	private String connectedToSwitch;
	private String switchPortConnected;

	public String getID_HOST() {
		return ID_HOST;
	}
	public void setID_HOST(String iD_HOST) {
		ID_HOST = iD_HOST;
	}
	public String getIP_HOST() {
		return IP_HOST;
	}
	public void setIP_HOST(String iP_HOST) {
		IP_HOST = iP_HOST;
	}
	public String getIP_HOST_VLAN() {
		return IP_HOST_VLAN;
	}
	public void setIP_HOST_VLAN(String iP_HOST_VLAN) {
		IP_HOST_VLAN = iP_HOST_VLAN;
	}
	public String getVLAN() {
		return VLAN;
	}
	public void setVLAN(String vLAN) {
		VLAN = vLAN;
	}
	public String getMAC() {
		return MAC;
	}
	public void setMAC(String mAC) {
		MAC = mAC;
	}
	public String getConnectedToSwitch() {
		return connectedToSwitch;
	}
	public void setConnectedToSwitch(String connectedToSwitch) {
		this.connectedToSwitch = connectedToSwitch;
	}
	public String getSwitchPortConnected() {
		return switchPortConnected;
	}
	public void setSwitchPortConnected(String switchPortConnected) {
		this.switchPortConnected = switchPortConnected;
	}



}
