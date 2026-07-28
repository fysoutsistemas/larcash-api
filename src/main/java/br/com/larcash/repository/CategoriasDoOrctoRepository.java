package br.com.larcash.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.CategoriaDoOrcamento;
import br.com.larcash.entity.composite.CategoriaDoOrcamentoId;

@Repository
public interface CategoriasDoOrctoRepository extends 
		JpaRepository<CategoriaDoOrcamento, CategoriaDoOrcamentoId>{
	
	@Query(value = 
			"SELECT co "
			+ "FROM CategoriaDoOrcamento co "
			+ "JOIN FETCH co.categoria "
			+ "WHERE co.orcamento.id = :idDoOrcamento "
			+ "ORDER BY co.categoria.id ")
	public List<CategoriaDoOrcamento> listarPor(Integer idDoOrcamento);
	
	@Query(value = 
			"SELECT co "
			+ "FROM CategoriaDoOrcamento co "
			+ "JOIN FETCH co.categoria "
			+ "WHERE co.orcamento.id = :idDoOrcamento "
			+ "AND co.categoria.id = :idDaCategoria ")
	public CategoriaDoOrcamento buscarPor(Integer idDaCategoria, Integer idDoOrcamento);
	
	@Query(value = 
			"SELECT co.limite "
			+ "FROM CategoriaDoOrcamento co "
			+ "WHERE co.orcamento.id = (SELECT Max(o.id) "
			+ "                         FROM Orcamento o "
			+ "                         WHERE o.familia.id = :idDaFamilia"
			+ "                         AND o.status = 'A') "
			+ "AND co.categoria.id = :idDaCategoria ")
	public BigDecimal buscarLimitePor(Integer idDaFamilia, Integer idDaCategoria);
	
	@Modifying
	@Query(value = 
			"UPDATE CategoriaDoOrcamento co "
			+ "SET co.limite = :limite "
			+ "WHERE co.categoria.id = :idDaCategoria "
			+ "AND co.orcamento.id = :idDoOrcamento ")
	public void atualizarLimitePor(Integer idDaCategoria, Integer idDoOrcamento, BigDecimal limite);
	
}