package br.com.larcash.integration.whatsout.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatbotResponse {

	private String numero;
	
	private String webhook;
	
	private String qrcode64;
	
	private String isConectado;
	
	private String isRecebeMsgs;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
}
