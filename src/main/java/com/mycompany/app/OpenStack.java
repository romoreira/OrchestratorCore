package com.mycompany.app;

import java.io.BufferedReader;
import java.util.logging.*;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import org.openstack4j.model.compute.*;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

public class OpenStack extends App{

	private static final Logger LOGGER = Logger.getLogger(OpenStack.class.getName());
	
	/*
	 * Public Array to be used for other Classes 
	 */
	public ArrayList<SIPServer> sipServersList = null;
	
	private ArrayList<PoolFloatIP> poolFloatIP = null;

	public ArrayList<SIPServer> getSipServersList() {
		return sipServersList;
	}

	public void setSipServersList(ArrayList<SIPServer> sipServersList) {
		this.sipServersList = sipServersList;
	}

	public ArrayList<PoolFloatIP> getPoolFloatIP() {
		return poolFloatIP;
	}

	public void setPoolFloatIP(ArrayList<PoolFloatIP> poolFloatIP) {
		this.poolFloatIP = poolFloatIP;
	}

	public OSClientV3 connectionFactory(){
		OSClientV3 os = null;
		try{
			os = OSFactory.builderV3().endpoint("http://"+App.CLOUD_IP+":5000/v3/").credentials(App.CLOUD_USER, App.CLOUD_PASS, Identifier.byName("Default")).authenticate();
			//LOGGER.log(Level.INFO, "Authenticated! - Compute Survey");
		}catch(Exception e){
			LOGGER.log(Level.WARNING, "Authentication Error - Connection Factory");
			return null;
		}
		return os;
	}
	
	/*
	 * Update VNFs
	 */
	public void computeSurvey(){
		
		/*
		 * Initializa NFV Array
		 */
		this.setSipServersList(new ArrayList<SIPServer>());
		
		/*
		 * Connection Factory call
		 */
		OSClientV3 os = this.connectionFactory();
		
		
		/*
		 * Get Session Token
		 */
		String token = os.getToken().toString();
		token = token.substring(token.indexOf("=")+1, token.indexOf(","));
		String novaServerListJSON = "";

		try{
			StringBuilder result = new StringBuilder();
			URL url = new URL("http://"+App.CLOUD_IP+":8774/v2.1/servers/detail");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Auth-Token", token);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			novaServerListJSON = result.toString();
		}
		catch(Exception e){
			LOGGER.log(Level.WARNING, "Request Error - Compute Survey");
			return;
		}
		
		/*
		 * Create VNF Array list
		 */
		JSONProcessor jsonProcessor = new JSONProcessor();
		
		this.setSipServersList((jsonProcessor.getServersList(novaServerListJSON)));

    	for(int i = 0; i < this.getSipServersList().size(); i++){
    		this.getSipServersList().get(i).setCPU_HISTORY(new LinkedList<Double>());
    	}
    	return;
	}

	public String getServerID(String serverName){
		OSClientV3 os = null;

		try{
			os = OSFactory.builderV3()
					.endpoint("http://200.19.151.205:35357/v3")
					.credentials("rodrigo.moreira@ufu.br", "rodrigo.moreira", Identifier.byName("default"))
					.authenticate();
			//System.out.println("Authenticated!");
		}catch(Exception e){
			System.out.println("Error while authenticating...");
			System.out.println(e.getMessage());
		}

		/*
		 * Aqui � pegado um token da se��o com KeyStone do OpenStack
		 */
		String token = os.getToken().toString();
		token = token.substring(token.indexOf("=")+1, token.indexOf(","));
		String serversJSON = "";
		try
		{	
			StringBuilder result = new StringBuilder();
			URL url = new URL("http://violao:8774/v2.1/6ca3840d77234f8da2175d2f8289cdd6/servers/detail");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Auth-Token", token);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			//System.out.println(result.toString());
			serversJSON = result.toString();
		}
		catch(Exception e){
			System.out.println("Error fetching Server ID on OpenStack \n"+e.getMessage());
		}
		JSONProcessor treatmentJSON = new JSONProcessor();
		return treatmentJSON.getServerID(serversJSON, serverName);
	}
	
	public String getImageID(String imageName){
		
		OpenStack cloud = new OpenStack();
		OSClientV3 os = cloud.connectionFactory();
		
		String token = os.getToken().toString();
		token = token.substring(token.indexOf("=")+1, token.indexOf(","));
		String imageJSON = "";
		try{
			StringBuilder result = new StringBuilder();
			URL url = new URL("http://"+App.CLOUD_IP+":8774/v2.1/images");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Auth-Token", token);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			//System.out.println(result.toString());
			imageJSON = result.toString();
		}
		catch(Exception e){
			System.out.println("Error fetching Image ID on OpenStack \n"+e.getMessage());
		}
		JSONProcessor treatmentJSON = new JSONProcessor();
		return treatmentJSON.getImageID(imageJSON, imageName);
	}

	public String getFlavorID(String flavorName){
		
		OpenStack cloud = new OpenStack();
		OSClientV3 os = cloud.connectionFactory();

		String token = os.getToken().toString();
		token = token.substring(token.indexOf("=")+1, token.indexOf(","));
		String flavorJSON = "";
		try{
			StringBuilder result = new StringBuilder();
			URL url = new URL("http://"+App.CLOUD_IP+":8774/v2.1/flavors");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Auth-Token", token);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			//System.out.println(result.toString());
			flavorJSON = result.toString();
		}
		catch(Exception e){
			System.out.println("Error fetching Flavor ID on OpenStack \n"+e.getMessage());
		}
		JSONProcessor treatmentJSON = new JSONProcessor();
		return treatmentJSON.getFlavorID(flavorJSON, flavorName);

	}

