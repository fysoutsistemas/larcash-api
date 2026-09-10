package br.com.larcash.dto.indicadores;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResumoGeralDeCompras {

    @NotNull(message = "A quantidade de compras é obrigatória.")
    @Min(value = 0, message = "A quantidade de compras deve ser igual ou maior que zero.")
    private Integer qtdeDeCompras;

    @NotNull(message = "A quantidade de itens é obrigatória.")
    @Min(value = 0, message = "A quantidade de itens deve ser igual ou maior que zero.")
    private Integer qtdeDeItens;

    @NotNull(message = "A média de compra é obrigatória.")
    @DecimalMin(value = "0.0", inclusive = true, message = "A média de compra deve ser igual ou maior que zero.")
    private BigDecimal mediaDeCompra;
    
    public ResumoGeralDeCompras() {
        this.qtdeDeCompras = 0;
        this.qtdeDeItens = 0;
        this.mediaDeCompra = new BigDecimal("0.0");
    }
    
}
