package br.com.larcash.enums;

public enum StatusDaLista {
	NOVA,
	INICIADA,
	ENCERRADA;
	
	public static StatusDaLista toEnum(String value) {
		try {
			return StatusDaLista.valueOf(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("O status '" + value + "' é inválido.");
		}
	}
}
