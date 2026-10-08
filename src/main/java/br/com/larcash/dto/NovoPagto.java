package br.com.larcash.dto;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class NovoPagto {

	@Positive(message = "O valor deve ser positivo")
	@Column(name = "valor")
	private BigDecimal valor;
	
}
