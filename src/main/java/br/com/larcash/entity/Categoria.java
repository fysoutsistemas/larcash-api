package br.com.larcash.entity;

import java.math.BigDecimal;

import br.com.larcash.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "categorias")
@Entity(name = "Categoria")
public class Categoria {
	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Integer id;	
	
	@NotBlank(message = "O nome é obrigatório")
	@Size(max = 100, message = "O nome não deve conter mais de 100 caracteres")
	@Column(name = "nome")
	private String nome;
	
	@NotBlank(message = "O ícone é obrigatório")
	@Size(max = 50, message = "O ícone não deve conter mais de 50 caracteres")
	@Column(name = "icone")
	private String icone;
	
	@NotBlank(message = "A cor é obrigatória")
	@Size(max = 50, message = "A cor não deve conter mais de 50 caracteres")
	@Column(name = "cor")
	private String cor;
	
	@NotNull(message = "O limite da categoria é obrigatória")
	@PositiveOrZero(message = "O limite não pode ser negativo")
	@Column(name = "limite")
	private BigDecimal limite;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O status é obrigatório")
	@Column(name = "status")
	private Status status;

}
