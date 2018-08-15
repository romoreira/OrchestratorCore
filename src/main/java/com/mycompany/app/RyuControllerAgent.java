package com.mycompany.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class RyuControllerAgent {
	
	public String getHTML(String urlToRead) throws Exception {
		
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}
	
	public String getDevices(){
		String erro = "Error fetching DEVICES list from controller...";
		try{
			return this.getHTML("http://"+App.SDN_CONTROLLER_IP+":8080/v1.0/topology/switches");
		}
		catch(Exception e){
			System.out.println(erro+" "+e.getMessage());
			return "";
		}
	}
	
	public String getHosts(){
		String erro = "Error fetching HOST list from controller...";
		try{
			return this.getHTML("http://"+App.SDN_CONTROLLER_IP+":8080/v1.0/topology/hosts");
		}
		catch(Exception e){
			System.out.println(erro+""+e.getMessage());
			return "";
		}
	}
}
