package br.com.larcash.dto.indicadores;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaiorGastoDoOrc {

    @NotBlank(message = "O nome da categoria é obrigatório e deve conter ao menos 1 caractere.")
    private String nomeDaCategoria;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O valor deve ser igual ou maior que zero.")
    private BigDecimal valor;

    @NotNull(message = "O percentual de gasto é obrigatório.")
    @Min(value = 0, message = "O percentual de gasto deve ser igual ou maior que zero.")
    private Integer percGasto;
    
    public MaiorGastoDoOrc() {
        this.nomeDaCategoria = "";
        this.valor = new BigDecimal("0.0");
        this.percGasto = 0;
    }
    
}
