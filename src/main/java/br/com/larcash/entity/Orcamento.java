package br.com.larcash.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.larcash.enums.Confirmacao;
import br.com.larcash.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "orcamentos")
@Entity(name = "Orcamento")
public class Orcamento {

	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Integer id;
	
	@NotNull(message = "O limite da categoria é obrigatória")
	@Positive(message = "O limite não pode ser negativo")
	@Column(name = "limite")
	private BigDecimal limite;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_familia")
	@NotNull(message = "A família é obrigatória")
	private Familia familia;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O status é obrigatório")
	@Column(name = "status")
	private Status status;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O indicador de configuração de categorias é obrigatório")
	@Column(name = "fl_categs_config")
	private Confirmacao flCategoriasConfiguradas;
	
	public Orcamento() {
		this.status = Status.A;
		this.flCategoriasConfiguradas = Confirmacao.N;
	}
	
	@JsonIgnore
	@Transient
	public boolean isNovo() {
		return getId() == null || getId() <= 0;
	}
	
	@JsonIgnore
	@Transient
	public Integer getIdDaFamilia() {
		return getFamilia().getId();
	}
	
	@JsonIgnore
	@Transient
	public boolean isAtivo() {
		return getStatus() == Status.A;
	}
	
	@JsonIgnore
	@Transient
	public boolean isCategoriasConfiguradas() {
		return getFlCategoriasConfiguradas() == Confirmacao.S;
	}
	
}
