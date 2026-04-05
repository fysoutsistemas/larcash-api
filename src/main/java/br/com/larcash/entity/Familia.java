package br.com.larcash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "familias")
@Entity(name = "Familia")
public class Familia {

	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Integer id;	
	
	@NotBlank(message = "O nome é obrigatório")
	@Size(max = 100, message = "O nome não deve conter mais de 100 caracteres")
	@Column(name = "nome")
	private String nome;
	
}
