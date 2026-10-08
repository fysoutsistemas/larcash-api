package br.com.larcash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.PagtoDaAssinatura;

@Repository
public interface PagtosDeAssinaturaRepository extends JpaRepository<PagtoDaAssinatura, Integer>{
	
	@Query(value = 
			"SELECT p "
			+ "FROM Assinatura a, "
			+ "     PagtoDaAssinatura p "
			+ "WHERE p.assinatura = a "
			+ "AND a.familia.id = :idDaFamilia "
			+ "ORDER BY p.id ")
	public List<PagtoDaAssinatura> listarPor(Integer idDaFamilia);

}
