package br.com.larcash.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "configs_gerais")
@Entity(name = "ConfiguracaoGeral")
public class ConfiguracaoGeral {
	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Integer id;	
	
	@NotBlank(message = "O telefone é obrigatório")
	@Size(max = 20, message = "O telefone não deve conter mais de 20 caracteres")
	@Column(name = "tel_atendimento")
	private String telefoneDeAtendimento;
	
	@NotNull(message = "O preço da categoria é obrigatória")
	@Positive(message = "O preço deve positivo")
	@Column(name = "preco_licenca")
	private BigDecimal precoDaLicenca;
	
	@NotNull(message = "Os dias de trial é obrigatório")
	@Positive(message = "Os dias de trial deve ser positivo")
	@Column(name = "dias_trial")
	private Integer diasDeTrial;	

}
