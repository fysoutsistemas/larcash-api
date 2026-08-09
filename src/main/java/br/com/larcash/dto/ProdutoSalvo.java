package br.com.larcash.dto;

import java.math.BigDecimal;

import br.com.larcash.entity.CategoriaDoProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProdutoSalvo {

	@NotNull(message = "O id do produto é obrigatório")
	@Positive(message = "O id deve ser positivo")
	private Integer id;
	
	@NotBlank(message = "A descrição é obrigatória")
	@Size(max = 100, message = "A descrição não deve conter mais de 100 caracteres")
	private String descricao;
	
	private String foto;
	
	private BigDecimal precoEstimado;
	
	@NotNull(message = "A categoria é obrigatória")
	private CategoriaDoProduto categoria;
	
}
