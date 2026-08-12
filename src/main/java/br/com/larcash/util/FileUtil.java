package br.com.larcash.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.NotBlank;

@Component
public class FileUtil {
	
	private static final BigDecimal MB = new BigDecimal(1048576);
	
	private static final String PNG = "data:image/png;base64",
			                    JPEG = "data:image/jpeg;base64,";
	
	public BigDecimal getSize(
			@NotBlank(message = "O conteúdo do arquivo é obrigatório")
			String base64File) {
		
		String[] fileParts = base64File.split(",");
		
		Preconditions.checkArgument(fileParts.length == 2, "Formato do arquivo inválido");
		
		byte[] imagem = Base64.getDecoder().decode(fileParts[1]);
		
		BigDecimal tamanho = new BigDecimal(imagem.length)
				.divide(MB, 2, RoundingMode.HALF_EVEN);
		
		return tamanho;

	}
	
	public boolean isImg(
			@NotBlank(message = "O conteúdo do arquivo é obrigatório")
			String base64File) {				
		return base64File.startsWith(PNG) || base64File.startsWith(JPEG); 
	}

}
