package br.com.larcash.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.dto.SenhaResetada;
import br.com.larcash.entity.ResetDeSenha;
import br.com.larcash.entity.Usuario;
import br.com.larcash.enums.Confirmacao;
import br.com.larcash.repository.ResetsDeSenhasRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service
@Validated
public class ResetDeSenhaService {

	@Autowired
	private ResetsDeSenhasRepository repository;
	
	@Autowired
	private NotificacaoService notificacaoService;

	@Autowired
	private UsuarioService usuarioService;
	
	@Transactional
	public ResetDeSenha gerarCodigoPor(
			@NotBlank(message = "O login é obrigatório")
			String login) {
		
		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(login);
		
		final Integer LIMITE = 1000000;
		
		final Integer MAX_RETRIES = 100;
		
		final Long VALIDADE_EM_MINUTOS = 30L;
		
		final Long PROX_ENVIO_EM_SEGS = 90L;
		
		final LocalDateTime AGORA = LocalDateTime.now();
		
		Random gerador = new Random();
		
		String codigoGerado = String.format("%06d", gerador.nextInt(LIMITE));
		
		ResetDeSenha reset = repository.buscarPor(login);
		
		if (reset == null) {
			reset = new ResetDeSenha();
			reset.setLogin(login);
			reset.setCodigoOTP(codigoGerado);
		}else {
			
			Preconditions.checkArgument(AGORA.isAfter(reset.getProximoEnvio()),
					"É preciso aguardar a liberação do próximo envio");
			
			if (reset.getCodigoOTP().equals(codigoGerado)) {
				
				int nroTentativa = 1;					
				
				while (reset.getCodigoOTP().equals(codigoGerado)) {
					
					if (nroTentativa <= MAX_RETRIES) {
						codigoGerado = String.format("%06d", gerador.nextInt(LIMITE));
						nroTentativa++;
					}
					
					Preconditions.checkArgument(nroTentativa <= MAX_RETRIES, 
							"Timeout na geração do código de ativação");

				}
				
			}
			
			reset.setDataDeMovto(AGORA);

			reset.setCodigoOTP(codigoGerado);
			
		}
		
		reset.setTelefone(usuarioEncontrado.getTelefone());
		
		LocalDateTime validade = AGORA.plusMinutes(VALIDADE_EM_MINUTOS); 

		reset.setValidoAte(validade);
		
		reset.setProximoEnvio(AGORA.plusSeconds(PROX_ENVIO_EM_SEGS));
		
		reset.setFlResetada(Confirmacao.N);

		reset = repository.save(reset);
		
		StringBuilder msg = new StringBuilder();
		msg.append("⚠️ Redefinição de senha solicitada\n\n");
		msg.append("Olá! Recebemos um pedido para alterar a senha da sua conta no Larca$h. ");	
		msg.append(" Aqui está o seu código de segurança:\n\n");	
		msg.append("🔢 Código: *").append(codigoGerado).append("*\n");
		msg.append("⏳ Válido por: *").append(VALIDADE_EM_MINUTOS).append(" minutos*\n\n");	
		msg.append("Para a sua proteção, nossa equipe nunca pedirá esse código por telefone ou mensagem. ");
		msg.append("Se não foi você quem pediu, apenas desconsidere esta mensagem.\n\n");	
		msg.append("Abraços, \n");	
		msg.append("Equipe Larca$h 🚀");
		
		this.notificacaoService.enviarMsgPor(usuarioEncontrado
				.getTelefone(), msg.toString());
		
		return reset;
		
	}
	
	public ResetDeSenha validarCodigoPor(
			@NotBlank(message = "O login é obrigatório")
			String login,
			@NotBlank(message = "O código é obrigatório")
			String codigo) {

		ResetDeSenha reset = repository.buscarPor(login);

		Preconditions.checkArgument(reset != null, "Código inexistente");

		Preconditions.checkArgument(codigo.equals(
				reset.getCodigoOTP()), "Còdigo inválido");

		Preconditions.checkArgument(!reset.isResetada(), "O código já foi utilizado");

		return reset;

	}
	
	@Transactional
	public void processar(
			@Valid
			@NotNull(message = "A senha resetada é obrigatória")
			SenhaResetada senhaResetada) {
		
		ResetDeSenha resetEncontrado = validarCodigoPor(senhaResetada.getLogin(), 
				senhaResetada.getCodigoOTP());
		
		this.usuarioService.atualizarSenhaPor(senhaResetada.getLogin(), 
				senhaResetada.getNovaSenha());
		
		resetEncontrado.setDataDeMovto(LocalDateTime.now());
		
		resetEncontrado.setFlResetada(Confirmacao.S);
		
		this.repository.save(resetEncontrado);

	}
	
}
