package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
	
}
