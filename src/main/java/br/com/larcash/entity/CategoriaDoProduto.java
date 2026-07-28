package br.com.larcash.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.larcash.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "categs_prods")
@Entity(name = "CategoriaDoProduto")
public class CategoriaDoProduto {
	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Integer id;
	
	@NotBlank(message = "O nome é obrigatório")
	@Size(max = 100, message = "O nome não deve conter mais de 100 caracteres")
	@Column(name = "nome")
	private String nome;
	
	@NotBlank(message = "A cor é obrigatória")
	@Size(max = 50, message = "A cor não deve conter mais de 50 caracteres")
	@Column(name = "cor")
	private String cor;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O status é obrigatório")
	@Column(name = "status")
	private Status status;
	
	@JsonIgnore
	@Transient
	public boolean isAtiva() {
		return getStatus() == Status.A;
	}
	
}
