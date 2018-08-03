package com.mycompany.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
public class SIPLoadBalancer implements Runnable {
	
	private String destination = "";
	private int priority;
	
	
	
	public String getDestination() {
		return destination;
	}



	public void setDestination(String destination) {
		this.destination = destination;
	}



	public int prioritySetting(String method, String serverName) {
		System.out.println("prioritySetting");
		OpenStack os = new OpenStack();
		os.computeSurvey();
		if(method.equals("INSERT")){
			int serverPriority;
			for(int i = 0; i < os.sipServersList.size(); i++){
				if(!os.getSipServersList().get(i).getINSTANCE_NAME().equals(serverName)){
					serverPriority = selectServerPriority(os.sipServersList.get(i).getINSTANCE_NAME());
				
					/*
					 *Aqui é atualizado no Kamailio DB a nova entrada de balanceamento com prioridade desloaca - isto é, para o novo SIPSerer que entrar ele poderá entrar com a menor prioridade; 
					 */
					update(os.sipServersList.get(i).getINSTANCE_NAME(), serverPriority+1);
				
				}
			}
			return 0;
		}
		else{
			if(method.equals("DELETE")){
				int serverPriority;
				for(int i = 0; i < os.sipServersList.size(); i++){
					if(!os.sipServersList.get(i).getINSTANCE_NAME().equals(serverName)){
						serverPriority = selectServerPriority(os.sipServersList.get(i).getINSTANCE_NAME());
						if(serverPriority >= 1){
							update(os.sipServersList.get(i).getINSTANCE_NAME(), serverPriority - 1);
						}
						else{
							if(serverPriority == 0){
								update(os.sipServersList.get(i).getINSTANCE_NAME(), 0);
							}
							
						}
					}
				}
			}
		}
		return -1;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean kamailioReloadConfigs(){
		//System.out.print("Into reload Configs");
		try{
			String pythonScriptPath = "/home/rodrigo/workspace/OrchestratorCore/LoadBalancerUpdate.py";
			String[] cmd = new String[2];
			cmd[0] = "python"; // check version of installed python: python -V
			cmd[1] = pythonScriptPath;
			 
			// create runtime to execute external command
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(cmd);
			 
			// retrieve output from python script
			BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while((line = bfr.readLine()) != null) {
			// display each output line form python script
				System.out.println(line);
				if(line.contains("Updated")){
					return true;
				}
				else{
					return false;
				}
			}
		}catch(Exception e){
			System.out.println("Erro to execute: 'kamctl dispatcher reload'");
		}
		return false;
	}

	public boolean update(String serverName, int priority){
		MySQLAccess mysql = new MySQLAccess();
		return mysql.updateServerPriority(serverName, priority);
	}

	public int selectServerPriority(String serverName){
		MySQLAccess mysql = new MySQLAccess();
		return mysql.selectServerPriority(serverName);
	}
	
	public static boolean delete(String serverName){
		if(serverName.equals("KSR1")){
			ComputeMonitor.RUNNING_DELETE = false;
			System.out.println("Can not DELETE  Main VNF!");
			return false;
		}
		MySQLAccess mysql = new MySQLAccess();
		return mysql.deleteServerToDispatcher(serverName);
		
	}
	
	public boolean insertLoadBalanceEntry(String destination, int priority, String description){
		System.out.println("InsertLoadBalanceEntry");
		MySQLAccess mysql = new MySQLAccess();
		return mysql.insertServerToDispatcher(destination, priority, description);
	}
		
	public static int verifyLoadBalancerPriority(){
		return 0;
	}
	public void run(){
			
	}
}
