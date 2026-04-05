package br.com.larcash.enums;

public enum Confirmacao {
	
	S, //Sim
	N;  //Não

	public static Confirmacao toEnum(String value) {
		try {
			return Confirmacao.valueOf(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("A confirmação '" + value + "' é inválida");
		}
	}
	
}
