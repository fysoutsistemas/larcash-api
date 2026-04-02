package br.com.larcash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.Categoria;
import br.com.larcash.enums.Status;

@Repository
public interface CategoriasRepository extends JpaRepository<Categoria, Integer>{
	
	@Query(value = 
			"SELECT c "
			+ "FROM Categoria c "
			+ "WHERE c.status = :status "
			+ "ORDER BY c.id ")
	public List<Categoria> listarPor(Status status);

}
