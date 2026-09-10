package br.com.larcash.repository.projection;

import java.math.BigDecimal;

public interface TotaisDaCompra {

	public BigDecimal getTotalEconomizado();

	public Integer getPercEconomizado();

	public BigDecimal getTotalEstimado();

	public BigDecimal getTotalComprado();

	public Integer getQtdeDeCompras();

	public Integer getQtdeDeItens();

	public BigDecimal getMediaDeCompra();
	
}
