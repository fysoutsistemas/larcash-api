package br.com.larcash.dto.indicadores;

import java.math.BigDecimal;

import br.com.larcash.repository.projection.ResumoDeCompraDoProd;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MaiorGastoDeProduto {
	
	@NotNull(message = "O id do produto é obrigatório")
	@Positive(message = "O id do produto deve ser positivo")
	public Integer idDoProduto;

    @NotBlank(message = "A descrição do produto é obrigatória e deve conter ao menos 1 caractere.")
    private String descricaoDoProd;

    @NotBlank(message = "O nome da categoria é obrigatório e deve conter ao menos 1 caractere.")
    private String nomeDaCategoria;

    @NotBlank(message = "A cor da categoria é obrigatória e deve conter ao menos 1 caractere.")
    private String corDaCategoria;

    @NotNull(message = "A quantidade comprada é obrigatória.")
    @Min(value = 0, message = "A quantidade comprada deve ser igual ou maior que zero.")
    private Integer qtdeComprada;

    @NotNull(message = "O total comprado é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O total comprado deve ser igual ou maior que zero.")
    private BigDecimal totalComprado;
    
    public MaiorGastoDeProduto(
    		@NotNull(message = "O resumo de compra do produto não pode ser nulo")
    		ResumoDeCompraDoProd resumo) {

    	this.idDoProduto = resumo.getIdDoProduto();
    	this.descricaoDoProd = resumo.getDescricaoDoProd();
    	this.nomeDaCategoria = resumo.getNomeDaCategoria();
    	this.corDaCategoria = resumo.getCorDaCategoria();
    	this.qtdeComprada = resumo.getQtdeComprada();
    	this.totalComprado = resumo.getTotalComprado();

    }
    
    public MaiorGastoDeProduto() {
        this.descricaoDoProd = "";
        this.nomeDaCategoria = "";
        this.corDaCategoria = "";
        this.qtdeComprada = 0;
        this.totalComprado = new BigDecimal("0.0");
    }
    
}
