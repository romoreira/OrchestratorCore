package com.mycompany.app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class QoSModule extends Thread{
	
	private double availableBandwidth = 0;
	private ArrayList<LSP> caminhos = null;	
	public static String source_host = "";
	public static String destination_host = "";
	public static String WORKER = "IDLE";
	public static String TRAFFIC_CATTEGORY = "";
	
	public QoSModule(){
		this.caminhos = new ArrayList<LSP>();
		this.availableBandwidth = 10000;
	}
	
	public double getAvailableBandwidth() {
		return availableBandwidth;
	}

	public void setAvailableBandwidth(double availableBandwidth) {
		this.availableBandwidth = availableBandwidth;
	}


	public String getWORKER() {
		return WORKER;
	}

	public void setWORKER(String wORKER) {
		WORKER = wORKER;
	}

	public String getSource_host() {
		return source_host;
	}

	public void setSource_host(String source_host) {
		this.source_host = source_host;
	}

	public String getDestination_host() {
		return destination_host;
	}

	public void setDestination_host(String destination_host) {
		this.destination_host = destination_host;
	}

	public ArrayList<LSP> getCaminhos() {
		return caminhos;
	}

	public void setCaminhos(ArrayList<LSP> caminhos) {
		this.caminhos = caminhos;
	}

	public void pathMannagerConnection(String json){
		System.out.println("Como esta: "+json);
		System.out.println("Como deveria estar: '{\"ip_src\":\"192.168.0.1\",\"ip_dst\":\"192.168.0.2\",\"required_bandwidth\":5,\"reservation_mode\": \"MAM\"}");

		try{
			String url = "http://10.0.0.100:8081/qos/apply";
			HttpURLConnection con = null;
			try{
				URL myurl = new URL(url);
				byte[] postData = json.getBytes("UTF-8");
				con = (HttpURLConnection) myurl.openConnection();
				con.setDoOutput(true);
				con.setRequestMethod("POST");
				con.setRequestProperty("User-Agent", "Java client");
				con.setRequestProperty("Content-Type", "application/json");
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.write(postData);
				System.out.println("Mandou para o QoSPath");
					
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line;
				StringBuilder content = new StringBuilder();
				while((line = in.readLine()) !=null){
					content.append(line);
					content.append(System.lineSeparator());
				}
					System.out.println("PathMannager Response: "+content);
					
			}
			catch(Exception e){
				System.out.println("Erro: "+e.getMessage());
			}
			finally{
					con.disconnect();
			}
		}catch(Exception e){
			System.out.println("ERRO: "+e.getMessage());
		}
	}
	
	
	public void run(){
		String pathManagerJson = null;
		try{
			System.out.println("Making a LSP between: " + this.source_host + " and: " + this.destination_host + " Traffic Category: " + this.TRAFFIC_CATTEGORY);
			
			JSONParser parser = new JSONParser();//Create JSON parser to get predicted traffic from Classifier
			System.out.println("Antes do parser");
			pathManagerJson = "{\"ip_src\": \""+this.source_host+"\", \"ip_dst\": \""+this.destination_host+"\",\"required_bandwidth\": 1000, \"reservation_mode\": \"MAM\"}";
			System.out.println("Depois do parser: "+ pathManagerJson);
			//JSONObject predicted_traffic_json = (JSONObject) parser.parse(pathManagerJson.toString());//Create JSON object to convert String to JSON Object
			//System.out.println("PathManager Request: "+predicted_traffic_json.toString());
			
			this.pathMannagerConnection(pathManagerJson);
			
			Thread.sleep(30000);
		}
		catch(Exception e){
			System.out.println("ERRO AO DORMIR: "+e.getMessage());
		}
		System.out.println("TERMINOU - UNLOK");
		this.WORKER = "IDLE";
	}
}
