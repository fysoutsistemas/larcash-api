package br.com.larcash.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "pagtos_assinatura")
@Entity(name = "PagtoDaAssinatura")
public class PagtoDaAssinatura {
	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Integer id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_assinatura")
	@NotNull(message = "A assinatura é obrigatória")
	private Assinatura assinatura;
	
	@NotNull(message = "A data de pagamento é obrigatória")
	@Column(name = "dt_pagto")
	private LocalDateTime dataDePagto;
	
	@Column(name = "acesso_ate")
	private LocalDateTime acessoAte;
	
}
