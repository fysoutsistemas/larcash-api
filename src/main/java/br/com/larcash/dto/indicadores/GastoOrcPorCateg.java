package br.com.larcash.dto.indicadores;

import java.math.BigDecimal;

import br.com.larcash.repository.projection.GastoPorCategoria;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class GastoOrcPorCateg {
	
	@NotNull(message = "O id da categoria é obrigatório")
	@Positive(message = "O id da categoria deve ser positivo")
	private Integer idDaCategoria;

    @NotBlank(message = "O nome da categoria é obrigatório e deve conter ao menos 1 caractere.")
    private String nomeDaCategoria;

    @NotBlank(message = "A cor da categoria é obrigatória e deve conter ao menos 1 caractere.")
    private String corDaCategoria;

    @NotNull(message = "O limite é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O limite deve ser igual ou maior que zero.")
    private BigDecimal limite;

    @NotNull(message = "O percentual de gasto do orçamento é obrigatório.")
    @Min(value = 0, message = "O percentual de gasto do orçamento deve ser igual ou maior que zero.")
    private Integer percGastoDoOrc;
    
    @NotNull(message = "O percentual de gasto da categoria é obrigatório.")
    @Min(value = 0, message = "O percentual de gasto da categoria deve ser igual ou maior que zero.")
    private Integer percGastoDaCateg;

    @NotNull(message = "O total gasto é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O total gasto deve ser igual ou maior que zero.")
    private BigDecimal totalGasto;
    
    public GastoOrcPorCateg(
    		@NotNull(message = "O gasto no orçamento da categoria não pode ser nulo")
    		GastoPorCategoria gasto) {
    	this.idDaCategoria = gasto.getIdDaCategoria();
    	this.nomeDaCategoria = gasto.getNomeDaCategoria();
    	this.corDaCategoria = gasto.getCorDaCategoria();
    	this.limite = gasto.getLimite();
    	this.percGastoDoOrc = gasto.getPercGastoDoOrc();
    	this.percGastoDaCateg = gasto.getPercGastoDaCateg();
    	this.totalGasto = gasto.getTotalGasto();
    }
    
    public GastoOrcPorCateg() {
        this.nomeDaCategoria = "";
        this.corDaCategoria = "";
        this.limite = new BigDecimal("0.0");
        this.percGastoDoOrc = 0;
        this.totalGasto = new BigDecimal("0.0");
    }
    
}
