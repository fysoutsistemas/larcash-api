package br.com.larcash.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Orcamento;
import br.com.larcash.repository.projection.TotaisDoOrcamento;

@Repository
public interface OrcamentosRepository extends JpaRepository<Orcamento, Integer>{
	
	@Query(value = 
			"SELECT o "
			+ "FROM Orcamento o "					
			+ "WHERE o.id = (SELECT Max(oaux.id) "
			+ "              FROM Orcamento oaux "
			+ "              WHERE oaux.familia.id = :idDaFamilia) ")
	public Orcamento buscarUltimoPor(Integer idDaFamilia);
	
	@Query("SELECT o "
			+ "FROM Orcamento o "
			+ "WHERE o.familia.id = :idDaFamilia "
			+ "ORDER BY o.id DESC ")
	public List<Orcamento> listarUltimosPor(Integer idDaFamilia, Pageable paginacao);

	@Query(value = 
			"SELECT o "
			+ "FROM Orcamento o "
			+ "JOIN FETCH o.familia "
			+ "WHERE o.id = :id ")
	public Orcamento buscarPor(Integer id);
	
	@Modifying
	@Query(value = 
			"UPDATE Orcamento o "
			+ "SET o.status = br.com.larcash.enums.Status.I "
			+ "WHERE o.familia.id = :idDaFamilia ")
	public void inativarTodosPor(Integer idDaFamilia);
	
	@Modifying
	@Query(value = 
			"UPDATE Orcamento o "
			+ "SET o.flCategoriasConfiguradas = br.com.larcash.enums.Confirmacao.S "
			+ "WHERE o.id = :idDoOrcamento ")
	public void marcarCategsComoConfiguradasPor(Integer idDoOrcamento);
	
	@Query(value = 
			"SELECT o.id AS idDoOrcamento, "
			+ "     o.limite AS limite, "
			+ "     o.dataDeCriacao AS dataDeInicio, "
			+ "     o.dataDeEncerramento AS dataDeTermino, "
			+ "     Coalesce(Sum(l.valor), 0) AS totalGasto, "
			+ "     Coalesce(Avg(l.valor), 0) AS mediaDeGastoDia, "
			+ "     (Coalesce(Sum(l.valor), 0) / o.limite) * 100 AS percGasto, "
			+ "     o.limite - Coalesce(Sum(l.valor), 0) AS totalDisponivel "
			+ "FROM Orcamento o "
			+ "LEFT JOIN o.lanctos l "
			+ "WHERE o.familia.id = :idDaFamilia "
			+ "GROUP BY o.id, o.limite, o.dataDeCriacao, o.dataDeEncerramento "
			+ "ORDER BY o.id ASC ")	
	public List<TotaisDoOrcamento> listarTotalizadoresPor(Integer idDaFamilia, Pageable paginacao);
	
}
