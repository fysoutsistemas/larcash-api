package br.com.larcash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Lancamento;

@Repository
public interface LanctosRepository extends JpaRepository<Lancamento, Integer>{

	@Query(value = 
			"SELECT l "
			+ "FROM Lancamento l "
			+ "JOIN FETCH l.categoria "
			+ "WHERE l.orcamento.id = :idDoOrcamento "			
			+ "ORDER BY l.id DESC ")
	public List<Lancamento> listarPor(Integer idDoOrcamento);
	
	@Query(value = 
			"SELECT l "
			+ "FROM Lancamento l "
			+ "JOIN FETCH l.categoria "
			+ "JOIN FETCH l.familia "
			+ "WHERE l.usuario.login = :login "
			+ "AND l.id = :id ")
	public Lancamento buscarPor(Integer id, String login);
	
	@Query(value = 
			"SELECT l "
			+ "FROM Lancamento l "
			+ "JOIN FETCH l.categoria "
			+ "JOIN FETCH l.familia f "
			+ "WHERE f.id = :idDaFamilia "
			+ "AND l.id = :idDoLancto ")
	public Lancamento buscarPor(Integer idDaFamilia, Integer idDoLancto);
	
	@Modifying
	@Query(value = 
			"DELETE FROM Lancamento l "
			+ "WHERE l.familia.id = :idDaFamilia "
			+ "AND l.id = :idDoLancto ")
	public void removerPor(Integer idDaFamilia, Integer idDoLancto);
	
}