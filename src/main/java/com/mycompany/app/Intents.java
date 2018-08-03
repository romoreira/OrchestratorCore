package com.mycompany.app;

import java.util.ArrayList;

import javax.swing.InternalFrameFocusTraversalPolicy;

public class Intents extends Thread{
	private String TYPE;
	private String ID;
	private String APP_ID;
	private String STATE;
	private ArrayList<String> PAIRS;
	private int AGE;//Minute 1440 representa 24 horas

	public int getAGE() {
		return AGE;
	}
	public void setAGE(int aGE) {
		AGE = aGE;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getAPP_ID() {
		return APP_ID;
	}
	public void setAPP_ID(String aPP_ID) {
		APP_ID = aPP_ID;
	}
	public String getSTATE() {
		return STATE;
	}
	public void setSTATE(String sTATE) {
		STATE = sTATE;
	}
	public ArrayList<String> getPAIRS() {
		return PAIRS;
	}
	public void setPAIRS(ArrayList<String> pAIRS) {
		PAIRS = pAIRS;
	}
	
	public void intentAnalyzer() throws Exception{
		OnosControllerAgent oca = new OnosControllerAgent();
		JSONProcessor jsonProcessor = new JSONProcessor();
		
		ArrayList<Intents> intentsListTemp =  null;//jsonProcessor.getIntentList(oca.getIntents());
		ArrayList<Intents> intentsList = new ArrayList<Intents>();
		
		int intentsListSize = intentsList.size();
		int listIndex = 0;
		
		while(true){
			
			intentsListTemp =  jsonProcessor.getIntentList(oca.getIntents());
			
			
			//Quando for maior que zero significa que houve atualizacao das Intents - mais chamadas em curso
			if(intentsListTemp.size() > intentsList.size()){
				
				//Se a lista principal for zero entao devo atualizar ele com os novos dados coletados - as intents comecam com idades de 1440 minutos de vida (24hrs)
				if(intentsList.size() == 0){
					for(int i = 0; i < intentsListTemp.size(); i++){
						intentsListTemp.get(i).setAGE(1440);
						intentsList.add(intentsListTemp.get(i));
					}
				}
				//A lista principal nao e zero, entao devera ser adicionado somente aquele elemento novo e com seu tempo de vida igual a 1440 minutos (24hrs)
				else{
				
					for(int i = 0; i < intentsListTemp.size(); i++){
						while(listIndex < intentsListSize){
							if(intentsListTemp.get(i).getID().equals(intentsList.get(listIndex).getId())){
								break;
							}
							else{
								if(listIndex < intentsListSize){
									listIndex++;
								}
								else{
									intentsListTemp.get(i).setAGE(1440);
									intentsList.add(intentsListTemp.get(i));
								}
							}
						}
						listIndex = 0;
					}
				}	
			}
			else{
				if(intentsListTemp.size() != 0){
					int index = 0;
					for(int i = 0; i < intentsList.size(); i++){
						while(index < intentsListTemp.size()){
							if(intentsList.get(i).equals(intentsListTemp.get(index))){
								index = 0;
								break;
							}
							if(index == intentsListTemp.size()){
								intentsList.remove(i);
							}
							index++;
						}
					}
				}
				else{
					try{Thread.sleep(60000);}catch(Exception e){System.out.println("Erro ao colocar a thread IntentsAnalyzer para dormir - caso que a lista de intents diminuiu");}
				}
				
			}
			try{Thread.sleep(60000);}catch(Exception e){System.out.println("Erro ao colocar a Thread intentsAnalyzer para dormir");}
			for(int i = 0; i < intentsList.size(); i++){
				int idadeAnterior = intentsList.get(i).getAGE();
				idadeAnterior = idadeAnterior - 1;
				if(idadeAnterior <= 0){
					oca.removeHostToHostIntent(intentsList.get(i).getPAIRS().get(0), intentsList.get(i).getPAIRS().get(1));
					intentsList.remove(i);
				}
				else{
					intentsList.get(i).setAGE(idadeAnterior - 1);
				}
			}
		}
	}
	
	public void run(){
		try{
			this.intentAnalyzer();
		}
		catch(Exception e){
			System.out.println("Verificar algoritimo intentsAnalyzer: "+e.getMessage());
		}
	}
}
