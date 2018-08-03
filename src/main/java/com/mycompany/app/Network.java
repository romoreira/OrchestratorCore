package com.mycompany.app;

import java.util.ArrayList;

public class Network extends Fibre{

	public Fibre fibre = new Fibre();

	public Network(){
    	VLAN v1 = new VLAN();
    	v1.setCONTROL_IP("192.168.254.1");
    	v1.setDATA_IP("10.136.12.43");
    	v1.setVLAN_ID("4090");

    	VLAN v2 = new VLAN();
    	v2.setCONTROL_IP("192.168.254.2");
    	v2.setDATA_IP("10.136.12.45");
    	v2.setVLAN_ID("4090");
    	
    	VLAN v3 = new VLAN();
    	v3.setCONTROL_IP("192.168.254.3");
    	v3.setDATA_IP("10.136.12.75");
    	v3.setVLAN_ID("4090");

    	ArrayList<VLAN> mapeamento = new ArrayList<VLAN>();
    	mapeamento.add(v1);
    	mapeamento.add(v2);
    	mapeamento.add(v3);

    	fibre.setVlansListMap(mapeamento);
	}

	public Fibre getFibre() {
		return fibre;
	}

	public void setFibre(Fibre fibre) {
		this.fibre = fibre;
	}
	public void dispatcherDPI(String source, String destination, String method){
		/*
		 * Processo de criacaoo de Intent
		 */
		OnosControllerAgent intentAgent = new OnosControllerAgent();
		intentAgent.setSOURCE(source);
		intentAgent.setDESTINATION(destination);
		intentAgent.setMETHOD(method);
		intentAgent.start();
	}
	public void init(){
		System.out.println("Monitoring Network...");
		fibre.networkSurvey();
    	networkMonitor();
	}
	public void networkMonitor(){
		Intents intents = new Intents();
		Thread intentsThread = new Thread(intents);
		intentsThread.start();
		
		PackageInspector dpi = new PackageInspector();
		Thread threadDPI = new Thread(dpi);
		threadDPI.run();
	}
	public void networkSurvey(){
		fibre.hostDiscover();
    	fibre.switchDiscover();
    	fibre.intentDiscover();
	}
	public String hashHOST_IP(String DATA_IP){
		
		for(int i = 0; i < fibre.getVlansListMap().size(); i ++){
			if(fibre.getVlansListMap().get(i).getDATA_IP().equals(DATA_IP)){
				return fibre.getVlansListMap().get(i).getCONTROL_IP();
			}
		}
		
		return "NotFound";
	}

	public String hashHOST_ID(String CONTROL_IP){
		for(int i = 0; i <fibre.getHostList().size(); i++){
			if(fibre.getHostList().get(i).getIP_HOST_VLAN().equals(CONTROL_IP)){
				return fibre.getHostList().get(i).getID_HOST();
			}
		}
		return "NotFound";
	}
	/*
	 * Aqui � passado a lista de intents, e � verificado se h� intent de source para destination, ou de destination para fonte, retorna um valor boleano
	 */
	public boolean intentAlreadyExists(ArrayList<Intents> intents, String source, String destination){
		String bob = "";
		String alice = "";

		for(int i = 0; i < intents.size(); i++){

			bob = intents.get(i).getPAIRS().get(0).toString();
			alice  = intents.get(i).getPAIRS().get(1).toString();

			if((bob.equals(source) || bob.equals(destination)) && (alice.equals(source) || alice.equals(destination))){
				return true;
			}
			else{
				return false;
			}
		}
		return true;
	}

	public String hashINTENT_ID(ArrayList<Intents> intents, String source, String destination){

		String bob = "";
		String alice = "";

		for(int i = 0; i< intents.size(); i++){
			bob = intents.get(i).getPAIRS().get(0).toString();
			alice = intents.get(i).getPAIRS().get(1).toString();

			if((bob.equals(source) || bob.equals(destination)) && (alice.equals(source) || alice.equals(destination))){
				return intents.get(i).getID().toString();
			}
		}
		return "NotFound";
	}

	public String hashAPP_ID(ArrayList<Intents> intents, String source, String destination){
		String bob = "";
		String alice = "";

		for(int i = 0; i< intents.size(); i++){
			bob = intents.get(i).getPAIRS().get(0).toString();
			alice = intents.get(i).getPAIRS().get(1).toString();

			if((bob.equals(source) || bob.equals(destination)) && (alice.equals(source) || alice.equals(destination))){
				return intents.get(i).getAPP_ID().toString();
			}
		}
		return "NotFound";
	}
}
