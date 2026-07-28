package br.com.larcash.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.ListaDeCompra;
import br.com.larcash.enums.StatusDaLista;

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
			+ "AND lc.status <> br.com.larcash.enums.StatusDaLista.ENCERRADA "
			+ "ORDER BY lc.id ")
	public List<ListaDeCompra> listarPor(Integer idDaFamilia);
	
	@Modifying
	@Query(value = 
			"UPDATE ListaDeCompra lc SET lc.status = :status "
			+ "WHERE lc.id = :idDaLista "
			+ "AND lc.familia.id = :idDaFamilia ")
	public void atualizarStatusPor(Integer idDaFamilia, Integer idDaLista, StatusDaLista status);
	
	@Modifying
	@Query(value = 
			"UPDATE ListaDeCompra lc "
			+ "SET lc.totalDaCompra = :totalDaCompra,"
			+ "    lc.totalEstimado = :totalEstimado,"
			+ "    lc.difDeTotais = :difDeTotais,"
			+ "    lc.comprador.login = :loginDoComprador "
			+ "WHERE lc.id = :idDaLista "
			+ "AND lc.familia.id = :idDaFamilia ")
	public void atualizarTotaisPor(Integer idDaFamilia, Integer idDaLista, 
			BigDecimal totalDaCompra, BigDecimal totalEstimado, 
			BigDecimal difDeTotais, String loginDoComprador);	
	
}
