package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Orcamento;

@Repository
public interface OrcamentosRepository extends JpaRepository<Orcamento, Integer>{
	
	@Query(value = 
			"SELECT o "
			+ "FROM Orcamento o "					
			+ "WHERE o.id = (SELECT Max(oaux.id) "
			+ "              FROM Orcamento oaux "
			+ "              WHERE oaux.familia.id = :idDaFamilia) ")
	public Orcamento buscarUltimoPor(Integer idDaFamilia);
	
}
