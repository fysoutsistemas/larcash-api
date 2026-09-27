package br.com.larcash.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.dto.ResumoDaLista;
import br.com.larcash.entity.ListaDeCompra;
import br.com.larcash.enums.Confirmacao;
import br.com.larcash.enums.StatusDaLista;
import br.com.larcash.repository.projection.TotaisDaCompra;

@Repository
public interface ListasDeCompraRepository extends JpaRepository<ListaDeCompra, Integer>{

	@Query(value = 
			"SELECT l "
			+ "FROM ListaDeCompra l "
			+ "JOIN FETCH l.itens it "
			+ "JOIN FETCH l.usuario u "
			+ "JOIN FETCH l.familia f "
			+ "JOIN FETCH it.produto p "
			+ "JOIN FETCH p.categoria ca"
			+ "LEFT OUTER JOIN FETCH l.comprador co "
			+ "WHERE l.familia.id = :idDaFamilia "
			+ "AND l.id = :idDaLista ")
	public ListaDeCompra buscarPor(Integer idDaFamilia, Integer idDaLista);
	
	@Query(value = 
			"SELECT lc "
			+ "FROM ListaDeCompra lc "
			+ "JOIN FETCH lc.familia f "
			+ "JOIN FETCH lc.usuario u "
			+ "WHERE f.id = :idDaFamilia "
			+ "AND lc.flAtivo = br.com.larcash.enums.Confirmacao.S "
			+ "AND (:status IS NULL OR lc.status = :status) "
			+ "ORDER BY lc.status DESC, lc.id DESC ",
			countQuery = 
					"SELECT Coalesce(Count(lc), 0) "
					+ "FROM ListaDeCompra lc "
					+ "WHERE lc.flAtivo = br.com.larcash.enums.Confirmacao.S "
					+ "AND (:status IS NULL OR lc.status = :status) ")
	public Page<ListaDeCompra> listarPor(Integer idDaFamilia, 
			StatusDaLista status, Pageable paginacao);
	
	@Query(value = 
			"SELECT l "
			+ "FROM ListaDeCompra l "
			+ "WHERE l.familia.id = :idDaFamilia "
			+ "AND YEAR(l.dataDeMovto) = :ano "
			+ "AND MONTH(l.dataDeMovto) = :mes "
			+ "AND l.status = :status "
			+ "AND l.flAtivo = :flAtivo "
			+ "ORDER BY l.id ")
	public List<ListaDeCompra> listarPor(Integer idDaFamilia, Integer ano, 
			Integer mes, StatusDaLista status, Confirmacao flAtivo);
	
	@Query(value = 
			"SELECT NEW br.com.larcash.dto.ResumoDaLista(lc.status, Count(lc)) "
			+ "FROM ListaDeCompra lc "
			+ "WHERE lc.familia.id = :idDaFamilia "
			+ "AND lc.flAtivo = br.com.larcash.enums.Confirmacao.S "			
			+ "GROUP BY lc.status "
			+ "ORDER BY lc.status")
	public List<ResumoDaLista> listarResumosPor(Integer idDaFamilia);
	
	@Modifying
	@Query(value = 
			"UPDATE ListaDeCompra lc "
			+ "SET lc.status = :status,"
			+ "    lc.dataDeMovto = :dataDeMovto "
			+ "WHERE lc.id = :idDaLista "
			+ "AND lc.familia.id = :idDaFamilia ")
	public void atualizarStatusPor(Integer idDaFamilia, Integer idDaLista, 
			StatusDaLista status, LocalDateTime dataDeMovto);
	
	@Modifying
	@Query(value = 
			"UPDATE ListaDeCompra lc "
			+ "SET lc.flNotificada = :flNotificada "
			+ "WHERE lc.id = :idDaLista ")
	public void atualizarStatusDeNotifPor(Integer idDaLista, Confirmacao flNotificada);
	
	@Modifying
	@Query(value = 
			"UPDATE ListaDeCompra lc "
			+ "SET lc.status = br.com.larcash.enums.StatusDaLista.NOVA, "
			+ "    lc.totalDaCompra = 0.0, lc.comprador.login = null, "
			+ "    lc.difDeTotais = lc.totalEstimado "
			+ "WHERE lc.id = :idDaLista "
			+ "AND lc.familia.id = :idDaFamilia ")
	public void reiniciarPor(Integer idDaFamilia, Integer idDaLista);
	
	@Modifying
	@Query(value = 
			"UPDATE ListaDeCompra lc "
			+ "SET lc.totalDaCompra = :totalDaCompra,"
			+ "    lc.totalEstimado = :totalEstimado,"
			+ "    lc.difDeTotais = :difDeTotais,"
			+ "    lc.comprador.login = :loginDoComprador,"
			+ "    lc.dataDeMovto = :dataDeMovto "
			+ "WHERE lc.id = :idDaLista "
			+ "AND lc.familia.id = :idDaFamilia ")
	public void atualizarTotaisPor(Integer idDaFamilia, Integer idDaLista, 
			BigDecimal totalDaCompra, BigDecimal totalEstimado, 
			BigDecimal difDeTotais, String loginDoComprador, 
			LocalDateTime dataDeMovto);
	
	@Query(value = 
			"SELECT Coalesce(Count(lc), 0) AS qtde "
			+ "FROM ListaDeCompra lc "
			+ "WHERE lc.familia.id = :idDaFamilia "
			+ "AND CAST(lc.dataDeMovto AS LocalDate) >= :dataDeInicio "
			+ "AND lc.status = br.com.larcash.enums.StatusDaLista.ENCERRADA ")
	public Integer contarListasPor(Integer idDaFamilia, LocalDate dataDeInicio);
	
	@Query(value = 
			"SELECT Coalesce(Sum(lc.totalEstimado), 0) AS totalEstimado, "
			+ "     Coalesce(Sum(lc.totalDaCompra), 0) AS totalComprado, "
			+ "     Coalesce(Sum(lc.totalEstimado) - Sum(lc.totalDaCompra), 0) AS totalEconomizado, "
			+ "     Coalesce(((Sum(lc.totalEstimado) - Sum(lc.totalDaCompra)) /  Sum(lc.totalEstimado)) * 100, 0) AS percEconomizado, "
			+ "     Coalesce(Count(Distinct(lc.id)), 0) AS qtdeDeCompras, "
			+ "     Coalesce(Count(il.produto.id), 0) AS qtdeDeItens, "
			+ "     Coalesce(Avg(lc.totalDaCompra), 0) AS mediaDeCompra "
			+ "FROM ListaDeCompra lc, "
			+ "     ItemDaLista il "
			+ "WHERE il.listaDeCompra = lc "
			+ "AND lc.status = br.com.larcash.enums.StatusDaLista.ENCERRADA "
			+ "AND lc.flAtivo = br.com.larcash.enums.Confirmacao.S "
			+ "AND il.flagNoCarrinho = br.com.larcash.enums.Confirmacao.S "
			+ "AND EXTRACT(YEAR FROM lc.dataDeMovto) = :ano "
			+ "AND EXTRACT(MONTH FROM lc.dataDeMovto) = :mes "
			+ "AND lc.familia.id = :idDaFamilia ")
	public TotaisDaCompra buscarTotaisPor(Integer ano, Integer mes, Integer idDaFamilia);
	
}
