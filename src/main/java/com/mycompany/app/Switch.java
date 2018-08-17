package com.mycompany.app;

import java.util.ArrayList;

public class Switch extends Fibre{
	
	private String ID_SWITCH;
	private String TYPE;
	private Boolean AVAILABLE;
	private String ROLE;
	private String MFR;
	private String HARDWARE;
	private String SOFTWARE;
	private String SERIAL;
	private String CHASSI_ID;
	private ArrayList<SWPorts> SW_PORTS; 
	
	public Switch(){
		this.setSW_PORTS(new ArrayList<SWPorts>());
	}
	
	public ArrayList<SWPorts> getSW_PORTS() {
		return SW_PORTS;
	}
	public void setSW_PORTS(ArrayList<SWPorts> sW_PORTS) {
		SW_PORTS = sW_PORTS;
	}
	public String getID_SWITCH() {
		return ID_SWITCH;
	}
	public void setID_SWITCH(String iD_SWITCH) {
		ID_SWITCH = iD_SWITCH;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public Boolean getAVAILABLE() {
		return AVAILABLE;
	}
	public void setAVAILABLE(Boolean aVAILABLE) {
		AVAILABLE = aVAILABLE;
	}
	public String getROLE() {
		return ROLE;
	}
	public void setROLE(String rOLE) {
		ROLE = rOLE;
	}
	public String getMFR() {
		return MFR;
	}
	public void setMFR(String mfr) {
		this.MFR = mfr;
	}
	public String getHARDWARE() {
		return HARDWARE;
	}
	public void setHARDWARE(String hARDWARE) {
		HARDWARE = hARDWARE;
	}
	public String getSOFTWARE() {
		return SOFTWARE;
	}
	public void setSOFTWARE(String sOFTWARE) {
		SOFTWARE = sOFTWARE;
	}
	public String getSERIAL() {
		return SERIAL;
	}
	public void setSERIAL(String sERIAL) {
		SERIAL = sERIAL;
	}
	public String getCHASSI_ID() {
		return CHASSI_ID;
	}
	public void setCHASSI_ID(String cHASSI_ID) {
		CHASSI_ID = cHASSI_ID;
	}



}
