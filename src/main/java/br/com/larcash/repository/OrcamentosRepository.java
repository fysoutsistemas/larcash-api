package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Orcamento;

@Repository
public interface OrcamentosRepository extends JpaRepository<Orcamento, Integer>{

	@Query(value = 
			"SELECT o "
			+ "FROM Orcamento o, "
			+ "     Usuario u "
			+ "WHERE o.familia = u.familia "
			+ "AND o.ano = :ano "
			+ "AND o.mes = :mes "
			+ "AND u.login = :login ")
	public Orcamento buscarPor(Integer ano, Integer mes, String login);
	
}
