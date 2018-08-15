package com.mycompany.app;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.logging.*;


public class JSONProcessor {

	/*
	 * Define LOGGER 
	 */
	private static final Logger LOGGER = Logger.getLogger(OpenStack.class.getName());
	
	public String getCpuIdleIP(String jsonMeters){
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(jsonMeters);
			JSONObject jsonObject = (JSONObject) obj;

            JSONArray metrics = (JSONArray) jsonObject.get("metrics");

            Iterator<Object> iterator = metrics.iterator();
            //System.out.println("Tamanho JSON: "+metrics.get(3));

            Object cpuIdle =  metrics.get(3);

			try {
				JSONObject jsonobj = (JSONObject) cpuIdle;
				String cpuUtilIP = jsonobj.get("name").toString();

			
				/*
				 * RegEx get from JSON IP.
				 */
				String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
				Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
				Matcher matcher = pattern.matcher(cpuUtilIP);
				if (matcher.find()) {
				    return matcher.group();
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error to recover CPU Idle IP - JSON Processor");
			}

        } catch (Exception e) {
        	LOGGER.log(Level.WARNING, "Error to recover CPU Idle IP (JSON file) - JSON Processor");
            e.printStackTrace();
        }
		return "";
	}

	public Double getCpuIdleValue(String jsonMeters){

		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(jsonMeters);
			JSONObject jsonObject = (JSONObject) obj;

            JSONArray metrics = (JSONArray) jsonObject.get("metrics");

            Iterator<Object> iterator = metrics.iterator();
            //System.out.println("Tamanho JSON: "+metrics.get(3));

            Object cpuIdle =  metrics.get(3);

			try {
				JSONObject jsonobj = (JSONObject) cpuIdle;
				Double cpuUtilNumerical = 0.0;
				String cpuUtilString = jsonobj.get("value").toString();
				cpuUtilNumerical = Double.parseDouble(cpuUtilString);
				return cpuUtilNumerical;

			} catch (Exception e) {
				System.out.println("\nError when recover CPU Idle"+ e.getMessage());
			}

        } catch (Exception e) {
        	System.out.println("Error when reading Compute JSON "+e.getMessage());
            e.printStackTrace();
        }
		return (double) -1.0;
	}

	public String getImageID(String imageJsonResponser, String imageName){

		JSONParser parser = new JSONParser();

		if(imageJsonResponser.isEmpty()){
			System.out.println("Image List is empty");
			return null;
		}

		try{
			Object obj = parser.parse(imageJsonResponser);
			JSONObject jsonObject = (JSONObject) obj;

            JSONArray flavorList = (JSONArray) jsonObject.get("images");

            for(int i = 0; i < flavorList.size(); i++) {
            	try{

            		JSONObject jsonobj = (JSONObject) flavorList.get(i);

            		if(jsonobj.get("name").equals(imageName)){
            			return jsonobj.get("id").toString();
            		}
            	}catch(Exception e){
            		System.out.println("\nError when discover Flavor ID on the OpenStack \n"+e.getMessage());
            	}
            }

        } catch (Exception e) {
        	System.out.println("Error when discover Flavor List \n"+e.getMessage());
            e.printStackTrace();
        }
		return "NotFound";
	}
	
	public String getServerID(String imageJsonResponser, String serverName){

		JSONParser parser = new JSONParser();

		if(imageJsonResponser.isEmpty()){
			System.out.println("Server List is empty");
			return null;
		}

		try{
			Object obj = parser.parse(imageJsonResponser);
			JSONObject jsonObject = (JSONObject) obj;

            JSONArray serverList = (JSONArray) jsonObject.get("servers");

            for(int i = 0; i < serverList.size(); i++) {
            	try{

            		JSONObject jsonobj = (JSONObject) serverList.get(i);

            		if(jsonobj.get("name").equals(serverName)){
            			return jsonobj.get("id").toString();
            		}
            	}catch(Exception e){
            		System.out.println("\nError when discover Server ID on the OpenStack \n"+e.getMessage());
            	}
            }

        } catch (Exception e) {
        	System.out.println("Error when discover Server List \n"+e.getMessage());
            e.printStackTrace();
        }
		return "NotFound";
	}

	public String getSwitchPath(String switchPathJsonResponse){
		JSONParser parser = new JSONParser();

		if(switchPathJsonResponse.isEmpty()){
			System.out.println("Path between Switches does not exists!");
			return null;
		}

		System.out.println("Mensagem Recebida: "+switchPathJsonResponse);
		
		try{
			Object obj = parser.parse(switchPathJsonResponse);
			JSONObject jsonObject = (JSONObject) obj;

            JSONArray paths = (JSONArray) jsonObject.get("paths");

            Iterator<Object> iterator = paths.iterator();
            while (iterator.hasNext()) {
            	try{

            		JSONObject jsonobj = (JSONObject) iterator.next();
            		System.out.println("\nCusto do Caminho: "+(Double)jsonobj.get("cost")+"\n");
            		
            		JSONArray links = (JSONArray) jsonobj.get("links");
            		
            		/*
            		 * Para a lista de links, exiba a origem e o destino de cada um
            		 */
            		JSONObject source = null;
            		JSONObject destination = null;
            		
            		for(int i = 0; i < links.size(); i++){
            			
            			source = (JSONObject) links.get(i);
            			destination = (JSONObject) links.get(i);
            			
            			System.out.println("Fonte ");
            			source = (JSONObject) source.get("src");
            			System.out.println("Switch: "+source.get("device"));
            			System.out.println("Porta: "+source.get("port")+"\n");
            			
            			System.out.println("Destino ");
            			destination = (JSONObject) destination.get("dst");
            			System.out.println("Switch: "+destination.get("device"));
            			System.out.println("Porta: "+destination.get("port")+"\n");
            			
            		}
            		
            	}
            	catch(Exception e){
            		System.out.println("Erro 'getSwitchPath()'\n"+e.getMessage());
            	}
            }

        } catch (Exception e) {
        	System.out.println("Error when discover Flavor List \n"+e.getMessage());
            e.printStackTrace();
        }
		return "NotFound";
	}
	
	
	public String getFlavorID(String flavorJsonResponser, String flavorName){

		JSONParser parser = new JSONParser();

		if(flavorJsonResponser.isEmpty()){
			System.out.println("Flavor List is empty");
			return null;
		}

		try{
			Object obj = parser.parse(flavorJsonResponser);
			JSONObject jsonObject = (JSONObject) obj;

            JSONArray flavorList = (JSONArray) jsonObject.get("flavors");

            for(int i = 0; i < flavorList.size(); i++) {
            	try{

            		JSONObject jsonobj = (JSONObject) flavorList.get(i);

            		if(jsonobj.get("name").equals(flavorName)){
            			return jsonobj.get("id").toString();
            		}
            		else{
            			if(jsonobj.get("name").equals(flavorName)){
                			return jsonobj.get("id").toString();
                		}
            			else{
            				if(jsonobj.get("name").equals(flavorName)){
                    			return jsonobj.get("id").toString();
                    		}
            			}
            		}

            	}catch(Exception e){
            		System.out.println("\nError when discover Flavor ID on the OpenStack \n"+e.getMessage());
            	}
            }

        } catch (Exception e) {
        	System.out.println("Error when discover Flavor List \n"+e.getMessage());
            e.printStackTrace();
        }
		return "NotFound";
	}

	public String getFreeIdFloatingIpId(String serverJsonResponse){

		JSONParser parser = new JSONParser();

		if(serverJsonResponse.isEmpty()){
			System.out.println("Neutron Floating IP List is empty");
			return null;
		}

		try{
			Object obj = parser.parse(serverJsonResponse);
			JSONObject jsonObject = (JSONObject) obj;

            JSONArray floatingIpsList = (JSONArray) jsonObject.get("floatingips");

            for(int i = 0; i < floatingIpsList.size(); i++) {
            	try{

            		JSONObject jsonobj = (JSONObject) floatingIpsList.get(i);

            		if(jsonobj.get("status").equals("DOWN")){
            			return jsonobj.get("id").toString();
            		}

            	}catch(Exception e){
            		System.out.println("\nError when discover Floating IPs Free on the OpenStack "+e.getMessage());
            	}
            }

        } catch (Exception e) {
        	System.out.println("Error when discover Floating IPs "+e.getMessage());
            e.printStackTrace();
        }
		return "NotFound";
	}

	/*Metodo que retorna um ArrayList com os Hosts, cada objeto host possui seus atributos;
	 *
	*/
	public ArrayList<Host> getHostsList(String devicesJsonList){

		if (App.SDN_CONTROLLER_TYPE == "ONOS") {

			JSONParser parser = new JSONParser();

			if (devicesJsonList.isEmpty()) {
				return null;
			}

			Host host = null;
			ArrayList<Host> hostList = new ArrayList<Host>();

			try {
				Object obj = parser.parse(devicesJsonList);
				JSONObject jsonObject = (JSONObject) obj;

				JSONArray hosts = (JSONArray) jsonObject.get("hosts");

				Iterator<Object> iterator = hosts.iterator();
				while (iterator.hasNext()) {
					try {

						JSONObject jsonobj = (JSONObject) iterator.next();

						host = new Host();
						host.setID_HOST((String) jsonobj.get("id"));
						// System.out.println("\nID:
						// "+(String)jsonobj.get("id"));

						host.setMAC((String) jsonobj.get("mac"));
						// System.out.println("MAC:
						// "+(String)jsonobj.get("mac"));

						if (!jsonobj.get("vlan").toString().equals("")) {
							host.setVLAN((String) jsonobj.get("vlan"));
							// System.out.println("VLAN:
							// "+(String)jsonobj.get("vlan"));
						}

						/*
						 * Retira colchetes e aspas do IP
						 */
						JSONArray listaIp = (JSONArray) jsonobj.get("ipAddresses");

						/*
						 * Se a lista de IPs vinda do JSON n�o for vazia, ent�o
						 * os ips dever�o ser estra�dos
						 */
						if (!listaIp.isEmpty()) {
							String ip = new String(listaIp.toString());
							host.setIP_HOST_VLAN(ip.substring(2, ip.length() - 2));
							// System.out.println("IP_VLAN: "+ip.substring(2,
							// ip.length() - 2));
						}

						JSONObject location = (JSONObject) jsonobj.get("location");
						host.setConnectedToSwitch((String) location.get("elementId"));
						// System.out.println("Connected on the Swich:
						// "+(String)location.get("elementId"));

						host.setConnectedToSwitch((String) location.get("port"));
						// System.out.println("On Port:
						// "+(String)location.get("port"));

						hostList.add(host);

					} catch (Exception e) {
						System.out.println("\nError when discover Hosts " + e.getMessage());
					}
				}

			} catch (Exception e) {
				System.out.println("Error when discover Hosts " + e.getMessage());
				e.printStackTrace();
			}
			/*
			 * Aqui sera retornado NULL se nao houver intents instaladas
			 */
			if (hostList.size() == 0) {
				return null;
			} else {
				return hostList;
			}
		} 
		else {
			
			if (App.SDN_CONTROLLER_TYPE == "RYU") {

				if (devicesJsonList.isEmpty()) {
					return null;
				}

				Host host = null;
				ArrayList<Host> hostList = new ArrayList<Host>();

				try {
					
					JSONParser jsonParser = new JSONParser();
					Object obj = jsonParser.parse(devicesJsonList);
					JSONArray json = (JSONArray) obj;
				
					Iterator<JSONObject> iterator = json.iterator();
					
					while(iterator.hasNext()){
						JSONObject jsonChild = iterator.next();
						
						host = new Host();
						
						host.setMAC((String) jsonChild.get("mac"));
					
						JSONArray listaIp = (JSONArray) jsonChild.get("ipv4");
						if (!listaIp.isEmpty()) {
							String ip = new String(listaIp.toString());
							host.setIP_HOST_VLAN(ip.substring(2, ip.length() - 2));

						}
						
						JSONObject location = (JSONObject) jsonChild.get("port");
						host.setConnectedToSwitch((String) location.get("dpid"));

						host.setConnectedToSwitch((String) location.get("hw_addr"));

						hostList.add(host);
						
					}
					
				} catch (Exception e) {
					System.out.println("Error when discover Hosts " + e.getMessage());
					e.printStackTrace();
				}
				/*
				 * Aqui sera retornado NULL se nao houver intents instaladas
				 */
				if (hostList.size() == 0) {
					return null;
				} else {
					return hostList;
				}
			}
		}
		return null;
	}
	

	public ArrayList<Switch> getSwitchList(String devicesJsonList) {

		if (App.SDN_CONTROLLER_TYPE == "ONOS") {

			JSONParser parser = new JSONParser();

			Switch sw = null;
			ArrayList<Switch> swList = new ArrayList<Switch>();

			if (devicesJsonList.isEmpty()) {
				return null;
			}

			try {
				Object obj = parser.parse(devicesJsonList);
				JSONObject jsonObject = (JSONObject) obj;

				JSONArray switches = (JSONArray) jsonObject.get("devices");

				Iterator<Object> iterator = switches.iterator();
				while (iterator.hasNext()) {
					try {

						JSONObject jsonobj = (JSONObject) iterator.next();

						sw = new Switch();

						sw.setID_SWITCH((String) jsonobj.get("id"));
						// System.out.println("\nID:
						// "+(String)jsonobj.get("id"));

						sw.setTYPE((String) jsonobj.get("type"));
						// System.out.println("TYPE:
						// "+(String)jsonobj.get("type"));

						sw.setROLE((String) jsonobj.get("role"));
						// System.out.println("ROLE:
						// "+(String)jsonobj.get("role"));

						sw.setMFR((String) jsonobj.get("mfr"));
						// System.out.println("MFR:
						// "+(String)jsonobj.get("mfr"));

						sw.setHARDWARE((String) jsonobj.get("hw"));
						// System.out.println("HW: "+(String)jsonobj.get("hw"));

						sw.setSOFTWARE((String) jsonobj.get("sw"));
						// System.out.println("SW: "+(String)jsonobj.get("sw"));

						sw.setSERIAL((String) jsonobj.get("serial"));
						// System.out.println("SERIAL:
						// "+(String)jsonobj.get("serial"));

						sw.setCHASSI_ID((String) jsonobj.get("chassisId"));
						// System.out.println("CHASSI_ID:
						// "+(String)jsonobj.get("chassisId"));

						swList.add(sw);

					} catch (Exception e) {
						System.out.println("\nError when discover switches " + e.getMessage());
					}
				}

			} 
			catch (Exception e) {
				System.out.println("Error when discover Switches ");
				e.printStackTrace();
			}
			if (swList.size() == 0) {
				return null;
			} 
			else{
				return swList;
			}
		} 
		else {
			if (App.SDN_CONTROLLER_TYPE == "RYU") {

				Switch sw = null;
				ArrayList<Switch> swList = new ArrayList<Switch>();

				if (devicesJsonList.isEmpty()) {
					return null;
				}

				try {

					JSONParser jsonParser = new JSONParser();
					Object obj = jsonParser.parse(devicesJsonList);
					JSONArray json = (JSONArray) obj;
					Iterator<JSONObject> iterator = json.iterator();

					while (iterator.hasNext()) {
						
						JSONObject jsonChild = iterator.next();
						
						sw = new Switch();
						sw.setID_SWITCH((String) jsonChild.get("dpid"));
						
						JSONArray portList = (JSONArray) jsonChild.get("ports");
						
						if (!portList.isEmpty()) {
							Iterator<JSONObject> it = portList.iterator();
							SWPorts port;
							while(it.hasNext()){
								port = new SWPorts();
								JSONObject jsonPort = it.next();
								port.setDpid(jsonPort.get("dpid").toString());
								port.setHw_addr(jsonPort.get("hw_addr").toString());
								port.setPort_no(jsonPort.get("port_no").toString());
								port.setName(jsonPort.get("name").toString());
								sw.getSW_PORTS().add(port);
							}

						}
						swList.add(sw);
					}

				} catch (Exception e) {
					System.out.println("\nError when discover switches " + e.getMessage());
				}
				if (swList.size() == 0) {
					return null;
				} 
				else{
					return swList;
				}
			}
		}
		return null;
	}

	public ArrayList<Intents> getIntentList(String intentJsonList){
		JSONParser parser = new JSONParser();

		Intents intent = null;
		ArrayList<Intents> intentsList = new ArrayList<Intents>();

		if(intentJsonList.isEmpty()){
			return null;
		}

		try{
			Object obj = parser.parse(intentJsonList);
			JSONObject jsonObject = (JSONObject) obj;

            JSONArray intents = (JSONArray) jsonObject.get("intents");

            Iterator<Object> iterator = intents.iterator();
            while (iterator.hasNext()) {
            	try{

            		JSONObject jsonobj = (JSONObject) iterator.next();

            		intent = new Intents();

            		intent.setTYPE((String)jsonobj.get("type"));
            		//System.out.println("\nTYPE: "+(String)jsonobj.get("type"));

            		intent.setID((String)jsonobj.get("id"));
            		//System.out.println("ID: "+(String)jsonobj.get("id"));

            		intent.setAPP_ID((String)jsonobj.get("appId"));
            		//System.out.println("APP_ID: "+(String)jsonobj.get("appId"));

            		/*
            		 *Retira colchetes e aspas dos MACS associados das INTENTS
            		 */
            		JSONArray ips = (JSONArray) jsonobj.get("resources");

            		/*
            		 * Se a lista de INTENTES vinda do JSON n�o for vazia, ent�o os MACS dever�o ser estra�dos
            		 */
            		if(!ips.isEmpty()){
            			String ipsList = new String(ips.toString());
            			String firstHost = new String(ips.toString());
            			String secondHost = new String(ips.toString());

            			firstHost = ipsList.substring(2,ipsList.lastIndexOf(",")-1);
            			firstHost = firstHost.replace("\\", "");//Retira a contra barra
            			//System.out.println("HOST1: "+firstHost);

            			secondHost = ipsList.substring(ipsList.lastIndexOf(",")+2,ipsList.length()-2);
            			secondHost = secondHost.replace("\\", "");//Retira a contra barra
            			//System.out.println("HOST2: "+secondHost);

            			ArrayList<String> hostsIntent = new ArrayList<String>();
            			hostsIntent.add(firstHost);
            			hostsIntent.add(secondHost);

            			intent.setPAIRS(hostsIntent);

            			intentsList.add(intent);
            		}

            		intent.setSTATE((String)jsonobj.get("state"));
            		//System.out.println("STATE: "+(String)jsonobj.get("state"));

            	}catch(Exception e){
            		System.out.println("\nError when discover Intents"+e.getMessage());
            	}
            }

        } catch (Exception e) {
        	System.out.println("Error when discover Intentes "+e.getMessage());
            e.printStackTrace();
        }

		/*
		 * Aqui ser� retornado NULL se n�o houver intents instaladas
		 */
		if(intentsList.size() ==0){
			return null;
		}
		else{
			return intentsList;
		}
	}
	public ArrayList<SIPServer> getServersList(String jsonNovaServers){
		
		JSONParser parser = new JSONParser();

		SIPServer instance = null;
		ArrayList<SIPServer> instanceList = new ArrayList<SIPServer>();


		//Check if instance list is empty
		if(jsonNovaServers.isEmpty()){
			System.out.println("Servers List is empty - Check Openstack JSON");
			return null;
		}

		//If not empty
		try{
			Object obj = parser.parse(jsonNovaServers);
			JSONObject jsonObject = (JSONObject) obj;

			JSONArray servers = (JSONArray) jsonObject.get("servers");


			/*
			 * ServerName and ServerStatus - Cloud infrastructure could has another instances. 
			 * 
			 */
			String serverName = "";
			String serverStatus= "";
			
            Iterator<Object> iterator = servers.iterator();
            while (iterator.hasNext()) {
            	try{

            		JSONObject jsonobj = (JSONObject) iterator.next();

            		instance = new SIPServer();

            		serverName = (String)jsonobj.get("name");
            		serverStatus = (String)jsonobj.get("status");
          		            		
           		
            		/*
            		 * Check if instance name is KSR
            		 */
            		if(serverName.contains("KSR") && serverStatus.equals("ACTIVE")){
            			instance.setINSTANCE_NAME((String)jsonobj.get("name"));
            			//System.out.println("\nNAME: "+(String)jsonobj.get("name"));
            		
            			instance.setINSTANCE_KEY_PAR_NAME((String)jsonobj.get("key_name"));
            			//System.out.println("\nKEY_PAIR_NAME: "+(String)jsonobj.get("key_name"));

            			instance.setINSTANCE_STATUS((String)jsonobj.get("status"));
            			//System.out.println("\nINSTANCE_STATUS: "+(String)jsonobj.get("status"));
            			
            			instance.setINSTANCE_ID((String)jsonobj.get("id"));
            			//System.out.println("\nINSTANCE_ID: "+(String)jsonobj.get("id"));

            			/*
            			 *Get from JSON Network entries
            			 */
            			String network = jsonobj.get("addresses").toString();
            			JSONParser parser2 = new JSONParser();
            			Object obj2 = parser2.parse(network);
            			JSONObject jsonObject2 = (JSONObject) obj2;

            			/*
            			 * Name of OpenStack Network - should be changed to most suitable for current deployment
            			 */
            			JSONArray detalhesRede = (JSONArray) jsonObject2.get("net");
            			//System.out.println("Tamanho"+detalhesRede.size());
            			
            			/*
            			 *Case when deployment has Floating IP 
            			 */
            			if(detalhesRede.size() == 2){
            				String DATA_IP = detalhesRede.get(0).toString();
            				String CONTROL_IP = detalhesRede.get(1).toString();

            				//System.out.println("IP de DADOS: "+DATA_IP);
            				//System.out.println("IP de CONTROLE: "+CONTROL_IP);
        				
            				/*
            				 * RegEx to catch IP from JSON
            				 */
            				String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
            				Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
            				Matcher matcher1 = pattern.matcher(DATA_IP);
            				Matcher matcher2 = pattern.matcher(CONTROL_IP);
            				if (matcher1.find()) {
            					DATA_IP = matcher1.group();
            				}
            				if (matcher2.find()) {
            					CONTROL_IP = matcher2.group();
            				}

            				instance.setDATA_IP_ADRESS(DATA_IP);
            				instance.setCONTROL_IP_ADRESS(CONTROL_IP);

            				instanceList.add(instance);
            			}
            			/*
            			 * Case when deployment has only Data IP - from DHCP
            			 */
            			else{
            				String DATA_IP = detalhesRede.get(0).toString();
            				
        					
        					String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        					Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        					Matcher matcher1 = pattern.matcher(DATA_IP);
        					if (matcher1.find()) {
        						DATA_IP = matcher1.group();
        					}


        					instance.setDATA_IP_ADRESS(DATA_IP);

        					instanceList.add(instance);
            			}
            		}
            	}catch(Exception e){
            		LOGGER.log(Level.WARNING, "Error fetching instance details (JSON)  - Compute Survey");
            		System.exit(1);
            	}
            }
            return instanceList;

        } catch (Exception e) {
        	LOGGER.log(Level.WARNING, "Invalid entry in JSON (Instances) file  - Compute Survey");
    		System.exit(1);
        }

		/*
		 * When there are 0 instances in OpenStack will be returned 0
		 */
		return null;
	}
}
