package br.com.larcash.enums;

public enum StatusDoConvite {

	GERADO,
	CANCELADO,
	CONFIRMADO;
	
	public static StatusDoConvite toEnum(String value) {
		try {
			return StatusDoConvite.valueOf(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("O status '" + value + "' é inválido.");
		}
	}
	
}
