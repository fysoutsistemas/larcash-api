package br.com.larcash.enums;

public enum Status {
	A, //Ativo
	I; //Inativo
	
	public static Status toEnum(String value) {
		try {
			return Status.valueOf(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("O status '" + value + "' é inválido.");
		}
	}
}
