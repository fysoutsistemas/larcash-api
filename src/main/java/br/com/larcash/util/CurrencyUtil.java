package br.com.larcash.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;

@Component
public class CurrencyUtil {

	public String toBRL(
			@NotNull(message = "O valor é obrigatório")
			BigDecimal valor) {
		
		DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
		
		DecimalFormat formatador = new DecimalFormat("#,##0.00", simbolos);
		
		return "R$ " + formatador.format(valor);
		
	}
	
}
