package br.com.larcash.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.ItemDaLista;
import br.com.larcash.entity.composite.ItemDaListaId;
import br.com.larcash.enums.Confirmacao;
import br.com.larcash.enums.StatusDaLista;
import br.com.larcash.repository.projection.ResumoDeCompraDoProd;
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
			"SELECT it "
			+ "FROM ItemDaLista it, "
			+ "     ListaDeCompra lc "
			+ "JOIN FETCH it.produto p "
			+ "WHERE it.listaDeCompra = lc "
			+ "AND lc.familia.id = :idDaFamilia "
			+ "AND YEAR(lc.dataDeMovto) = :ano "
			+ "AND MONTH(lc.dataDeMovto) = :mes "
			+ "AND it.flagNoCarrinho = :flagNoCarrinho "
			+ "AND lc.status = :statusDaLista "
			+ "AND lc.flAtivo = :flListaAtiva ")
	public List<ItemDaLista> listarPor(Integer idDaFamilia, Integer ano, Integer mes, 
			Confirmacao flagNoCarrinho, StatusDaLista statusDaLista, Confirmacao flListaAtiva);
	
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
			+ "AND lc.flAtivo = br.com.larcash.enums.Confirmacao.S "
			+ "AND il.flagNoCarrinho = br.com.larcash.enums.Confirmacao.S "
			+ "AND lc.familia.id = :idDaFamilia "
			+ "AND CAST(lc.dataDeMovto AS LocalDate) >= :dataDeInicio "
			+ "GROUP BY c.nome, c.cor "
			+ "ORDER BY Coalesce(Sum(il.subtotal), 0) DESC ")
	public List<TotalDeComprasPorCateg> totalizarComprasPor(
			Integer idDaFamilia, LocalDate dataDeInicio);

	@Query(value = 
			"SELECT p.id AS idDoProduto, "
			+ "     p.descricao AS descricaoDoProd, "
			+ "     cp.nome AS nomeDaCategoria, "
			+ "     cp.cor AS corDaCategoria, "
			+ "     Sum(il.qtde) AS qtdeComprada, "
			+ "     Sum(il.subtotal) AS totalComprado "
			+ "FROM ListaDeCompra lc, "
			+ "     ItemDaLista il, "
			+ "     Produto p, "
			+ "     CategoriaDoProduto cp "
			+ "WHERE il.produto = p "
			+ "AND il.listaDeCompra = lc "
			+ "AND p.categoria = cp "
			+ "AND lc.status = br.com.larcash.enums.StatusDaLista.ENCERRADA "
			+ "AND lc.flAtivo = br.com.larcash.enums.Confirmacao.S "
			+ "AND il.flagNoCarrinho = br.com.larcash.enums.Confirmacao.S "
			+ "AND EXTRACT(YEAR FROM lc.dataDeMovto) = :ano "
			+ "AND EXTRACT(MONTH FROM lc.dataDeMovto) = :mes "
			+ "AND lc.familia.id = :idDaFamilia "
			+ "GROUP BY p.id, p.descricao, cp.nome, cp.cor "
			+ "ORDER BY Sum(il.subtotal) DESC ")
	public List<ResumoDeCompraDoProd> listarResumosPor(Integer ano, 
			Integer mes, Integer idDaFamilia, Pageable paginacao);
	
}