	public boolean  allocateFloatIP(){

		OSClientV3 os = null;

		try{
			os = OSFactory.builderV3()
					.endpoint("http://200.19.151.205:35357/v3")
					.credentials("rodrigo.moreira@ufu.br", "rodrigo.moreira", Identifier.byName("default"))
					.authenticate();
			//System.out.println("Authenticated!");
		}catch(Exception e){
			System.out.println("Error while authenticating...");
			System.out.println(e.getMessage());
		}

		System.out.println("Trying to allocate floating IP");
		FloatingIP ip = os.compute().floatingIps().allocateIP("provider");

		if(ip.equals("")){
			System.out.println("Error allocating floating IP...");
			return false;
		}
		else{
			return true;
		}

	}

	public String getAllocateFloatIpID(){
		OSClientV3 os = null;

		try{
			os = OSFactory.builderV3()
					.endpoint("http://200.19.151.205:35357/v3")
					.credentials("rodrigo.moreira@ufu.br", "rodrigo.moreira", Identifier.byName("default"))
					.authenticate();
			//System.out.println("Authenticated!");
		}catch(Exception e){
			System.out.println("Error while authenticating...");
			System.out.println(e.getMessage());
		}

		/*
		 * Aqui � pegado um token da se��o com KeyStone do OpenStack
		 */
		String token = os.getToken().toString();
		token = token.substring(token.indexOf("=")+1, token.indexOf(","));
		String neutronFloatingIPs = "";
		try{
			StringBuilder result = new StringBuilder();
			URL url = new URL("http://violao:9696/v2.0/floatingips");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Auth-Token", token);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			//System.out.println(result.toString());
			neutronFloatingIPs = result.toString();
		}
		catch(Exception e){
			System.out.println("Alguma caca com a reqest"+e.getMessage());
		}
		JSONProcessor treatmentJSON = new JSONProcessor();
		return treatmentJSON.getFreeIdFloatingIpId(neutronFloatingIPs);
	}

	public void updatePoolFloatIP(){}

	public void getTste(){

		OSClientV3 os = null;

		try{
			os = OSFactory.builderV3()
					.endpoint("http://200.19.151.205:35357/v3")
					.credentials("rodrigo.moreira@ufu.br", "rodrigo.moreira", Identifier.byName("default"))
					.authenticate();
			//System.out.println("Authenticado!");
		}catch(Exception e){
			System.out.println("Erro ao autenticar");
			System.out.println(e.getMessage());
		}

		//System.out.println(os.networking().network().list());

		System.out.println("IPS Flutuantes: "+os.networking().floatingip().list().toString());

		//System.out.println(os.images().listAll());

		//System.out.println("Lista de Flavors Disponiveis"+os.compute().flavors().list());

		//System.out.println(os.images().list().get(0));

		//System.out.println("\nLista de Servidores: "+os.compute().servers().list());

		//System.out.println(os.images().list().get(0).getId());

		//Volume v = null;
		//BlockDeviceMappingBuilder blockDeviceMappingBuilder = null;

		//try{

/*			v = os.blockStorage().volumes()
					.create(Builders.volume()
							.name("Server")
							.description("Bootable install volume")
							.imageRef("1fdca7ac-24fe-49e4-aae8-ef671f54b284")
							.bootable(true)
							.size(15)
							.build()
							);

			System.out.println("Criou o Volume");*/


			//Dividir em Threads - equanto n�o tiver sucesso na cria��o do volume n�o se pode iniciar a cria��o de uma M�quina.

			//blockDeviceMappingBuilder = Builders.blockDeviceMapping()
			//		.uuid("9707222d-8a36-4481-87d9-74b6d7f1e806")
			//		.deviceName("/dev/vda")
			//		.deleteOnTermination(true)
			//		.bootIndex(0);

			//System.out.println("Mapeou o Volume");

		//}catch(Exception e){
		//	System.out.println("\nErro de Volume ou Mapemanto de Bloco - "+e.getMessage());
		//}


//		try{
//
//			OpenStackAgent serverAgent = new OpenStackAgent();
//			serverAgent.allocateFloatIP();
//
//
//			// Aqui cria o Servidor baseado no blockDevice e no Flavor
//			ServerCreate serverCreated = null;
//
//			serverCreated = serverAgent.createInstance("KSR", "544894f4-ed98-43cd-b872-7ba6c7925fe7", "key_k-ims", "c9901656-f54d-4ed3-b293-b21345da3777");
//			if(serverCreated !=null){
//				System.out.println("Server created...");
//				System.out.println("Waiting for infrastructure provisioning - approximately 30 seconds...");
//				Thread.sleep(30000);//wainting 30 seconds to OpenStack up the Virtual machine
//				if(serverAgent.startInstance(serverCreated, os)){
//					System.out.println("Server started...");
//					Thread.sleep(10000);
//
//					NetFloatingIP netFloatingIP = os.networking().floatingip().get("5d889286-7ffe-4a32-b8ac-4d0c55138a1f");
//					os.compute().servers().list();
//					Server server = os.compute().servers().get("b2e1404c-41d8-430e-ade1-a158a1cb0f50");
//					os.compute().floatingIps().addFloatingIP(server, netFloatingIP.getFloatingIpAddress());
//
//					System.out.println("The server is now ready to receive workload");
//				}
//			}
//
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
	}
}
