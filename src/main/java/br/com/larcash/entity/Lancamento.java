package br.com.larcash.entity;

import java.math.BigDecimal;
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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "lancamentos")
@Entity(name = "Lancamento")
public class Lancamento {
	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Integer id;	
	
	@NotBlank(message = "A descrição é obrigatória")
	@Size(max = 100, message = "A descrição não deve conter mais de 100 caracteres")
	@Column(name = "descricao")
	private String descricao;
	
	@NotNull(message = "A data é obrigatória")
	@Column(name = "dt_gasto")
	private LocalDateTime data;
	
	@NotNull(message = "O valor é obrigatório")
	@Positive(message = "O valor deve ser positivo")
	@Column(name = "valor")
	private BigDecimal valor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria")
	@NotNull(message = "A categoria é obrigatória")
	private Categoria categoria;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "login")
	@NotNull(message = "O usuário é obrigatório")
	private Usuario usuario;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_familia")
	@NotNull(message = "A família é obrigatória")
	private Familia familia;

	@JsonIgnore
	@Transient
	public String getLogin() {
		return getUsuario().getLogin();
	}

}
