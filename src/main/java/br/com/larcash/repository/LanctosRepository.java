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
			+ "WHERE l.familia.id = :idDaFamilia "
			+ "AND YEAR(l.data) = :ano "
			+ "AND MONTH(l.data) = :mes "
			+ "ORDER BY l.data DESC ")
	public List<Lancamento> listarPor(Integer ano, Integer mes, Integer idDaFamilia);
	
	@Query(value = 
			"SELECT l "
			+ "FROM Lancamento l "
			+ "JOIN FETCH l.categoria "
			+ "JOIN FETCH l.familia "
			+ "WHERE l.usuario.login = :login "
			+ "AND l.id = :id "			
			+ "ORDER BY l.data DESC ")
	public Lancamento buscarPor(Integer id, String login);
	
	@Modifying
	@Query(value = 
			"DELETE FROM Lancamento l "
			+ "WHERE l.usuario.login = :login "
			+ "AND l.id = :id ")
	public void removerPor(String login, Integer id);
	
}