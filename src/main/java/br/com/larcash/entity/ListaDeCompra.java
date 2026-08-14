package br.com.larcash.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.larcash.config.validation.AoAlterar;
import br.com.larcash.config.validation.AoInserir;
import br.com.larcash.enums.Confirmacao;
import br.com.larcash.enums.StatusDaLista;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "listas_compras")
@Entity(name = "ListaDeCompra")
public class ListaDeCompra {

	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull(message = "O id da lista é obrigatório", groups = AoAlterar.class)
	@Null(message = "O id da lista deve ser nulo", groups = AoInserir.class)
	@EqualsAndHashCode.Include	
	@Column(name = "id")
	private Integer id;	
	
	@NotBlank(message = "O nome é obrigatório")
	@Size(max = 100, message = "O nome não deve conter mais de 100 caracteres")
	@Column(name = "nome")
	private String nome;
	
	@NotNull(message = "A qtde de itens é obrigatória")
	@Positive(message = "A qtde deve ser positiva")
	@Column(name = "qtde_itens")
	private Integer qtde;
	
	@NotNull(message = "O total estimado é obrigatório")
	@PositiveOrZero(message = "O total estimado não pode ser negativo")
	@Column(name = "total_estimado")
	private BigDecimal totalEstimado;
	
	@NotNull(message = "O total da compra é obrigatório")
	@PositiveOrZero(message = "O total da compra não pode ser negativo")
	@Column(name = "total_compra")
	private BigDecimal totalDaCompra;
	
	@NotNull(message = "A diferença entre totais é obrigatória")	
	@Column(name = "dif_totais")
	private BigDecimal difDeTotais;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O status é obrigatório")
	@Column(name = "status")
	private StatusDaLista status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "login_criador")
	@NotNull(message = "O usuário é obrigatório")
	private Usuario usuario;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "login_comprador")
	private Usuario comprador;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_familia")
	@NotNull(message = "A família é obrigatória")
	private Familia familia;
	
	@NotNull(message = "A data de movimento é obrigatório")
	@Column(name = "dt_movto")
	private LocalDateTime dataDeMovto;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O indicador da ativação da lista é obrigatório")
	@Column(name = "fl_ativo")
	private Confirmacao flAtivo;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O indicador de recorrência da lista é obrigatório")
	@Column(name = "fl_recorrente")
	private Confirmacao flRecorrente;
	
	@OneToMany(mappedBy = "listaDeCompra", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("ordem ASC")
	private List<ItemDaLista> itens;
	
	public ListaDeCompra() {
		this.difDeTotais = new BigDecimal(0.0);
		this.totalDaCompra = new BigDecimal(0.0);
		this.totalEstimado = new BigDecimal(0.0);
		this.status = StatusDaLista.NOVA;
		this.dataDeMovto = LocalDateTime.now();
		this.flAtivo = Confirmacao.S;
		this.flRecorrente = Confirmacao.N;
		this.itens = new ArrayList<>();
	}
	
	@Transient
	public void adicionar(
			@NotNull(message = "O produto não pode ser nulo")
			Produto produto, 
			@NotNull(message = "A qtde não pode ser nula")
			@Positive(message = "A qtde deve ser positiva")
			BigDecimal qtde,
			@NotNull(message = "A ordem não pode ser nula")
			@Positive(message = "A ordem deve ser positiva")
			Integer ordem) {
		
		ItemDaLista item = new ItemDaLista();
		item.vincularChave(produto.getId(), getId());
		item.setListaDeCompra(this);
		item.setProduto(produto);
		item.setOrdem(ordem);
		item.setPreco(produto.getPrecoEstimado());
		item.setQtde(qtde);

		BigDecimal subtotal = produto.getPrecoEstimado()
				.multiply(item.getQtde())
				.setScale(2, RoundingMode.HALF_EVEN);

		item.setSubtotal(subtotal);

		this.totalEstimado = totalEstimado.add(subtotal);
		
		this.setDifDeTotais(getTotalEstimado());

		this.getItens().add(item);

	}
	
	@JsonIgnore
	@Transient
	public boolean isNova() {
		return getStatus() == StatusDaLista.NOVA;
	}

	@JsonIgnore
	@Transient
	public boolean isEncerrada() {
		return getStatus() == StatusDaLista.ENCERRADA;
	}
	
	@JsonIgnore
	@Transient
	public boolean isIniciada() {
		return getStatus() == StatusDaLista.INICIADA;
	}
	
	@JsonIgnore
	@Transient
	public boolean isRecorrente() {
		return getFlRecorrente() == Confirmacao.S;
	}
	
	@Transient
	public String getLoginComprador() {
		return getComprador() != null ? getComprador().getLogin() : null;
	}
	
	@Transient
	public String getLoginCriador() {
		return getUsuario() != null ? getUsuario().getLogin() : null;
	}
	
	@Transient
	public void removerItens() {
		this.getItens().clear();
		this.totalEstimado = new BigDecimal(0.0);
		this.difDeTotais = new BigDecimal(0.0);
	}
	
}
