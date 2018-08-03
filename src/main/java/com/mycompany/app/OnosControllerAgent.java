package com.mycompany.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

public class OnosControllerAgent extends Thread{

	private String HOST_IP = "10.136.12.38";
	private String ONOS_USER = "karaf";
	private String ONOS_PASSWORD = "karaf";
	private String METHOD = "";
	private String SOURCE;
	private String DESTINATION;


	public String getMETHOD() {
		return METHOD;
	}

	public void setMETHOD(String mETHOD) {
		METHOD = mETHOD;
	}

	public String getSOURCE() {
		return SOURCE;
	}

	public void setSOURCE(String sOURCE) {
		SOURCE = sOURCE;
	}

	public String getDESTINATION() {
		return DESTINATION;
	}

	public void setDESTINATION(String dESTINATION) {
		DESTINATION = dESTINATION;
	}

	public String getHOST_IP() {
		return HOST_IP;
	}

	public void setHOST_IP(String hOST_IP) {
		HOST_IP = hOST_IP;
	}

	public String getONOS_USER() {
		return ONOS_USER;
	}

	public void setONOS_USER(String oNOS_USER) {
		ONOS_USER = oNOS_USER;
	}

	public String getONOS_PASSWORD() {
		return ONOS_PASSWORD;
	}

	public void setONOS_PASSWORD(String oNOS_PASSWORD) {
		ONOS_PASSWORD = oNOS_PASSWORD;
	}

	public OnosControllerAgent(String hostIp, String user, String password, String another){
		this.setHOST_IP(hostIp);
		this.setONOS_USER(user);
		this.setONOS_PASSWORD(password);
	}

	public OnosControllerAgent(){}

	public OnosControllerAgent(String method, String source, String destination){
		this.METHOD = method;
		this.SOURCE = source;
		this.DESTINATION = destination;
	}

