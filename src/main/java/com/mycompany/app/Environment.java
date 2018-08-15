package com.mycompany.app;

import java.util.ArrayList;

public class Environment {
	public ArrayList<Host> hostList = null;
	public ArrayList<Switch> swicthList = null;
	
	public ArrayList<Host> getHostList() {
		return hostList;
	}
	public void setHostList(ArrayList<Host> hostList) {
		this.hostList = hostList;
	}
	public ArrayList<Switch> getSwicthList() {
		return swicthList;
	}
	public void setSwicthList(ArrayList<Switch> swicthList) {
		this.swicthList = swicthList;
	}
	
	public void hostDiscover(){
		RyuControllerAgent ryuAgent = new RyuControllerAgent();
		JSONProcessor jsonProcessor = new JSONProcessor();

		this.setHostList(jsonProcessor.getHostsList(ryuAgent.getHosts()));

		if(this.getHostList() == null){
			System.out.println("No HOSTS found - check the SDN Controller or VLAN\n");
		}

	}
	
	public void switchDiscover(){
		RyuControllerAgent ryuAgent = new RyuControllerAgent();
		JSONProcessor jsonProcessor = new JSONProcessor();

		this.setSwicthList(jsonProcessor.getSwitchList(ryuAgent.getDevices()));

		if(this.getSwicthList() == null){
			System.out.println("No SWITCHES found - check the SDN Controller\n");
		}
	}
	
	public void networkSurvey(){
		hostDiscover();
		switchDiscover();
		System.out.println("Network resources updated.\n");
	}
	
}
