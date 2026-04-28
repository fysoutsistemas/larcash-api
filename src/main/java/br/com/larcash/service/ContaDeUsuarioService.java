package br.com.larcash.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import br.com.larcash.dto.ContaDeUsuarioEditada;
import br.com.larcash.dto.NovaContaDeUsuario;
import br.com.larcash.dto.ResumoDaContaDeUsuario;
import br.com.larcash.entity.Familia;
import br.com.larcash.entity.Orcamento;
import br.com.larcash.entity.Usuario;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service
@Validated
public class ContaDeUsuarioService {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private FamiliaService familiaService;
		
	@Autowired
	private OrcamentoService orcamentoService;
	
	@Transactional
	public void criar(
			@Valid
			@NotNull(message = "A nova conta não pode ser nula")
			NovaContaDeUsuario novaConta) {
		
		Familia novaFamilia = new Familia();
		novaFamilia.setNome(novaConta.getNomeDaFamilia());
		
		Familia familiaSalva = familiaService.inserir(novaFamilia);
		
		Orcamento novoOrcamento = new Orcamento();		
		novoOrcamento.setLimite(novaConta.getOrcamentoMensal());
		novoOrcamento.setFamilia(familiaSalva);

		this.orcamentoService.inserir(novoOrcamento);

		Usuario novoUsuario = new Usuario();
		novoUsuario.setLogin(novaConta.getLogin());
		novoUsuario.setSenha(novaConta.getSenha());
		novoUsuario.setNomeCompleto(novaConta.getNomeCompleto());
		novoUsuario.setFamilia(familiaSalva);

		this.usuarioService.inserir(novoUsuario);

	}
	
	@Transactional
	public ResumoDaContaDeUsuario atualizar(
			@NotNull(message = "A conta editada não pode ser nulo")
			ContaDeUsuarioEditada contaEditada) {
		
		Usuario usuarioAtualizado = usuarioService.atualizarPor(contaEditada.getLogin(), 
				contaEditada.getNomeCompleto(), contaEditada.getSenhaAtual(), 
				contaEditada.getNovaSenha());
		
		Familia familiaDoUsuario = usuarioAtualizado.getFamilia();
		familiaDoUsuario.setNome(contaEditada.getNomeDaFamilia());		
		this.familiaService.alterar(familiaDoUsuario);		
		
		return buscarResumoPor(usuarioAtualizado.getLogin());
		
	}
	
	public ResumoDaContaDeUsuario buscarResumoPor(
			@NotBlank(message = "O login é obrigatório")
			String login) {
		
		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(login);
		
		ResumoDaContaDeUsuario resumo = new ResumoDaContaDeUsuario();
		
		resumo.setLogin(usuarioEncontrado.getLogin());
		resumo.setNomeCompleto(usuarioEncontrado.getNomeCompleto());
		resumo.setNomeDaFamilia(usuarioEncontrado.getFamilia().getNome());
		
		return resumo;

	}
	
}
