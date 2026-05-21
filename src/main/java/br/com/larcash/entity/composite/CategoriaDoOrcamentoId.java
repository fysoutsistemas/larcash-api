package br.com.larcash.entity.composite;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CategoriaDoOrcamentoId {
	
	@Column(name = "id_categoria")
	private Integer idDaCategoria;
	
	@Column(name = "id_orcamento")
	private Integer idDoOrcamento;
	
}
