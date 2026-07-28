package br.com.larcash.entity.composite;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ItemDaListaId {
	
	@Column(name = "id_lista_compra")
	@EqualsAndHashCode.Include
	private Integer idDaListaDeCompra;
	
	@Column(name = "id_produto")
	@EqualsAndHashCode.Include
	private Integer idDoProduto;
	
}
