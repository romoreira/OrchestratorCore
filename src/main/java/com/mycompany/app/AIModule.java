package com.mycompany.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.omg.CORBA.portable.OutputStream;

public class AIModule extends Thread{
	
	private String WORKER = "";
	
	public AIModule(String worker){
		this.WORKER = worker;
	}
	
	public void querryModule(){
		QoSModule qosModule = new QoSModule();
		Thread qosEnforcement = null;

		DatagramSocket serverSocket = null;//Socket to be used to receive network snapshot
		DatagramPacket receivePacketSnapShot = null;
		
		byte[] snapshot_data = null;//SNAP SHOT data
		byte[] ai_querry = null;//Data to be send to AI Querry
		
		ArrayList<String> source_dest_list = null;
		
		/*
		 * Server Socket - Network SNAP Shot Receiver
		 */
		try{
			serverSocket = new DatagramSocket(8082);
			snapshot_data = new byte[1024];
		}
		catch(Exception e){
			System.out.println("Erro ao criar socket - Network SnapSHOT");
		}
		
		
		
		try{
			
		
			while(true){
				
				/*
				 * String retrieved from UDP buffer is size of buffer, to overcome this, it is necessary to create a new Packet by considering 
				 * size of buffer used, not allocated (1024)
				 */
				receivePacketSnapShot = new DatagramPacket(snapshot_data, 1024);
				serverSocket.receive(receivePacketSnapShot);
				ai_querry = new byte[receivePacketSnapShot.getLength()];
				System.arraycopy(receivePacketSnapShot.getData(), receivePacketSnapShot.getOffset(),ai_querry,0,receivePacketSnapShot.getLength());
				receivePacketSnapShot = new DatagramPacket(ai_querry, ai_querry.length);
				String network_snapshot = new String(receivePacketSnapShot.getData());
				network_snapshot = network_snapshot.replaceAll("\\s", "");
				
				source_dest_list = new ArrayList<String>(Arrays.asList(network_snapshot.split(",")));
				
				String url = "http://10.0.0.100:8083/ml";
				HttpURLConnection con = null;
				try{
					URL myurl = new URL(url);
					StringBuilder string = new StringBuilder();
					string.append("{\"network_snapshot\":\"");
					string.append(network_snapshot.toString());
					string.append("\"}");
					String json = new String(string.toString());
					
					
					byte[] postData = json.getBytes("UTF-8");
					con = (HttpURLConnection) myurl.openConnection();
					con.setDoOutput(true);
					con.setRequestMethod("POST");
					con.setRequestProperty("User-Agent", "Java client");
					con.setRequestProperty("Content-Type", "application/json");
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					wr.write(postData);
					
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String line;
					StringBuilder content = new StringBuilder();
					while((line = in.readLine()) !=null){
						content.append(line);
						content.append(System.lineSeparator());
					}
					System.out.println("Classifier Response: "+content);
					if(QoSModule.WORKER == "IDLE"){
						QoSModule.source_host = source_dest_list.get(1);
						QoSModule.destination_host = source_dest_list.get(3);
						JSONParser parser = new JSONParser();//Create JSON parser to get predicted traffic from Classifier
						JSONObject predicted_traffic_json = (JSONObject) parser.parse(content.toString());//Create JSON object to convert String to JSON Object
						QoSModule.TRAFFIC_CATTEGORY = predicted_traffic_json.get("traffic_catogory").toString();//Put JSON value into QoSModule to after Worker make a LSP Between
						qosEnforcement = new Thread(qosModule);//Create LSP Maker
						QoSModule.WORKER = "BUSY";//Lock QoSModule until a path is being created.
						qosEnforcement.start();
					}
					else{
						System.out.println("LSP Maker already Working");
					}
					
				}
				catch(Exception e){
					System.out.println("Erro: "+e.getMessage());
				}
				finally{
					con.disconnect();
				}
				
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
