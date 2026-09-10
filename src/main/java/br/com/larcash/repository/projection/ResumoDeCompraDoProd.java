package br.com.larcash.repository.projection;

import java.math.BigDecimal;

public interface ResumoDeCompraDoProd {
	
	public Integer getIdDoProduto();
	
	public String getDescricaoDoProd();
	
	public String getNomeDaCategoria();
	
	public String getCorDaCategoria();
	
	public Integer getQtdeComprada();
	
	public BigDecimal getTotalComprado();
	
}
