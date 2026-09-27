package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.ResetDeSenha;

@Repository
public interface ResetsDeSenhasRepository extends JpaRepository<ResetDeSenha, String>{

	@Query(value = 
			"SELECT r "
			+ "FROM ResetDeSenha r "
			+ "WHERE r.login = :login ")
	public ResetDeSenha buscarPor(String login);
	
	@Query(value = 
			"SELECT r "
			+ "FROM ResetDeSenha r "
			+ "WHERE r.login = :login "
			+ "AND r.codigoOTP = :codigo ")
	public ResetDeSenha buscarPorCodigo(String login, String codigo);
	
}
