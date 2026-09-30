package br.com.larcash.enums;

public enum TipoDeConta {
	
	TRIAL,
	CORTESIA,
	MENSALISTA;
	
	public static TipoDeConta toEnum(String value) {
		try {
			return TipoDeConta.valueOf(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("O tipo '" + value + "' é inválido.");
		}
	}
	
}
