package com.mycompany.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends Thread{

	public static boolean L3_AGENT = false;
	public static String CLOUD_IP = null;
	public static String CLOUD_USER = "orchestrator";
	public static String CLOUD_PASS = "1";
	
	//Para registrar o currentTime em cenarios de experiemnto
	public void upTimeExperiment() {
		OpenStackAgent deleteComputeThread = new OpenStackAgent("", "");

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		OnosControllerAgent oca = new OnosControllerAgent();
		//deleteComputeThread.run();
	}

	public static void args_handler(String[] args){
		
		/*
		 * Handling command-line arguments
		 */
		
		if(args.length < 2){
			System.out.println("Missing arguments");
			System.exit(1);
		}

		String l3_agent_enabled = new String(args[0]);
		if(l3_agent_enabled == null){
			System.out.println("Missing IP L3_AGENT specification");
			System.exit(1);
		}
		if(l3_agent_enabled.toUpperCase().equals("TRUE")){
			L3_AGENT = true;
		}
		else{
			if(l3_agent_enabled.toUpperCase().equals("FALSE")){
				L3_AGENT = false;
			}
			else{
				System.out.println("Incorrect L3 Agent Parameter - boolean is required");
				System.exit(1);
			}
		}
		
		String cloud_ip = new String(args[1]);
		if(cloud_ip.length() == 0){
			System.out.println("Missing IP Address");
			System.exit(1);
		}
		
		
		String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher1 = pattern.matcher(cloud_ip);
		if (matcher1.find()) {
			CLOUD_IP = cloud_ip;
		}
		else{
			System.out.println("Incorrect Cloud API Parameter - IP Address is required");
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		
		String[] ar = new String[]{"false","200.19.151.12"};
		args_handler(ar);
		
		//Inicio da Thread para monitoramento dos recursos de Compute;
		ComputeMonitor compute = new ComputeMonitor();
		Thread computeThread = new Thread(compute);
		computeThread.start();

		//Inicio da Thread para monitoramento dos recursos de Network;
		Network network = new Network();
		//network.init();
	}
}






























/*
 * // networkAgent.setHostToHostInent(fibre.getHostList().get(0).getID_HOST(),
 * fibre.getHostList().get(1).getID_HOST()); //
 * networkAgent.removeHostToHostIntent("org.onosproject.cli", "0x0");
 * 
 * // OpenStack computeAgent = new OpenStack(); // computeAgent.getTste(); // //
 * networkAgent.removeFlows(); //
 * networkAgent.removeDevice("of:0000000000000903"); //
 * networkAgent.removeDevice("of:0000000000000901"); //
 * networkAgent.removeDevice("of:0000000000000902");
 */