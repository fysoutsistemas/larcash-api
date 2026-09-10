package br.com.larcash.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Lancamento;
import br.com.larcash.repository.projection.GastoPorCategoria;

@Repository
public interface LanctosRepository extends JpaRepository<Lancamento, Integer>{

	@Query(value = 
			"SELECT l "
			+ "FROM Lancamento l "
			+ "JOIN FETCH l.categoria "
			+ "JOIN FETCH l.familia "
			+ "JOIN FETCH l.usuario "
			+ "WHERE l.orcamento.id = :idDoOrcamento "			
			+ "ORDER BY l.id DESC ")
	public List<Lancamento> listarPor(Integer idDoOrcamento);
	
	@Query(value = 
			"SELECT l "
			+ "FROM Lancamento l "
			+ "JOIN FETCH l.categoria c "
			+ "WHERE YEAR(l.data) = :ano "
			+ "AND MONTH(l.data) = :mes "
			+ "AND l.familia.id = :idDaFamilia "
			+ "ORDER BY c.id ")
	public List<Lancamento> listarPor(Integer idDaFamilia, Integer ano, Integer mes);
	
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
			+ "JOIN FETCH l.usuario "
			+ "WHERE f.id = :idDaFamilia "
			+ "AND l.id = :idDoLancto ")
	public Lancamento buscarPor(Integer idDaFamilia, Integer idDoLancto);

	@Query(value = 
			"SELECT COALESCE(SUM(l.valor), 0) "
			+ "FROM Lancamento l "
			+ "WHERE l.orcamento.id = :idDoOrcamento ")
	public BigDecimal somarTotalGastoPor(Integer idDoOrcamento);
	
	@Modifying
	@Query(value = 
			"DELETE FROM Lancamento l "
			+ "WHERE l.familia.id = :idDaFamilia "
			+ "AND l.id = :idDoLancto ")
	public void removerPor(Integer idDaFamilia, Integer idDoLancto);
	
	@Query(value = 
			"SELECT c.id AS idDaCategoria, "
			+ "     c.nome AS nomeDaCategoria, "
			+ "     c.cor AS corDaCategoria, "
			+ "     co.limite as limite, "
			+ "     Sum(l.valor) AS totalGasto, "
			+ "     ( "
			+ "		  Sum(l.valor) "
			+ "       / "
			+ "       ("
			+ "         SELECT Sum(l2.valor) "
			+ "         FROM Lancamento l2 "
			+ "         WHERE l2.orcamento.id = :idDoOrcamento"
			+ "       )"
			+ "     ) * 100 AS percGastoDoOrc, "
			+ "     CASE "
			+ "       WHEN co.limite > 0 THEN (Sum(l.valor) / co.limite) * 100 "
			+ "       ELSE 0.0 "
			+ "     END percGastoDaCateg "
			+ "FROM Lancamento l, "
			+ "     Orcamento o, "
			+ "     CategoriaDoOrcamento co,"
			+ "     Categoria c "
			+ "WHERE l.orcamento.id = o.id "
			+ "AND co.categoria.id = c.id "
			+ "AND co.orcamento.id = o.id "
			+ "AND l.categoria.id = c.id "
			+ "AND o.id = :idDoOrcamento "
			+ "GROUP BY c.id, c.nome, c.cor, co.limite "
			+ "ORDER BY Sum(l.valor) DESC ")
	public List<GastoPorCategoria> listarGastosDasCategsPor(Integer idDoOrcamento);
	
}