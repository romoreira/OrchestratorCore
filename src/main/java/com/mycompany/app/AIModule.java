package com.mycompany.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AIModule extends Thread{
	
	private String WORKER = "";
	
	public AIModule(String worker){
		this.WORKER = worker;
	}
	
	public void querryModule(){
		QoSModule qosModule = null;
		Thread qosEnforcement = null;

		DatagramSocket clientSocket = null;
		DatagramPacket sendPacket = null;
		InetAddress IPAddress = null;
		/*
		 * Criando o socket
		 */
		try{
			clientSocket = new DatagramSocket();
			byte[] ip_address = {10,0,0,100};
			IPAddress = InetAddress.getByAddress(ip_address);
		}
		catch(Exception e){
			System.out.println("Erro ao criar socket");
		}
		
		byte[] sendData = new byte[1024];
		
		System.out.println("Querry module is Running");
		
		try{
			DatagramSocket serverSocket = new DatagramSocket(8082);
			byte[] receiveData = new byte[1024];
			while(true){
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
				
				
			
				System.out.println(sentence);
				
				/*
				 * Querry AI Module about received network Snapshot
				 */
				sendData = sentence.getBytes();
				sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8083);
				clientSocket.send(sendPacket);
				clientSocket.close();				
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
	public void trainingModule(){
		
		try{
			String pythonScriptPath = "/home/rodrigo/workspace/OrchestratorModules/Modules/AIModule.py";
			String[] cmd = new String[2];
			cmd[0] = "/home/rodrigo/anaconda3/bin/python3.6"; // check version of installed python: python -V
			cmd[1] = pythonScriptPath;
			 
			// create runtime to execute external command
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(cmd);
			 
			//retrieve output from python script
			BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while((line = bfr.readLine()) != null) {
				//display each output line form python script
				System.out.println(line);
			}
		}
		catch(Exception e){
			System.out.println("Algum erro na chamada do script de trinamento");
		}
	}
	
	public void run(){
		if(this.WORKER == "TRAINING_AI_MODEL"){
			System.out.println("Training AI Model...");
			this.trainingModule();
		}
		else{
			if(this.WORKER == "TEST_AI_MODEL"){
				this.querryModule();
			}
		}
	}
}
