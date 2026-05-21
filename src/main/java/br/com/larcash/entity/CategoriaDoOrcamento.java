package br.com.larcash.entity;

import java.math.BigDecimal;

import br.com.larcash.entity.composite.CategoriaDoOrcamentoId;
import br.com.larcash.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "categs_orcamtos")
@Entity(name = "CategoriaDoOrcamento")
@ToString
public class CategoriaDoOrcamento {

	@EmbeddedId
	@NotNull(message = "O id da categoria da familia não pode ser nulo")
	private CategoriaDoOrcamentoId id;
	
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("idDaCategoria")
	@JoinColumn(name = "id_categoria")
	@NotNull(message = "A categoria é obrigatória")
	private Categoria categoria;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("idDoOrcamento")
	@JoinColumn(name = "id_orcamento")
	@NotNull(message = "O orçamento é obrigatório")
	private Orcamento orcamento;
	
	@NotNull(message = "O limite da categoria é obrigatória")
	@PositiveOrZero(message = "O limite não pode ser negativo")
	@Column(name = "limite")
	private BigDecimal limite;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O status da categoria não deve ser nulo")
	@Column(name = "status")
	private Status status;
	
	public CategoriaDoOrcamento() {
		this.limite = new BigDecimal(0.0);
		this.status = Status.A;
	}
	
}
