package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Familia;

@Repository
public interface FamiliasRepository extends JpaRepository<Familia, Integer> {
	
	@Query(value = 
			"SELECT f "
			+ "FROM Familia f, "
			+ "     Usuario u "
			+ "WHERE u.familia = f "
			+ "AND u.login = :login ")
	public Familia buscarPorLogin(String login);
	
	@Query(value = 
			"SELECT f "
			+ "FROM Familia f "
			+ "WHERE f.id = :id ")
	public Familia buscarPor(Integer id);

}
