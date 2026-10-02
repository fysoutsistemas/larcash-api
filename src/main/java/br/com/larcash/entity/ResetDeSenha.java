package br.com.larcash.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.larcash.enums.Confirmacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "resets_senhas")
@Entity(name = "ResetDeSenha")
public class ResetDeSenha {
	
	@Id
	@NotBlank(message = "O login é obrigatório")
	@Size(max = 30, message = "O login não deve conter mais de 30 caracteres")
	@Column(name = "login")
	private String login;
	
	@NotBlank(message = "O código OTP é obrigatório")
	@Column(name = "codigo_otp")
	private String codigoOTP;
	
	@NotNull(message = "A data de criação é obrigatória")
	@Column(name = "dt_movto")
	private LocalDateTime dataDeMovto;
	
	@NotNull(message = "A validade do código é obrigatória")
	@Column(name = "valido_ate")
	private LocalDateTime validoAte;
	
	@NotNull(message = "O próximo envio é obrigatório")
	@Column(name = "prox_envio")
	private LocalDateTime proximoEnvio;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O indicador de ativação é obrigatório")
	@Column(name = "fl_resetada")
	private Confirmacao flResetada;
	
	@NotBlank(message = "O telefone é obrigatório")
	@Size(max = 20, message = "O telefone não deve conter mais de 20 caracteres")
	@Column(name = "telefone")
	private String telefone;

	public ResetDeSenha() {
		this.dataDeMovto = LocalDateTime.now();
		this.flResetada = Confirmacao.N;
	}
	
	@JsonIgnore
	@Transient
	public String anonimizarTelefone() {		
		return "+55 (••) •••••-" + telefone.substring(telefone.length() - 4);
	}

	@JsonIgnore
	@Transient
	public boolean isResetada() {
		return getFlResetada() == Confirmacao.S;
	}

}
