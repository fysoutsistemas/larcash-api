package br.com.larcash.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.ItemDaLista;
import br.com.larcash.entity.composite.ItemDaListaId;
import br.com.larcash.enums.Confirmacao;
import br.com.larcash.repository.projection.TotalDeComprasPorCateg;

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
	
	@Query(value = 
			"SELECT c.nome AS nomeDaCategoria, "
			+ "     c.cor AS corDaCategoria, "
			+ "     Coalesce(Sum(il.subtotal), 0) AS totalDaCompra "
			+ "FROM ListaDeCompra lc, "
			+ "     ItemDaLista il, "
			+ "     Produto p, "
			+ "     CategoriaDoProduto c "
			+ "WHERE il.listaDeCompra = lc "
			+ "AND il.produto = p "
			+ "AND p.categoria = c "
			+ "AND lc.status = br.com.larcash.enums.StatusDaLista.ENCERRADA "
			+ "AND il.flagNoCarrinho = br.com.larcash.enums.Confirmacao.S "
			+ "AND lc.familia.id = :idDaFamilia "
			+ "AND CAST(lc.dataDeMovto AS LocalDate) >= :dataDeInicio "
			+ "GROUP BY c.nome, c.cor "
			+ "ORDER BY Coalesce(Sum(il.subtotal), 0) DESC ")
	public List<TotalDeComprasPorCateg> totalizarComprasPor(
			Integer idDaFamilia, LocalDate dataDeInicio);

	
}
