package br.com.larcash.dto;

import java.time.LocalDateTime;

import br.com.larcash.enums.Confirmacao;
import br.com.larcash.enums.TipoDeConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ResumoDaAssinatura {
	
	@NotNull(message = "O tipo da assinatura é obrigatório")
	private TipoDeConta tipo;
	
	@NotNull(message = "Os dias restantes é obrigatório")
	@PositiveOrZero(message = "Os dias restantes deve ser igual ou maior que zero")
	private Integer diasRestantes;
	
	@NotNull(message = "O percentual restante é obrigatório")
	@PositiveOrZero(message = "O percentual restante deve ser igual ou maior que zero")
	private Integer percRestante;
	
	@NotNull(message = "O indicador de assinatura expirada é obrigatório")
	private Confirmacao flExpirada;
	
	@NotBlank(message = "O telefone de atendimento é obrigatgório")
	private String telefoneDeAtendimento;
	
	@NotNull(message = "O vencimento da assinatura é obrigatório")
	private LocalDateTime vencimento;

}
