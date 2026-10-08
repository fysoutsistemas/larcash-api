package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Administrador;

@Repository
public interface AdminsRepository extends JpaRepository<Administrador, String>{
	
	@Query(value = 
			"SELECT a "
			+ "FROM Administrador a "							
			+ "WHERE a.login = :login")
	public Administrador buscarPor(String login);
	
}
