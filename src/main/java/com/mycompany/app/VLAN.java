package com.mycompany.app;

public class VLAN extends Fibre{
	private String VLAN_ID;
	private String CONTROL_IP;
	private String DATA_IP;

	public void setVLAN_ID(String vLAN_ID) {
		VLAN_ID = vLAN_ID;
	}
	public String getCONTROL_IP() {
		return CONTROL_IP;
	}
	public void setCONTROL_IP(String cONTROL_IP) {
		CONTROL_IP = cONTROL_IP;
	}
	public String getDATA_IP() {
		return DATA_IP;
	}
	public void setDATA_IP(String dATA_IP) {
		DATA_IP = dATA_IP;
	}
}
