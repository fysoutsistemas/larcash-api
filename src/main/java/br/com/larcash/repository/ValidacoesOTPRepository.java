package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.ValidacaoOTP;

@Repository
public interface ValidacoesOTPRepository extends JpaRepository<ValidacaoOTP, String>{
	
	@Query(value = 
			"SELECT v "
			+ "FROM ValidacaoOTP v "
			+ "WHERE v.telefone = :telefone")
	public ValidacaoOTP buscarPor(String telefone);

}
