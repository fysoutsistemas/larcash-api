package br.com.larcash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "usuarios")
@Entity(name = "Usuario")
public class Usuario {

	@Id
	@NotBlank(message = "O login é obrigatório")
	@Size(max = 30, message = "O login não deve conter mais de 30 caracteres")
	@Column(name = "login")
	private String login;
	
	@NotBlank(message = "A senha é obrigatória")
	@Size(max = 30, message = "A senha não deve conter mais de 30 caracteres")
	@Column(name = "senha")
	private String senha;
	
	@Column(name = "ultimo_token")
	private String ultimoToken;
	
}
