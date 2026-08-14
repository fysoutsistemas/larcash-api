package br.com.larcash.dto;

import java.util.ArrayList;
import java.util.List;

import br.com.larcash.enums.Confirmacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NovaListaDeCompra {

	@NotBlank(message = "O nome da lista é obrigatório")
	@Size(max = 100, message = "O nome da lista não deve conter mais de 100 caracteres")
	private String nome;
	
	@NotNull(message = "A lista de itens é obrigatória")
	@Size(min = 1, message = "A lista de itens deve possuir pelo menos 1 produto")
	private List<ItemDaListaResumido> itens;

	private Confirmacao flRecorrente;
	
	public NovaListaDeCompra() {
		this.itens = new ArrayList<>();
	}
	
}
