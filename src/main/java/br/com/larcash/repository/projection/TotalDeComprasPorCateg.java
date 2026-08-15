package br.com.larcash.repository.projection;

import java.math.BigDecimal;

public interface TotalDeComprasPorCateg {

	public String getNomeDaCategoria();
	
	public String getCorDaCategoria();

	public BigDecimal getTotalDaCompra();

}
