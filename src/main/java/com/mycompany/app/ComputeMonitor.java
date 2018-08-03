package com.mycompany.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.logging.*;

public class ComputeMonitor extends OpenStack implements Runnable{

	private static final Logger LOGGER = Logger.getLogger(OpenStack.class.getName());
	public static boolean RUNNING_SCHEDULING = false;
	public static boolean RUNNING_DELETE = false;
	public String fileName = "";
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/*
	 * Aqui  definido o Threshold de ociosidade de CPU, menor que esse valor dever ser disparado algum evento;
	 */
	public final Double CPU_THRESHOLD_OL = 99.0;//Threshold para alta utilização - 90% de utilização
	public final Double CPU_THRESHOLD_LL = 98.0;//Threshold para baixa utilização - 2% de utilização

	public static OpenStack computeServices = new OpenStack();
	JSONProcessor json = new JSONProcessor();

	public boolean checkLowLoad(String DATA_IP){
		/*
		 * Aqui e verificado se o consudmo de historico de CPU esta dentro dos parametros de Threshold -  se estiver, sera retornado um valor verdade;
		 */
		for(int i = 0; i < computeServices.getSipServersList().size(); i++){

			if(DATA_IP.equals(computeServices.getSipServersList().get(i).getDATA_IP_ADRESS().toString())){
				System.out.println("\nHistorico de CPU Ociosa IP: "+ computeServices.getSipServersList().get(i).getDATA_IP_ADRESS() +" -> "+computeServices.getSipServersList().get(i).getCPU_HISTORY().toString());
				/*
				 * Aqui é verificado se a média de consumo de CPU ociosa e maior que o Threshold -quando for significa que máquina está com baixa utilizaão e poderá ser excluída;
				 * IMPORTANTE: deverá ser verificado se a media de consumo é diferente de -1 será igual a -1 quando não tiver histórico, ou seja, menos que três amostras;
				 */
				if((averageCPU_USAGE(this.computeServices.getSipServersList().get(i).getCPU_HISTORY()) > this.CPU_THRESHOLD_LL) && (averageCPU_USAGE(this.computeServices.getSipServersList().get(i).getCPU_HISTORY()) != -1.0)){
					System.out.println("Low utilization!");
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Aqui devera ser retornoado um valor verdade se a Instancia esta em OverLoad;
	 */
	public boolean checkOverLoad(String DATA_IP){

		/*
		 * Aqui e verificado se o consumo historico de CPU esta dentro dos parametros de Threshold - se estiver, sera retornado um valor verdade;
		 */
		for(int i = 0; i < computeServices.getSipServersList().size(); i++){

			if(DATA_IP.equals(computeServices.getSipServersList().get(i).getDATA_IP_ADRESS().toString())){
				//System.out.println("Historico de CPU: "+computeServices.getSipServersList().get(i).getCPU_HISTORY().toString());
				//System.out.println("IP da Maquina: "+computeServices.getSipServersList().get(i).getDATA_IP_ADRESS());
				
				/*
				 * Aqui é verificado se a média de CPU ociosa está abaixo do aceitável - não estará quando estiver com alta utilização
				 * IMPORTANTE: deverá ser verificado também e o histórico não é igual a -1.0 - será quando não tiver valores de CPU_UTIL suficientes para calcular uma media de utilizaão
				 */
				
				if((averageCPU_USAGE(this.computeServices.getSipServersList().get(i).getCPU_HISTORY()) < this.CPU_THRESHOLD_OL) && (averageCPU_USAGE(this.computeServices.getSipServersList().get(i).getCPU_HISTORY()) != -1.0)){
					System.out.println("!!!High utilization!!!");

					/*
					 * Uma vez que o Host foi acometido com consumo alto de CPU e resetado seu historico;
					 */
					LinkedList<Double> resetCpuHistory = new LinkedList<Double>();
					resetCpuHistory.add(100.0);
					resetCpuHistory.add(100.0);
					resetCpuHistory.add(100.0);
					computeServices.getSipServersList().get(i).setCPU_HISTORY(resetCpuHistory);

					/*
					 * Aqui e retornado o valor verdade true pois devera ser criado um novo Host;
					 */
					return true;
				}
			}
		}
		return false;
	}

	public void updateCPU_USAGE(String DATA_IP_SERVER, String cpu_util){
		Double value = 0.0;
		//System.out.println("IP do host que sera atualizado o uso de CPU: "+DATA_IP_SERVER);
		//System.out.println("CPU UTIL: "+cpu_util);
		this.writeCPU_usage(DATA_IP_SERVER+";"+cpu_util+";"+this.getTimeCPU_UTIL());
		value = Double.parseDouble(cpu_util);
		if(value > 100){
			return;
		}
		for(int i = 0; i < this.computeServices.getSipServersList().size(); i++){
			if(this.computeServices.getSipServersList().get(i).getDATA_IP_ADRESS().equals(DATA_IP_SERVER)){
				if(this.computeServices.getSipServersList().get(i).getCPU_HISTORY().size() < 3){
					this.computeServices.getSipServersList().get(i).getCPU_HISTORY().addLast(value);
				}
				else{
					this.computeServices.getSipServersList().get(i).getCPU_HISTORY().addLast(value);
					this.computeServices.getSipServersList().get(i).getCPU_HISTORY().removeFirst();
				}
			}
		}
	}
	
	public String getTimeCPU_UTIL(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date).toString();
	}
	
	public void createFile(){
		/*
		 * Para o nome do arquivo de saída vir com a data de criação;
		 */		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmSS");
		Date date = new Date();
		dateFormat.format(date);
		String fileName = dateFormat.format(date)+"cpu_health.txt";
		File arquivo = new File(fileName);
		this.setFileName(fileName);
	}
	
	public void writeCPU_usage(String texto){
		try{
			BufferedWriter arquivo = new BufferedWriter(new FileWriter(this.getFileName(),true));
			arquivo.newLine();
			arquivo.write(texto);
			arquivo.flush();
			arquivo.close();
		}catch(Exception e){
			System.out.println("Error to write CPU Health in to File"+e.getMessage());
		}
	}
	
	/*
	 * Aqui dever ser retornado uma mdia de tres amostras de consumo de CPU,
	 */
	public Double averageCPU_USAGE(LinkedList<Double> samples){

		//System.out.println("Chegou apra calcular a media");

		/*
		 * Aqui e verificado se existem pelomenos 3 amostras para calcular a media - caso nao haja, sera retornado 100 (ou seja, CPU 100 ociosa)
		 */
		if(samples.size() < 3){
			return -1.0;
		}

		Double avg = 0.0;

		/*
		 * Aqui  percorrido a amostra de consumo de CPU de um determinado host, ser retornado a mdira aritimtica;
		 */
		for(int i = 0; i < 3; i++){
			avg = avg + samples.get(i);
		}
		if(samples.size() == 3){
			//System.out.println("Media de utilizacao: "+(avg/3));
			return (avg/3);
		}
		return -1.0;
	}

	public void computeCollector() {

		try {

			//Monitoring Service - listening on port 7070
			ServerSocket welcomeSocket = new ServerSocket(7070);

			while (true) {

				Socket socket = welcomeSocket.accept();

				/*
				 * Buffer in is received metrics
				 * Buffer out is 200-Ok response
				 */
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


				//Reading Header from received messages
				String line;
				line = in.readLine();
				StringBuilder raw = new StringBuilder();
				raw.append("" + line);
				boolean isPost = line.startsWith("POST");
				int contentLength = 0;
				while (!(line = in.readLine()).equals("")) {
					raw.append('\n' + line);
					if (isPost) {
						final String contentHeader = "Content-Length: ";
						if (line.startsWith(contentHeader)) {
							contentLength = Integer.parseInt(line
									.substring(contentHeader.length()));
						}
					}

				}

				StringBuilder body = new StringBuilder();
				if (isPost) {
					int c = 0;
					for (int i = 0; i < contentLength; i++) {
						c = in.read();
						body.append((char) c);
					}
				}

				//Catch JSON
				raw.append(body.toString());
				String JSON = "";
				JSON = body.toString();

				//TEMP: Show JSON content
				//System.out.println(JSON);
				String cpuMeters = JSON.substring(JSON.indexOf("Content-Length: ") + 1, JSON.length());

				//Response to VNF - when received its statistics
				out.write("HTTP/1.1 200 OK\r\n");
				out.write("Content-Type: text/html\r\n");
				out.write("\r\n");
				out.write(new Date().toString());
				if (isPost) {
					out.write("<br><u>" + body.toString() + "</u>");
				} else {
					out.write("<form method='POST'>");
					out.write("<input name='name' type='text'/>");
					out.write("<input type='submit'/>");
				}
				out.write("</form>");
				out.flush();
				out.close();
				socket.close();
				
				/*
				 *Updata CPU_USAGE from received Instance
				 */
				this.updateCPU_USAGE(this.json.getCpuIdleIP(cpuMeters),this.json.getCpuIdleValue(cpuMeters).toString());
				
		         /*
				 * Compute-aware: check CPU consumption
				 */
				if(this.checkOverLoad(this.json.getCpuIdleIP(cpuMeters))){
					LOGGER.log(Level.INFO, "Need to create new VNF! - Compute Monitor");
					OpenStackAgent computeThread = new OpenStackAgent("CREATE");

					if(this.RUNNING_SCHEDULING == false){
						this.RUNNING_SCHEDULING = true;
						computeThread.start();
					}
					else{
						LOGGER.log(Level.INFO, "VNF Launching already in progress - Compute Monitor");
					}
				}
				
				else{
					if(this.checkLowLoad(this.json.getCpuIdleIP(cpuMeters)) && (computeServices.getSipServersList().size() > 1)){
						OpenStackAgent deleteVNFthread = new OpenStackAgent("DELETE", this.json.getCpuIdleIP(cpuMeters));
						
						/*
						 * Lock to Delete instance only once
						 */
						if(this.RUNNING_DELETE == false){
							this.RUNNING_DELETE = true;
							deleteVNFthread.start();
						}
						else{
							LOGGER.log(Level.INFO, "Destruction of VNF  already in progress - Compute Monitor");
						}
						
					}
				}
			}
		}
		catch(BindException e){
			LOGGER.log(Level.WARNING, "Socket alrady Open - Compute Monitor");
		}
		catch (ConnectException e) {
			LOGGER.log(Level.WARNING, "Socket connection error - Compute Monitor");
			System.exit(1);;
		}
		catch(StringIndexOutOfBoundsException e){
			LOGGER.log(Level.WARNING, "Statistics JSON error - Compute Monitor");
			System.exit(1);
		}
		catch (Exception e) {
			LOGGER.log(Level.WARNING, "General Error - Compute Monitor");
			return;
		}
	}
	public void run(){
		System.out.println("Monitoring Compute...");


		this.createFile();
		this.writeCPU_usage("instance_ip;cpu_idle;time");
		this.computeServices.computeSurvey();
		this.computeCollector();
	}
}
