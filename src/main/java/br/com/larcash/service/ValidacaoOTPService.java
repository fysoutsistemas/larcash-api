package br.com.larcash.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.entity.ValidacaoOTP;
import br.com.larcash.enums.Confirmacao;
import br.com.larcash.repository.ValidacoesOTPRepository;
import br.com.larcash.util.PhoneUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;

@Service
@Validated
public class ValidacaoOTPService {
	
	@Autowired
	private ValidacoesOTPRepository repository;
	
	@Autowired
	private NotificacaoService notificacaoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private PhoneUtil phoneUtil;

	@Transactional
	public ValidacaoOTP gerarCodigoPor(
			@NotBlank(message = "O login é obrigatório")
			String login, 
			@NotBlank(message = "O telefone é obrigatório")
			String telefone) {
		
		boolean isNovoLogin = !usuarioService.isExiste(login);

		Preconditions.checkArgument(isNovoLogin, "O login já existe");
		
		final Integer LIMITE = 1000000;
		
		final Integer MAX_RETRIES = 100;
		
		final Long VALIDADE_EM_MINUTOS = 30L;
		
		final Long PROX_ENVIO_EM_SEGS = 45L;
		
		final LocalDateTime AGORA = LocalDateTime.now();
		
		telefone = phoneUtil.removerMascaraDo(telefone);
					
		Random gerador = new Random();
		
		String codigoGerado = String.format("%06d", gerador.nextInt(LIMITE));
		
		ValidacaoOTP validacao = buscarPor(telefone);			
		
		if (validacao == null) {
			validacao = new ValidacaoOTP();
			validacao.setTelefone(telefone);
			validacao.setCodigo(codigoGerado);
		}else {
			
			Preconditions.checkArgument(!validacao.isAtivada(), 
					"O numero já está ativado");
			
			Preconditions.checkArgument(AGORA.isAfter(validacao.getProximoEnvio()),
					"É preciso aguardar a liberação do próximo envio");

			if (validacao.getCodigo().equals(codigoGerado)) {
				
				int nroTentativa = 1;					
				
				while (validacao.getCodigo().equals(codigoGerado)) {
					
					if (nroTentativa <= MAX_RETRIES) {
						codigoGerado = String.format("%06d", gerador.nextInt(LIMITE));
						nroTentativa++;
					}
					
					Preconditions.checkArgument(nroTentativa <= MAX_RETRIES, 
							"Timeout na geração do código de ativação");

				}

			}

			validacao.setDataDeMovto(AGORA);

			validacao.setCodigo(codigoGerado);

		}		
		
		LocalDateTime validade = AGORA.plusMinutes(VALIDADE_EM_MINUTOS); 

		validacao.setValidoAte(validade);
		
		validacao.setProximoEnvio(AGORA.plusSeconds(PROX_ENVIO_EM_SEGS));

		validacao = repository.save(validacao);
		
		StringBuilder msg = new StringBuilder();
		msg.append("🔐 Ativação de conta Larca$h\n\n");
		msg.append("Seu cadastro está quase concluído. Para validar seu acesso, ");
		msg.append("utilize o código de segurança abaixo no aplicativo: \n\n");
		msg.append("🔢 Código: *").append(codigoGerado).append("*\n");
		msg.append("⏳ Válido por: *").append(VALIDADE_EM_MINUTOS).append(" minutos*\n\n");
		msg.append("Se você não solicitou este código, por favor, desconsidere esta mensagem.\n\n");
		msg.append("Boas-vindas ao Larcash! 🚀");
		
		this.notificacaoService.enviarMsgPor(telefone, msg.toString());

		return validacao;

	}
	
	public void validarCodigoPor(
			@NotBlank(message = "O telefone é obrigatório")
			String telefone, 
			@NotBlank(message = "O código é obrigatório")
			String codigo) {
		
		telefone = phoneUtil.removerMascaraDo(telefone);
		
		ValidacaoOTP validacao = buscarPor(telefone);

		Preconditions.checkArgument(validacao != null, 
				"Código inexistente");

		Preconditions.checkArgument(validacao.getCodigo().equals(codigo), 
				"Código inválido");
		
		final LocalDateTime AGORA = LocalDateTime.now();
		
		boolean isValido = AGORA.isBefore(validacao.getValidoAte());

		Preconditions.checkArgument(isValido, "O código expirou");
		
		validacao.setDataDeMovto(AGORA);
		
		validacao.setFlAtivada(Confirmacao.S);
		
		this.repository.save(validacao);

	}
	
	public ValidacaoOTP buscarPor(
			@NotBlank(message = "O telefone é obrigatório")
			String telefone) {
		return repository.buscarPor(telefone);
	}

}
