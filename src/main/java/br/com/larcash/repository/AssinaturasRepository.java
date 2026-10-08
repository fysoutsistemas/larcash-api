package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Assinatura;

@Repository
public interface AssinaturasRepository extends JpaRepository<Assinatura, Integer>{
	
	@Query(value = 
			"SELECT a "
			+ "FROM Assinatura a "
			+ "JOIN FETCH a.familia f "
			+ "WHERE f.id = :idDaFamilia")
	public Assinatura buscarPor(Integer idDaFamilia);

}
