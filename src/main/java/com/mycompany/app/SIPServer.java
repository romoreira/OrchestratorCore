package com.mycompany.app;

import java.util.LinkedList;

public class SIPServer {
	private String INSTANCE_NAME;
	private String DATA_IP_ADRESS;
	private String CONTROL_IP_ADRESS;
	private String INSTANCE_KEY_PAR_NAME;
	private String INSTANCE_STATUS;
	private String INSTANCE_ID;
	private String INSTANCE_PRIORITY;
	
	public String getINSTANCE_PRIORITY() {
		return INSTANCE_PRIORITY;
	}
	public void setINSTANCE_PRIORITY(String iNSTANCE_PRIORITY) {
		INSTANCE_PRIORITY = iNSTANCE_PRIORITY;
	}
	private LinkedList<Double> CPU_HISTORY;

	public LinkedList<Double> getCPU_HISTORY() {
		return CPU_HISTORY;
	}
	public String getINSTANCE_ID() {
		return INSTANCE_ID;
	}
	public void setINSTANCE_ID(String iNSTANCE_ID) {
		INSTANCE_ID = iNSTANCE_ID;
	}
	public void setCPU_HISTORY(LinkedList<Double> cPU_HISTORY) {
		CPU_HISTORY = cPU_HISTORY;
	}
	public String getINSTANCE_NAME() {
		return INSTANCE_NAME;
	}
	public void setINSTANCE_NAME(String iNSTANCE_NAME) {
		INSTANCE_NAME = iNSTANCE_NAME;
	}
	public String getDATA_IP_ADRESS() {
		return DATA_IP_ADRESS;
	}
	public void setDATA_IP_ADRESS(String dATA_IP_ADRESS) {
		DATA_IP_ADRESS = dATA_IP_ADRESS;
	}
	public String getCONTROL_IP_ADRESS() {
		return CONTROL_IP_ADRESS;
	}
	public void setCONTROL_IP_ADRESS(String cONTROL_IP_ADRESS) {
		CONTROL_IP_ADRESS = cONTROL_IP_ADRESS;
	}
	public String getINSTANCE_KEY_PAR_NAME() {
		return INSTANCE_KEY_PAR_NAME;
	}
	public void setINSTANCE_KEY_PAR_NAME(String iNSTANCE_KEY_PAR_NAME) {
		INSTANCE_KEY_PAR_NAME = iNSTANCE_KEY_PAR_NAME;
	}
	public String getINSTANCE_STATUS() {
		return INSTANCE_STATUS;
	}
	public void setINSTANCE_STATUS(String iNSTANCE_STATUS) {
		INSTANCE_STATUS = iNSTANCE_STATUS;
	}
}
