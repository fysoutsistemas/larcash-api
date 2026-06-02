package br.com.larcash.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.larcash.enums.StatusDoConvite;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "convites")
@Entity(name = "Convite")
public class Convite {

	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Integer id;

	@Column(name = "dt_criacao")
	@NotNull(message = "O data de criação é obrigatória")
	private LocalDateTime dataDeCriacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "login_chefe")
	@NotNull(message = "O usuário do chefe da família é obrigatório")
	private Usuario chefe;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_familia")
	@NotNull(message = "A família é obrigatória")
	private Familia familia;
	
	@Column(name = "token")
	@NotBlank(message = "O token do convite é obrigatório")
	private String token;
	
	@Column(name = "link")
	@NotBlank(message = "O link do convite é obrigatório")
	private String link;
	
	@Column(name = "valido_ate")
	@NotNull(message = "A validade é obrigatória")
	private LocalDateTime validoAte;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O status é obrigatório")
	@Column(name = "status")
	private StatusDoConvite status;

	public Convite() {
		this.dataDeCriacao = LocalDateTime.now();
		this.status = StatusDoConvite.GERADO;
	}
	
	@JsonIgnore
	@Transient
	public boolean isConfirmado() {
		return getStatus() == StatusDoConvite.CONFIRMADO;
	}

}
