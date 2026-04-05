package br.com.larcash.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
	
	@NotNull(message = "O ano é obrigatório")
	@Positive(message = "O ano deve ser positivo")
	@Column(name = "ano")
	private Integer ano;
	
	@NotNull(message = "O mês é obrigatório")
	@Min(value = 1, message = "O mês deve estar entre 1 e 12")
	@Max(value = 12, message = "O mês deve estar entre 1 e 12")
	@Column(name = "mes")
	private Integer mes;
	
	@NotNull(message = "O limite da categoria é obrigatória")
	@PositiveOrZero(message = "O limite não pode ser negativo")
	@Column(name = "limite")
	private BigDecimal limite;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_familia")
	@NotNull(message = "A família é obrigatória")
	private Familia familia;
	
}
