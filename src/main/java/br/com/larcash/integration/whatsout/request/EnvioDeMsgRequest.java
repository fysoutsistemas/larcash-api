package br.com.larcash.integration.whatsout.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnvioDeMsgRequest {

	@NotBlank(message = "O número de destino é obrigatório")
	private String numeroDeDestino;
	
	@NotBlank(message = "O conteúdo da mensagem é obrigatório")
	private String conteudo;
	
}
