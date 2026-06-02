package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.larcash.entity.Convite;

public interface ConvitesRepository extends JpaRepository<Convite, Integer>	{

	@Query(value = 
			"SELECT c "
			+ "FROM Convite c "
			+ "JOIN FETCH c.chefe "
			+ "JOIN FETCH c.familia "
			+ "WHERE c.token = :token ")
	public Convite buscarPor(String token);
	
}
