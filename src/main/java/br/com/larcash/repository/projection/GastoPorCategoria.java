package br.com.larcash.repository.projection;

import java.math.BigDecimal;

public interface GastoPorCategoria {
	
	public Integer getIdDaCategoria();
	
	public String getNomeDaCategoria();
	
	public String getCorDaCategoria();
	
	public BigDecimal getTotalGasto();
	
	public BigDecimal getLimite();
	
	public Integer getPercGastoDoOrc();
	
	public Integer getPercGastoDaCateg();
	
}
