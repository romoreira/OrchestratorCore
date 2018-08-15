package com.mycompany.app;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jnetpcap.protocol.voip.Rtp;
import org.jnetpcap.protocol.voip.Sdp;
import org.jnetpcap.protocol.voip.Sip;

public class PackageInspector extends Network implements Runnable{
	
	public void rtpMonitor(){

		final Sip sip = new Sip();
        Sdp sdp = new Sdp();
        Udp udp = new Udp();

		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs
		int r = Pcap.findAllDevs(alldevs, errbuf);

		if (r != Pcap.OK || alldevs.isEmpty()) {
			//System.err.printf("Nao conseguiu ler a lista de Dispositivos. Erro: %s",errbuf.toString());
			return;
		}
		//System.out.println("Dispositivos NIC encontrados:");
		int i = 0;
		for (PcapIf device : alldevs) {
			String description = (device.getDescription() != null) ? device
					.getDescription() : "No description available";
			//System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
		}

		PcapIf device = alldevs.get(12); // Get first device in list
		//System.out.printf("Listening on Interface: %s\n",(device.getDescription() != null) ? device.getDescription(): device.getName());

		

		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // 10 seconds in millis
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

		
		
		if (pcap == null) {
			System.err.printf("Erro ao capturar trafico do Dispositivo."+ errbuf.toString());
			return;
		}

		PcapPacketHandler<String> dumpHandler = new PcapPacketHandler<String>() {
			public void nextPacket(PcapPacket packet, String user) {
				Sip sip = new Sip();
				Sdp sdp = new Sdp();
				Udp udp = new Udp();
				Tcp tcp = new Tcp();
				Rtp rtp = new Rtp();
				Ip4 ip = new Ip4();
				
				
				try {
					
					if(packet.hasHeader(sip)) {
						//System.out.println("******** THIS PACKET HAS A SIP HEADER ********");
						if ( packet.hasHeader(udp) ) {
							String sipMessage = new String(udp.getPayload());
							//System.out.println(sipMessage);

							String sourceIP = "";
							String destIP = "";

							String[] sipMessageLines = sipMessage.split(System.getProperty("line.separator"));
							String called = "";
							String caller = "";



							if(sipMessageLines[0].contains("INVITE")){//Constroi intent
								called = sipMessageLines[0].substring(sipMessageLines[0].indexOf("@")+1, sipMessageLines[0].indexOf("5060")-1);
								for(int i = 1; i < sipMessageLines.length; i++){
									if(sipMessageLines[i].startsWith("Call-ID:")){
										caller = sipMessageLines[i].substring(sipMessageLines[i].indexOf("@")+1,sipMessageLines[i].length());

										/*
										 * Faze-se o trim por que ele pegaa String com o IP destino com um \n - se não tirar da problema na hora de mapear o IP de dados
										 * e IP de Controle
										 */									
										caller = caller.trim();
									}
								}
								/*
								 * Chamada de rotina para criacao de INTENT
								 */
								dispatch(caller, called, "INVITE");
							}
							else{//Destroi aqui a intent que foi feita
								if(sipMessageLines[0].contains("BYE")){
									//System.out.println("A Intent dever� ser destru�da: ");
									//System.out.println(sipMessage);
									called = sipMessageLines[0].substring(sipMessageLines[0].indexOf("@")+1, sipMessageLines[0].indexOf("5060")-1);
									for(int i = 1; i < sipMessageLines.length; i++){
										if(sipMessageLines[i].startsWith("Call-ID:")){
											caller = sipMessageLines[i].substring(sipMessageLines[i].indexOf("@")+1,sipMessageLines[i].length());
											
											/*
											 * Faze-se o trim por que ele pegaa String com o IP destino com um \n - se não tirar da problema na hora de mapear o IP de dados
											 * e IP de Controle
											 */									
											caller = caller.trim();
										}
									}
									dispatch(caller, called, "BYE");
								}
							}

							byte[] sIP = packet.getHeader(ip).source();
							sourceIP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
							byte[] dIP = packet.getHeader(ip).destination();
							destIP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);

							//System.out.println("*" + sourceIP + "*" + destIP);
						    //System.out.println("Source IP " + sourceIP);
						    //System.out.println("Destination IP " + destIP);
						}
						//System.out.println("Get length of the packet : " + sip.getLength());
						//System.out.println("Get Sip header");
						if(sip.hasField(Sip.Fields.Call_ID)) {
							//System.out.println("Get Call Id " + sip.fieldValue(Sip.Fields.Call_ID));
						} else {
							//System.out.println("No call id in this fragment");
						}
						//System.out.println(packet.toHexdump());
						if (packet.hasHeader(sdp)) {
							//String sdptext = sdp.text();
							//System.out.println(sdptext);
						}
					}
					else {
						if (packet.hasHeader(udp)) {
							//System.out.print(udp.getPayload());						
						}
						else{
							if(packet.hasHeader(tcp)){
								//System.out.println("THIS PACKET HAS A TCP HEADER");
								//String sipMessage = new String(tcp.getPayload());
								//System.out.println(sipMessage);
							}
							else{
								//System.out.println(packet.toString());
								//System.out.println("O pacote recebido de outro Protocolo");
							}

						}
					}
				} catch (StringIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		};
		pcap.loop(pcap.LOOP_INFINATE, dumpHandler, "jNetPcap");
		pcap.close();
	}
/*
 * (non-Javadoc)
 * @see java.lang.Runnable#run()
 */
	public void dispatch(String source, String destination, String method){
		dispatcherDPI(source, destination, method);
	}

	public void receiveOrchestrator(){
		try{
			DatagramSocket serverSocket = new DatagramSocket(8082);
			byte[] receiveData = new byte[1024];
			while(true){
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
				System.out.println(sentence);
				
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
	public void run() {
		System.out.println("DPI Module is Running...");
		this.rtpMonitor();
	}
}
