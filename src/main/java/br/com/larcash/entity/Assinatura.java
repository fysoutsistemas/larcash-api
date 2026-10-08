package br.com.larcash.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;

import br.com.larcash.config.validation.grupo.AoInserir;
import br.com.larcash.enums.TipoDeConta;
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
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "assinaturas")
@Entity(name = "Assinatura")
public class Assinatura {
	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Null(message = "O id do produto deve ser nulo", groups = AoInserir.class)
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_familia")
	@NotNull(message = "A família é obrigatória")
	private Familia familia;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O tipo de conta é obrigatório")
	@Column(name = "tipo_conta")
	private TipoDeConta tipoDeConta;
	
	@NotNull(message = "A data de criação é obrigatória")
	@Column(name = "dt_criacao")
	private LocalDateTime dataDeCriacao;
	
	@Column(name = "acesso_ate")
	private LocalDateTime acessoAte;
	
	@Column(name = "dt_canc")
	private LocalDateTime dataDeCancelamento;
	
	@Transient
	private PagtoDaAssinatura ultimoPagto;
	
	public Assinatura(
			@NotNull(message = "A família é obrigatória")
			Familia familia, 
			@Positive(message = "Os dias de trial deve ser positivo")
			Integer diasDeTrial) {
		this.tipoDeConta = TipoDeConta.TRIAL;
		//A validade é de 7 dias
		this.acessoAte = LocalDateTime.now().plusDays(diasDeTrial);
		this.dataDeCriacao = LocalDateTime.now();
		this.familia = familia;
	}
	
	@Transient
	@JsonIgnore
	public void concederCortesia() {
		
		Preconditions.checkArgument(tipoDeConta != TipoDeConta.CORTESIA, 
				"A assinatura já é uma cortesia");
		
		this.tipoDeConta = TipoDeConta.CORTESIA;
		
		this.acessoAte = LocalDateTime.of(2099, 1, 1, 0, 0, 0, 0);
		
	}
	
	@Transient
	@JsonIgnore
	public boolean isMensalista() {
		return getTipoDeConta() == TipoDeConta.MENSALISTA;
	}
	
	@Transient
	@JsonIgnore
	public boolean isTrial() {
		return getTipoDeConta() == TipoDeConta.TRIAL;
	}
	
	@Transient
	@JsonIgnore
	public boolean isAcessoExpirado() {
		return LocalDateTime.now().isAfter(getAcessoAte());
	}	

}
