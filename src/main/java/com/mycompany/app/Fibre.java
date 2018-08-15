package com.mycompany.app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class Fibre extends App{

	public ArrayList<Host> hostList = null;
	public ArrayList<Switch> swicthList = null;
	public ArrayList<Intents> intentsList = null;
	public ArrayList<VLAN> vlansListMap = null;

	public ArrayList<VLAN> getVlansListMap() {
		return vlansListMap;
	}

	public void setVlansListMap(ArrayList<VLAN> vlansListMap) {
		this.vlansListMap = vlansListMap;
	}

	public ArrayList<Intents> getIntentsList() {
		return intentsList;
	}

	public void setIntentsList(ArrayList<Intents> intentsList) {
		this.intentsList = intentsList;
	}

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

	/*
	 * Aqui irei pegar a lista de hosts em JSON, trabalhar o JSON, e extrair as Strings e armazenar no ArrayList de hosts
	 */
	public void hostDiscover(){
		OnosControllerAgent onosAgent = new OnosControllerAgent();
		JSONProcessor jsonProcessor = new JSONProcessor();

		this.setHostList(jsonProcessor.getHostsList(onosAgent.getHosts()));

		if(this.getHostList() == null){
			System.out.println("No HOSTS found - check the SDN Controller or VLAN\n");
		}

	}

	public void networkSurvey(){
		hostDiscover();
		switchDiscover();
		intentDiscover();
		System.out.println("\nNetwork resources updated.\n");
	}

	/*
	 * Captura a lista de switches em JSON, trabalha o JSON, e extrai as Strings para serem armazenadas no ArrayList de switches;
	 */
	public void switchDiscover(){
		OnosControllerAgent onosAgent = new OnosControllerAgent();
		JSONProcessor jsonProcessor = new JSONProcessor();

		this.setSwicthList(jsonProcessor.getSwitchList(onosAgent.getDevices()));

		if(this.getSwicthList() == null){
			System.out.println("No SWITCHES found - check the SDN Controller\n");
		}
	}
	public void intentDiscover(){
		OnosControllerAgent onosAgent = new OnosControllerAgent();
		JSONProcessor jsonProcessor = new JSONProcessor();

		this.setIntentsList(jsonProcessor.getIntentList(onosAgent.getIntents()));
		if(this.getIntentsList() == null){
			this.setIntentsList(new ArrayList<Intents>());
			//System.out.println("No INTENTS found - check the SDN Controller\n");
		}
	}
}
