package br.com.larcash.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.larcash.entity.composite.ItemDaListaId;
import br.com.larcash.enums.Confirmacao;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "itens_listas")
@Entity(name = "ItemDaLista")
public class ItemDaLista {
	
	@JsonIgnore
	@EmbeddedId
	@NotNull(message = "O id do item da lista não pode ser nulo")
	private ItemDaListaId id;
		
	@NotNull(message = "A ordem é obrigatória")
	@Positive(message = "A ordem deve ser positiva")
	@Column(name = "ordem")
	private Integer ordem;	
	
	@JsonIgnore
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("idDaListaDeCompra")
	@JoinColumn(name = "id_lista_compra")
	@NotNull(message = "A lista de compra do item é obrigatória")
	private ListaDeCompra listaDeCompra;
	
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("idDoProduto")
	@JoinColumn(name = "id_produto")
	@NotNull(message = "O produto do item é obrigatório")
	private Produto produto;
	
	@Column(name = "preco")
	private BigDecimal preco;
	
	@NotNull(message = "A quantidade é o obrigado")
	@Positive(message = "A quantidade deve ser positiva")
	@Column(name = "qtde")
	private BigDecimal qtde;
	
	@Column(name = "subtotal")
	private BigDecimal subtotal;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O indicador de presente no carrinho é obrigatório")
	@Column(name = "fl_carrinho")
	private Confirmacao flagNoCarrinho;
	
	public ItemDaLista() {
		flagNoCarrinho = Confirmacao.N;
	}
	
	@JsonIgnore
	@Transient
	public void vincularChave(
			@NotNull(message = "O id do produto é obrigatório")
			@Positive(message = "O id do produto deve ser positivo")
			Integer idDoProduto,
			@NotNull(message = "O id da lista é obrigatório")
			@Positive(message = "O id da lista deve ser positivo")
			Integer idDaLista) {
		this.id = new ItemDaListaId();
		this.id.setIdDoProduto(idDoProduto);
		this.id.setIdDaListaDeCompra(idDaLista);		
	}
	
	@JsonIgnore
	@Transient
	public boolean isNoCarrinho() {
		return getFlagNoCarrinho() == Confirmacao.S;
	}
	
	@JsonIgnore
	@Transient
	public Integer getIdDoProduto() {
		return getProduto().getId();
	}
	
}
