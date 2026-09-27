package br.com.larcash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Familia;
import br.com.larcash.repository.projection.MembroDaFamilia;

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
	
	@Query(value = 
			"SELECT u.nomeCompleto as nomeCompleto, "
			+ "     u.login AS login, "
			+ "     u.telefone AS telefone, "
			+ "     u.flChefeDeFamilia AS flChefeDeFamilia "
			+ "FROM Usuario u "
			+ "WHERE u.familia.id = :idDaFamilia "
			+ "AND u.login <> :loginDaBusca "
			+ "ORDER BY u.nomeCompleto ")
	public List<MembroDaFamilia> listarMembrosPor(
			Integer idDaFamilia, String loginDaBusca);
	
	@Query(value = 
			"SELECT Count(u) "
			+ "FROM Usuario u "
			+ "WHERE u.familia.id = :idDaFamilia ")
	public Integer contarMembrosPor(Integer idDaFamilia);

}