	public String getHTML(String urlToRead) throws Exception {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(ONOS_USER, ONOS_PASSWORD.toCharArray());
			}
		});

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
			return this.getHTML("http://"+HOST_IP+":8181/onos/v1/devices");
		}
		catch(Exception e){
			System.out.println(erro+" "+e.getMessage());
			return "";
		}
	}

	public String getHosts(){
		String erro = "Error fetching HOST list from controller...";
		try{
			return this.getHTML("http://"+HOST_IP+":8181/onos/v1/hosts");
		}
		catch(Exception e){
			System.out.println(erro+""+e.getMessage());
			return "";
		}
	}

	public String getFlows(){
		String erro = "Error fetching FLOWS list from controller...";
		try{
			return this.getHTML("http://"+HOST_IP+":8181/onos/v1/flows");
		}
		catch(Exception e){
			System.out.println(erro+" "+e.getMessage());
			return "";
		}
	}

	public String getDatailsFlows(String deviceId, String flowId){
		String erro = "Error fetching FLOWS Detail list from controller...";
		try{
			return this.getHTML("http://"+HOST_IP+":8181/onos/v1/flows/"+deviceId+"/"+flowId);
		}
		catch(Exception e){
			System.out.println(erro+" "+e.getMessage());
			return "";
		}
	}

	public String getIntents(){
		String erro = "Error fetching INTENTS list from controller...";
		try{
			return this.getHTML("http://"+HOST_IP+":8181/onos/v1/intents");
		}
		catch(Exception e){
			System.out.println(erro+" "+e.getMessage());
			return "";
		}
	}
	public String getTopology(){
		String erro = "Error fetching TOPOLOGY from controller...";
		try{
			return this.getHTML("http://"+HOST_IP+":8181/onos/v1/topology");
		}
		catch(Exception e){
			System.out.println(erro+" "+e.getMessage());
			return "";
		}
	}

	public String getObjectiveFlows(){
		String erro = "Error fetching Objective FLOWS list from controller...";
		try{
			return this.getHTML("http://"+HOST_IP+":8181/onos/v1/flowobjectives/next");
		}
		catch(Exception e){
			System.out.println(erro+" "+e.getMessage());
			return "";
		}
	}

	public String getMetersList(){
		String erro = "Error fetching MTERERS list from controller...";
		try{
			return this.getHTML("http://"+HOST_IP+":8181/onos/v1/meters");
		}
		catch(Exception e){
			System.out.println(erro+" "+e.getMessage());
			return "";
		}
	}

	public void removeHostToHostIntent(String source, String destination){
		URL url = null;

		destination = "10.136.12.45";

		Network rede = new Network();
		rede.networkSurvey();

		if(rede.getFibre().getIntentsList().size() == 0){
			//System.out.println("No intentes to remove");
			return;
		}

		String intentID = rede.hashINTENT_ID(rede.getFibre().getIntentsList(), rede.hashHOST_ID(rede.hashHOST_IP(source)), rede.hashHOST_ID(rede.hashHOST_IP(destination)));
		String appID = rede.hashAPP_ID(rede.getFibre().getIntentsList(), rede.hashHOST_ID(rede.hashHOST_IP(source)), rede.hashHOST_ID(rede.hashHOST_IP(destination)));

		if(intentID.equals("NotFound") || appID.equals("NotFound")){
			//System.out.println("This Intent does not found...");
			return;
		}

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(ONOS_USER, ONOS_PASSWORD.toCharArray());
			}
		});

		if(!((intentID.equals("")) || appID.equals(""))){
			
			//System.out.println("Destroying Intent from: "+source+" to: "+destination+"\n");

			try {
				url = new URL("http://" + HOST_IP + ":8181/onos/v1/intents/"
						+ appID + "/" + intentID);
			} catch (MalformedURLException exception) {
				exception.printStackTrace();
			}
			HttpURLConnection httpURLConnection = null;
			try {
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestProperty("Content-Type",
						"application/json");
				httpURLConnection.setRequestMethod("DELETE");
				int res = httpURLConnection.getResponseCode();

				if((res == 200) || (res == 201) || (res == 202) || (res == 203) || (res == 204)){
					//System.out.println("Intent was removed");
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			} finally {
				if (httpURLConnection != null) {
					httpURLConnection.disconnect();
				}
			}
		}
	}

	public void setHostToHostInent(String source, String destination) {
		// String s = // "{"type":"HostToHostIntent","id":"1","appId":"org.onosproject.cli","resources":["00:00:00:00:00:01/-1","00:00:00:00:00:01/-1"],"state":"INSTALLED"}"

//		System.out.println("Source: "+source);
//		System.out.println("Destination: "+destination);
		
		Network rede = new Network();
		rede.networkSurvey();
		
		if (rede.getFibre().getIntentsList().size() == 0) {

			
			if (rede.getFibre().getHostList() == null) {
				rede.networkSurvey();
				try {
					this.finalize();
					return;
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
	
			source = rede.hashHOST_IP(source);
			destination = rede.hashHOST_IP(destination);
			if ((source == "NotFound") || (destination == "NotFound")) {
				System.out.println("\nOnosControllerAgent: Can not create the Intent - check IP Mapping");
				try {
					this.finalize();
					return;
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			source = rede.hashHOST_ID(source);
			destination = rede.hashHOST_ID(destination);
			if ((source == "NotFound") || (destination == "NotFound")) {
				System.out.println("\nOnosControllerAgent: Can not create the Intent - check HOST ID");
				try {
					this.finalize();
					return;
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(ONOS_USER, ONOS_PASSWORD
							.toCharArray());
				}
			});

			//System.out.println("Installing Intent from: "+source+" to: "+destination+"\n");
			
			String intentJSON = "{\"type\": \"HostToHostIntent\", \"appId\": \"org.onosproject.cli\", \"one\":\""
					+ source
					+ "\", \"two\":\""
					+ destination
					+ "\",\"bandwidth\":\"200\"}";

			try {
				URL url = new URL("http://" + HOST_IP + ":8181/onos/v1/intents");
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setConnectTimeout(5000);// 5 secs
				connection.setReadTimeout(5000);// 5 secs

				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type",
						"application/json");

				OutputStreamWriter out = new OutputStreamWriter(
						connection.getOutputStream());
				out.write(intentJSON);
				out.flush();
				out.close();

				int res = connection.getResponseCode();

				InputStream is = connection.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
				connection.disconnect();
				if ((res == 200) || (res == 201) || (res == 202)) {
					//System.out.println("Intent submited\n");
					Intents i = new Intents();
				}
			} catch (Exception e) {
				System.out.println("\nError to submit Intent \n"
						+ e.getMessage());
			}
		} else {
			source = rede.hashHOST_IP(source);
			destination = rede.hashHOST_IP(destination);
			if ((source == "NotFound") || (destination == "NotFound")) {
				System.out.println("\nOnosControllerAgent: Can not create the Intent - check IP Mapping");
				return;
			}

			source = rede.hashHOST_ID(source);
			destination = rede.hashHOST_ID(destination);
			if ((source == "NotFound") || (destination == "NotFound")) {
				System.out.println("\nOnosControllerAgent: Can not create the Intent - check HOST ID");
				return;
			}

			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(ONOS_USER, ONOS_PASSWORD
							.toCharArray());
				}
			});

			if(rede.intentAlreadyExists(rede.getFibre().getIntentsList(), source, destination)){
				//System.out.println("Intent already exists...");
				this.interrupt();
				return;
			}

			String intentJSON = "{\"type\": \"HostToHostIntent\", \"appId\": \"org.onosproject.cli\", \"one\":\""
					+ source
					+ "\", \"two\":\""
					+ destination
					+ "\",\"bandwidth\":\"200\"}";

			try {
				URL url = new URL("http://" + HOST_IP + ":8181/onos/v1/intents");
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setConnectTimeout(5000);// 5 secs
				connection.setReadTimeout(5000);// 5 secs

				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type",
						"application/json");

				OutputStreamWriter out = new OutputStreamWriter(
						connection.getOutputStream());
				out.write(intentJSON);
				out.flush();
				out.close();

				int res = connection.getResponseCode();

				InputStream is = connection.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
				connection.disconnect();
				if ((res == 200) || (res == 201) || (res == 202)) {
					//System.out.println("Intent submited");
				}
			} catch (Exception e) {
				System.out.println("\nError to submit Intent \n"
						+ e.getMessage());
			}
		}
	}

	public void removeFlows(){

		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication ("onos", "rocks".toCharArray());

		    }
		});

		HttpURLConnection conn = null;

		try{
			String urlToRead  = new String("http://"+HOST_IP+":8181/onos/v1/flows/of:0000000000000901/281476285386922");
			StringBuilder result = new StringBuilder();
		      URL url = new URL(urlToRead);
		      conn = (HttpURLConnection) url.openConnection();
		      conn.setRequestMethod("DELETE");
		      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		      String line;
		      while ((line = rd.readLine()) != null) {
		         result.append(line);
		      }
		      rd.close();

		      System.out.println("\n"+conn.getResponseCode()+ " - Fluxo removido com Sucesso!");

		}
		catch(Exception e){
			try {
				System.out.println("\n"+conn.getResponseCode()+" - Erro na Remoção de Flows "+e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void removeLink(String linkId){
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication ("onos", "rocks".toCharArray());

		    }
		});

		HttpURLConnection conn = null;

		try{
			String urlToRead  = new String("http://"+HOST_IP+":8181/onos/v1/link/"+linkId);
			StringBuilder result = new StringBuilder();
		      URL url = new URL(urlToRead);
		      conn = (HttpURLConnection) url.openConnection();
		      conn.setRequestMethod("DELETE");
		      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		      String line;
		      while ((line = rd.readLine()) != null) {
		         result.append(line);
		      }
		      rd.close();

		      System.out.println("\n"+conn.getResponseCode()+ " - Link "+linkId+" removido com Sucesso!");

		}
		catch(Exception e){
			try {
				System.out.println("\n"+conn.getResponseCode()+" - Erro na Remoção do Link "+e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void removeDevice(String deviceId){
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication ("onos", "rocks".toCharArray());

		    }
		});

		HttpURLConnection conn = null;

		try{
			String urlToRead  = new String("http://"+HOST_IP+":8181/onos/v1/devices/"+deviceId);
			StringBuilder result = new StringBuilder();
		      URL url = new URL(urlToRead);
		      conn = (HttpURLConnection) url.openConnection();
		      conn.setRequestMethod("DELETE");
		      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		      String line;
		      while ((line = rd.readLine()) != null) {
		         result.append(line);
		      }
		      rd.close();

		      System.out.println("\n"+conn.getResponseCode()+ " - Device "+deviceId+" removido com Sucesso!");

		}
		catch(Exception e){
			try {
				System.out.println("\n"+conn.getResponseCode()+" - Erro na Remoção do Dispositovo "+e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public String getSwitchPath(String sw1_id, String sw2_id){
		String erro = "Error fetching Switches Paths from controller...";
		try{
			return this.getHTML("http://"+HOST_IP+":8181/onos/v1/paths/"+sw1_id+"/"+sw2_id);
		}
		catch(Exception e){
			System.out.println(erro+" "+e.getMessage());
			return "";
		}
		
	}

	
	public String getLinks(){
		String erro = "Error fetching Switches Links from controller...";
		try{
			return this.getHTML("http://"+HOST_IP+":8181/onos/v1/links");
		}
		catch(Exception e){
			System.out.println(erro+" "+e.getMessage());
			return "";
		}
		
	}
	
	public void run(){
		if(this.METHOD.equals("INVITE")){
			this.setHostToHostInent(this.getSOURCE(), this.getDESTINATION());
		}
		else{
			if(this.METHOD.equals("BYE")){
				this.removeHostToHostIntent(this.getSOURCE(), this.getDESTINATION());
			}
		}
	}
	
}
