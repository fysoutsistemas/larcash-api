package br.com.larcash.dto.indicadores;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class EconomiaEmCompras {

    @NotNull(message = "O total economizado é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O total economizado deve ser igual ou maior que zero.")
    private BigDecimal totalEconomizado;

    @NotNull(message = "O percentual economizado é obrigatório.")
    @Min(value = 0, message = "O percentual economizado deve ser igual ou maior que zero.")
    private Integer percEconomizado;

    @NotNull(message = "O total estimado é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O total estimado deve ser igual ou maior que zero.")
    private BigDecimal totalEstimado;

    @NotNull(message = "O total comprado é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O total comprado deve ser igual ou maior que zero.")
    private BigDecimal totalComprado;
    
    public EconomiaEmCompras() {
        this.totalEconomizado = new BigDecimal("0.0");
        this.percEconomizado = 0;
        this.totalEstimado = new BigDecimal("0.0");
        this.totalComprado = new BigDecimal("0.0");
    }

}
