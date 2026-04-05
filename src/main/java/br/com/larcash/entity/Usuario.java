package br.com.larcash.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.larcash.enums.Confirmacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "usuarios")
@Entity(name = "Usuario")
public class Usuario {

	@Id
	@NotBlank(message = "O login é obrigatório")
	@Size(max = 30, message = "O login não deve conter mais de 30 caracteres")
	@Column(name = "login")
	private String login;
	
	@NotBlank(message = "A senha é obrigatória")
	@Size(max = 150, message = "A senha não deve conter mais de 150 caracteres")
	@Column(name = "senha")
	private String senha;
	
	@NotBlank(message = "O nome completo é obrigatório")
	@Size(max = 100, message = "O nome completo não deve conter mais de 100 caracteres")
	@Column(name = "nome_completo")
	private String nomeCompleto;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_familia")
	@NotNull(message = "A família é obrigatória")
	private Familia familia;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O indicador de chefe de família é obrigatório")
	@Column(name = "fl_chefe_familia")
	private Confirmacao flChefeDeFamilia;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "O indicador de alteração de orçamento é obrigatório")
	@Column(name = "fl_altera_orcamento")
	private Confirmacao flAlteraOrcamento; 
	
	@Column(name = "ultimo_token")
	private String ultimoToken;
	
	@JsonIgnore
	@Transient	
	public Integer getIdDaFamilia() {
		return getFamilia().getId();
	}
	
}
