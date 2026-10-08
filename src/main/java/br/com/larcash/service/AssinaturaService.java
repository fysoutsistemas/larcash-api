package br.com.larcash.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.config.validation.anotacao.IdValido;
import br.com.larcash.dto.ResumoDaAssinatura;
import br.com.larcash.entity.Assinatura;
import br.com.larcash.entity.ConfiguracaoGeral;
import br.com.larcash.entity.Familia;
import br.com.larcash.entity.PagtoDaAssinatura;
import br.com.larcash.enums.Confirmacao;
import br.com.larcash.enums.TipoDeConta;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.AssinaturasRepository;
import br.com.larcash.repository.ConfiguracoesGeraisRepository;
import br.com.larcash.repository.PagtosDeAssinaturaRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Validated
public class AssinaturaService {
	
	@Autowired
	private AssinaturasRepository repository;
	
	@Autowired
	private ConfiguracoesGeraisRepository configsRepository;
	
	@Autowired
	private PagtosDeAssinaturaRepository pagtosRepository;
	
	public Assinatura criarTrialPara(
			@NotNull(message = "A família não pode ser nulo")
			Familia familia) {
		
		Assinatura assinaturaEncontrada = repository.buscarPor(familia.getId());
		
		//Busca a configuração geral do sistema para definição do periodo de trial
		ConfiguracaoGeral config = configsRepository.buscarUltima();		

		Preconditions.checkArgument(assinaturaEncontrada == null, 
				"Já existe uma assinatura");

		Assinatura trial = new Assinatura(familia, config.getDiasDeTrial());		

		return repository.save(trial);

	}
	
	public Assinatura concederCortesiaPor(
			@IdValido(nomeDoAtributo = "id da família")
			Integer idDaFamilia) {
		
		Assinatura assinaturaEncontrada = buscarPor(idDaFamilia);
		
		Preconditions.checkArgument(assinaturaEncontrada != null, 
				"Não existe assinatura para essa família");
		
		assinaturaEncontrada.concederCortesia();

		return this.repository.save(assinaturaEncontrada);
		
	}
	
	public Assinatura renovarAcessoPor(
			@IdValido(nomeDoAtributo = "id da família")
			Integer idDaFamilia,
			@NotNull(message = "O valor é obrigatório")
			@Positive(message = "O valor deve ser positivo")
			BigDecimal valorPago) {
		
		Assinatura assinaturaEncontrada = buscarPor(idDaFamilia);
		
		final LocalDateTime HOJE = LocalDateTime.now();

		//Assume 1 mês a frente considerando a data do pagamento
		LocalDateTime novaDataDeAcesso = HOJE.plusMonths(1);

		//Caso a assinatura ainda não esteja vencida, 
		//usa a data limite do acesso já salvo para adicionar 1 mês
		if (!assinaturaEncontrada.isAcessoExpirado() && assinaturaEncontrada.isMensalista()) {
			novaDataDeAcesso = assinaturaEncontrada.getAcessoAte().plusMonths(1);
		}

		PagtoDaAssinatura novoPagto = new PagtoDaAssinatura();
		novoPagto.setAssinatura(assinaturaEncontrada);
		novoPagto.setDataDePagto(HOJE);
		novoPagto.setAcessoAte(novaDataDeAcesso);

		PagtoDaAssinatura pagtoSalvo = pagtosRepository.save(novoPagto);

		if (!assinaturaEncontrada.isMensalista()) {
			assinaturaEncontrada.setTipoDeConta(TipoDeConta.MENSALISTA);
		}

		assinaturaEncontrada.setAcessoAte(novaDataDeAcesso);
		
		Assinatura assinaturaAtualizada = repository.save(assinaturaEncontrada);
		
		assinaturaAtualizada.setUltimoPagto(pagtoSalvo);
		
		return assinaturaAtualizada;
		
	}
	
	public Assinatura buscarPor(
			@IdValido(nomeDoAtributo = "id da família")
			Integer idDaFamilia) {	

		Assinatura assinaturaEncontrada = repository.buscarPor(idDaFamilia);

		Optional.ofNullable(assinaturaEncontrada)
				.orElseThrow(() -> new RegistroNaoEncontradoException(
						"Não existe assinatura vinculada ao id da família informado"));

		return assinaturaEncontrada;

	}
	
	public List<PagtoDaAssinatura> listarPagtosPor(
			@IdValido(nomeDoAtributo = "id da família")
			Integer idDaFamilia){
		
		//Se não encontra lança exceção
		this.buscarPor(idDaFamilia);

		return pagtosRepository.listarPor(idDaFamilia);

	}
	
	public ResumoDaAssinatura buscarAssinaturaPor(
			@IdValido(nomeDoAtributo = "id da família")
			Integer idDaFamilia) {
		
		Assinatura assinatura = repository.buscarPor(idDaFamilia);
		
		if (assinatura != null) {
			
			ConfiguracaoGeral config = configsRepository.buscarUltima();
			
			LocalDateTime AGORA = LocalDateTime.now();
			
			LocalDateTime acessoAte = assinatura.getAcessoAte();
			
			final BigDecimal DIA_EM_MINS = new BigDecimal(1440.0);
			
			final BigDecimal MES_EM_DIAS = new BigDecimal(30.0);
			
			final BigDecimal TRIAL_EM_DIAS = new BigDecimal(config.getDiasDeTrial());
			
			long minutos = Duration.between(AGORA, acessoAte).toMinutes();
			
			Integer diasRestantes = new BigDecimal(minutos)
					.divide(DIA_EM_MINS, 1, RoundingMode.HALF_EVEN)					
					.intValue();
			
			Integer percRestante = 0;
			
			if (assinatura.isMensalista()) {				
				percRestante = new BigDecimal(diasRestantes)
						.divide(MES_EM_DIAS, 2, RoundingMode.HALF_EVEN)
						.multiply(new BigDecimal(100))
						.intValue();		
			}else if (assinatura.isTrial()){
				percRestante = new BigDecimal(diasRestantes)
						.divide(TRIAL_EM_DIAS, 2, RoundingMode.HALF_EVEN)
						.multiply(new BigDecimal(100))
						.intValue();
			}
			
			ResumoDaAssinatura resumo = new ResumoDaAssinatura();
			resumo.setTipo(assinatura.getTipoDeConta());
			resumo.setTelefoneDeAtendimento(config.getTelefoneDeAtendimento());
			resumo.setVencimento(assinatura.getAcessoAte());
			
			if (diasRestantes > 0) {
				resumo.setDiasRestantes(diasRestantes);
				resumo.setPercRestante(percRestante);
				resumo.setFlExpirada(Confirmacao.N);
			}else {
				resumo.setDiasRestantes(0);
				resumo.setPercRestante(100);
				resumo.setFlExpirada(Confirmacao.S);
			}
			
			return resumo;

		}

		return null;

	}

}
