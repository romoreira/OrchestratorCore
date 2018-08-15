package com.mycompany.app;

public class QoSModule extends Thread{
	
	private String AI_SENTENCE_TEST = "";
	
	public QoSModule(String sentence){
		this.AI_SENTENCE_TEST = sentence;
	}
	
	
	
	public String getAI_SENTENCE_TEST() {
		return AI_SENTENCE_TEST;
	}



	public void setAI_SENTENCE_TEST(String aI_SENTENCE_TEST) {
		AI_SENTENCE_TEST = aI_SENTENCE_TEST;
	}



	public void run(){
		System.out.println("Fazer aqui o QoSEnforcement ");
	}
}
