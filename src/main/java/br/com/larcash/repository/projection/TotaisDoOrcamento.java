package br.com.larcash.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TotaisDoOrcamento {

	public Integer getIdDoOrcamento();
	
	public BigDecimal getLimite();
	
	public LocalDate getDataDeInicio();
	
	public LocalDate getDataDeTermino();
	
	public BigDecimal getTotalGasto();
	
	public BigDecimal getMediaDeGastoDia();
	
	public Integer getPercGasto();
	
	public BigDecimal getTotalDisponivel();	
	
}
