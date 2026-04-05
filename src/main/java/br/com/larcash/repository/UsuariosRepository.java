package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Usuario;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, String> {
	
	@Query(value = 
			"SELECT u "
			+ "FROM Usuario u "
			+ "JOIN FETCH u.familia "
			+ "WHERE u.ultimoToken = :token")
	public Usuario buscarPorToken(String token);
	
	@Query(value = 
			"SELECT u "			
			+ "FROM Usuario u "
			+ "JOIN FETCH u.familia "					
			+ "WHERE u.login = :login")	
	public Usuario buscarPorLogin(String login);

}
