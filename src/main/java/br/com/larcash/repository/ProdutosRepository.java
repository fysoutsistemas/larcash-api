package br.com.larcash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Produto;
import br.com.larcash.enums.Status;

@Repository
public interface ProdutosRepository extends JpaRepository<Produto, Integer>{

	@Query(value = 
			"SELECT p "
			+ "FROM Produto p "
			+ "JOIN FETCH p.categoria "
			+ "JOIN FETCH p.usuario "
			+ "WHERE p.familia.id = :idDaFamilia "
			+ "AND p.status = :status "
			+ "ORDER BY p.id DESC ")
	public List<Produto> listarPor(Integer idDaFamilia, Status status);
	
	@Query(value = 
			"SELECT p "
			+ "FROM Produto p "
			+ "JOIN FETCH p.categoria "
			+ "JOIN FETCH p.usuario "
			+ "WHERE p.familia.id = :idDaFamilia "
			+ "AND p.id = :idDoProduto ")
	public Produto buscarPor(Integer idDaFamilia, Integer idDoProduto);
	
}
