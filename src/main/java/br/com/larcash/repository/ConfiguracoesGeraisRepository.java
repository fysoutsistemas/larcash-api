package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.ConfiguracaoGeral;

@Repository
public interface ConfiguracoesGeraisRepository extends JpaRepository<ConfiguracaoGeral, Integer>{

	@Query(value = 
			"SELECT c "
			+ "FROM ConfiguracaoGeral c "
			+ "WHERE c.id = (SELECT Max(caux.id) "
			+ "				 FROM ConfiguracaoGeral caux)")
	public ConfiguracaoGeral buscarUltima();
	
}
