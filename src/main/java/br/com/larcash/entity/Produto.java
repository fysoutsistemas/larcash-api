package br.com.larcash.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.larcash.config.validation.AoInserir;
import br.com.larcash.enums.Status;
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
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "produtos")
@Entity(name = "Produto")
public class Produto {
	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Null(message = "O id do produto deve ser nulo", groups = AoInserir.class)
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Integer id;
	
	@NotBlank(message = "A descrição é obrigatória")
	@Size(max = 100, message = "A descrição não deve conter mais de 100 caracteres")
	@Column(name = "descricao")
	private String descricao;
	
	@Column(name = "foto")
	private String foto;
	
	@Positive(message = "O preço estimado deve ser positivo")
	@Column(name = "preco_estimado")
	private BigDecimal precoEstimado;
	
	@Column(name = "dt_ultima_compra")
	private LocalDateTime dataDaUltimaCompra;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O status é obrigatório")
	@Column(name = "status")
	private Status status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "login")
	@NotNull(message = "O usuário é obrigatório")
	private Usuario usuario;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_familia")
	@NotNull(message = "A família é obrigatória")
	private Familia familia;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categ_prod")
	@NotNull(message = "A categoria é obrigatória")
	private CategoriaDoProduto categoria;
	
	@Transient
	private String loginDoCriador;
	
	public Produto() {
		this.status = Status.A;
	}
	
	@JsonIgnore
	@Transient
	public String getLogin() {
		return getUsuario().getLogin();
	}
	
	@JsonIgnore
	@Transient
	public Integer getIdDaFamilia() {
		return getFamilia().getId();
	}
	
	@JsonIgnore
	@Transient
	public Integer getIdDaCategoria(){
		return getCategoria().getId();
	}
	
	@JsonIgnore
	@Transient
	public boolean isAtivo() {
		return getStatus() == Status.A;
	}
	
}
