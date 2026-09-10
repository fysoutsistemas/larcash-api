package br.com.larcash.dto.indicadores;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumoGeralDeOrc {
	
	@NotNull(message = "O id do orçamento é obrigatório")
	@Positive(message = "O id do orçamento deve ser positivo")
	private Integer idDoOrcamento;
	
	@NotNull(message = "A data de início do orçamento é obrigatória")
	private LocalDate dataDeInicio;
	
	@NotNull(message = "A qtde de dias decorridos é obrigatória")
	@PositiveOrZero(message = "A qtde de dias decorridos deve ser maior ou igual a zero")
	private Long diasDecorridos;

    @NotNull(message = "O total gasto é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "O total gasto deve ser igual ou maior que zero")
    private BigDecimal totalGasto;

    @NotNull(message = "O percentual gasto é obrigatório")
    @Min(value = 0, message = "O percentual gasto deve ser igual ou maior que zero")
    private Integer percGasto;

    @NotNull(message = "O total disponível é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "O total disponível deve ser igual ou maior que zero")
    private BigDecimal totalDisponivel;

    @NotNull(message = "O limite é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "O limite deve ser igual ou maior que zero")
    private BigDecimal limite;

    @NotNull(message = "A média de gasto diário é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "O limite deve ser igual ou maior que zero")
    private BigDecimal mediaDeGastoDia;

    private LocalDate dataDeTermino;

    @NotNull(message = "A qtde de dias restantes é obrigatória")
	@PositiveOrZero(message = "A qtde de dias restantes deve ser maior ou igual a zero")
    private Long diasRestantes;

    private LocalDate dataDeTerminoProjetada;
    
    @NotNull(message = "O diagnóstico é obrigatório")
    private DiagnosticoDoOrc diagnostico;

    public ResumoGeralDeOrc() {
    	this.dataDeInicio = LocalDate.now();
    	this.diasDecorridos = 0L;
        this.totalGasto = new BigDecimal(0.0);
        this.percGasto = 0;
        this.totalDisponivel = new BigDecimal(0.0);
        this.limite = new BigDecimal(0.0);
        this.mediaDeGastoDia = new BigDecimal(0.0);
        this.dataDeTermino = LocalDate.now();
        this.diasRestantes = 0L;
    }
    
}
