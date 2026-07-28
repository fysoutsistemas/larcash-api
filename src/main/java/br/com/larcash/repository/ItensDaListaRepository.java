package br.com.larcash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.ItemDaLista;
import br.com.larcash.entity.composite.ItemDaListaId;
import br.com.larcash.enums.Confirmacao;

@Repository
public interface ItensDaListaRepository extends JpaRepository<ItemDaLista, ItemDaListaId>{

	@Query(value = 
			"SELECT it "
			+ "FROM ItemDaLista it "
			+ "JOIN FETCH it.produto p "
			+ "JOIN FETCH it.listaDeCompra lc "
			+ "WHERE lc.familia.id = :idDaFamilia "
			+ "AND lc.id = :idDaLista "
			+ "AND p.id = :idDoProduto ")
	public ItemDaLista buscarPor(Integer idDaFamilia, Integer idDaLista, Integer idDoProduto);
	
	@Query(value = 
			"SELECT Coalesce(Count(it), 0) "
			+ "FROM ItemDaLista it "
			+ "WHERE it.flagNoCarrinho = br.com.larcash.enums.Confirmacao.S "
			+ "AND it.listaDeCompra.id = :idDaLista ")
	public Integer contarItensNoCarrinhoPor(Integer idDaLista);
	
	@Modifying
	@Query(value = 
			"UPDATE ItemDaLista it "
			+ "SET it.flagNoCarrinho = :flagNoCarrinho "
			+ "WHERE it.listaDeCompra.id = :idDaLista "
			+ "AND it.produto.id = :idDoProduto ")
	public void atualizarStatusNoCarrinhoPor(Integer idDaLista, 
			Integer idDoProduto, Confirmacao flagNoCarrinho);
	
	@Modifying
	@Query(value = 
			"DELETE FROM ItemDaLista it "
			+ "WHERE it.listaDeCompra.id = :idDaLista ")
	public void removerItensPor(Integer idDaLista);
	
}
