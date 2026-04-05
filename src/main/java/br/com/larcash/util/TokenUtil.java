package br.com.larcash.util;

import java.util.Base64;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.NotBlank;

@Component
public class TokenUtil {

	public String extractLoginDo(
			@NotBlank(message = "O header de autorização é obrigatório")
			String authHeader) {
		
		Preconditions.checkArgument(authHeader.startsWith("Bearer "), "Header inválido");
		
		String token = authHeader.substring(7); 
		
		String dadosDoToken[] = new String(Base64.getDecoder()
	    		.decode(token.getBytes())).split(",");
	    
	    Preconditions.checkArgument(dadosDoToken.length == 2, "Token inválido");
	    
	    return dadosDoToken[0];

	}
	
}
