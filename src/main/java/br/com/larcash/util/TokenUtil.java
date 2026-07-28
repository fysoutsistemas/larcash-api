package br.com.larcash.util;

import java.util.Base64;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.NotBlank;

@Component
public class TokenUtil {
	
	private final int CAMPO_LOGIN = 0,
			          CAMPO_FAMILIA = 2;			

	public String extractLoginDo(String authHeader) {
	    return extractCamposDo(authHeader)[CAMPO_LOGIN];
	}
	
	public Integer extractIdDaFamiliaDo(String authHeader) {
		return Integer.parseInt(extractCamposDo(authHeader)[CAMPO_FAMILIA]);		
	}

	private String[] extractCamposDo(
			@NotBlank(message = "O header de autorização é obrigatório")
			String authHeader) {
		
		Preconditions.checkArgument(authHeader.startsWith("Bearer "), "Header inválido");
		
		String token = authHeader.substring(7); 
		
		String dadosDoToken[] = new String(Base64.getDecoder()
	    		.decode(token.getBytes())).split(",");
	    
	    Preconditions.checkArgument(dadosDoToken.length == 3, "Token inválido");
	    
	    return dadosDoToken;
		
	}
	
}
