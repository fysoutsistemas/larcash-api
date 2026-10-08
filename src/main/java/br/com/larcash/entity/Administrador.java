package br.com.larcash.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "admins")
@Entity(name = "Administrador")
public class Administrador {

	@Id
	@NotBlank(message = "O login é obrigatório")
	@Size(max = 30, message = "O login não deve conter mais de 30 caracteres")
	@Column(name = "login")
	private String login;
		
	@NotBlank(message = "A senha é obrigatória")
	@Size(max = 150, message = "A senha não deve conter mais de 150 caracteres")
	@Column(name = "senha")
	private String senha;//Senha Utilizada: Larcash@2026
	
	@NotBlank(message = "O nome completo é obrigatório")
	@Size(max = 100, message = "O nome completo não deve conter mais de 100 caracteres")
	@Column(name = "nome_completo")
	private String nomeCompleto;
	
	@Column(name = "ultimo_token")
	private String ultimoToken;
	
	@NotNull(message = "A data de criação é obrigatória")
	@Column(name = "dt_criacao")
	private LocalDateTime dataDeCriacao;
	
	public Administrador() {
		this.dataDeCriacao = LocalDateTime.now();
	}
	
}
