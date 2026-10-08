package br.com.larcash.controller.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.base.Preconditions;

import br.com.larcash.entity.Administrador;
import br.com.larcash.entity.Usuario;
import br.com.larcash.exception.AutorizacaoException;
import br.com.larcash.exception.ConverterException;
import br.com.larcash.exception.ErroDaApi;
import br.com.larcash.exception.ErrorConverter;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.service.AdminService;
import br.com.larcash.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter{

	private final String ENDPOINT_LOGIN = "/auth",
						 ENDPOINT_LOGIN_ADMIN = "/auth/admin",
			             ENDPOINT_STATUS_API = "/actuator",
			             ENDPOINT_REGISTRO_CONTAS = "/convites/registrar",
			             ENDPOINT_CONVITE = "/contas-usuarios/registrar",
			             ENDPOINT_CATEGORIA = "/categorias",
			             ENDPOINT_ORCAMENTO = "/orcamentos",
			             ENDPOINT_OTP = "/validacoes-otp",
			             ENDPOINT_RESET_SENHA = "/reset-senha",
			             PATH_ASSINATURA = "assinatura",
			             METODO_POST = "POST",
			             METODO_PUT = "PUT";
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired	
	private ErrorConverter errorConverter;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
			FilterChain filterChain) throws ServletException, IOException {

		try {
			
			// Libera requisições Preflight (OPTIONS) sem exigir token de autenticação
		    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
		        response.setStatus(HttpServletResponse.SC_OK);
		        filterChain.doFilter(request, response);
		        return;
		    }
			
			//Cria um cache com a cópia do request para poder manipular
			CustomHttpServletRequestWrapper requestCache = new CustomHttpServletRequestWrapper(request);
			
			String pathDoEndpoint = requestCache.getRequestURI();
			
			String metodo = requestCache.getMethod();
			
			String authHeader = requestCache.getHeader("Authorization");
			
			if (pathDoEndpoint.contains(PATH_ASSINATURA)) {
				this.validarTokenPresenteNo(authHeader, true);
			}else {
				
				if (!ENDPOINT_REGISTRO_CONTAS.equals(pathDoEndpoint)
						&& !ENDPOINT_CONVITE.equals(pathDoEndpoint)
						&& !ENDPOINT_LOGIN.equals(pathDoEndpoint)
						&& !ENDPOINT_LOGIN_ADMIN.equals(pathDoEndpoint)
						&& !pathDoEndpoint.startsWith(ENDPOINT_OTP)
						&& !pathDoEndpoint.startsWith(ENDPOINT_RESET_SENHA)
						&& !pathDoEndpoint.startsWith(ENDPOINT_STATUS_API)) {

					String tokenValido = validarTokenPresenteNo(authHeader, false);

					if (pathDoEndpoint.startsWith(ENDPOINT_CATEGORIA)
							|| pathDoEndpoint.startsWith(ENDPOINT_ORCAMENTO)) {
						
						String login = new String(Base64.getDecoder()
								.decode(tokenValido.getBytes())).split(",")[0];

						Usuario usuarioEncontrado = usuarioService.buscarPorLogin(login);

						if (METODO_POST.equalsIgnoreCase(metodo) 
								|| METODO_PUT.equalsIgnoreCase(metodo)) {

							Preconditions.checkArgument(usuarioEncontrado.isChefeDeFamilia(), 
									"O login não possui nível de acesso ao recurso de destino ");

						}

					}																		

				}

			}

			filterChain.doFilter(requestCache, response);
			
		}catch (AutorizacaoException ae) {
			
			JSONObject errorBody = errorConverter.criarJsonDeErro(
					ErroDaApi.ACESSO_NAO_PERMITIDO, ae.getMessage());
			
			this.retornarErroCom(HttpStatus.UNAUTHORIZED, response, errorBody);
			
		}catch (RegistroNaoEncontradoException ex) { 
			
			JSONObject errorBody = errorConverter.criarJsonDeErro(
					ErroDaApi.ACESSO_NAO_PERMITIDO, "Token inválido");
			
			this.retornarErroCom(HttpStatus.UNAUTHORIZED, response, errorBody);
			
		}catch (ConverterException ce) {
			
			JSONObject errorBody = errorConverter.criarJsonDeErro(
					ErroDaApi.BODY_INVALIDO, ce.getMessage());
			
			this.retornarErroCom(HttpStatus.BAD_REQUEST, response, errorBody);
					
		}catch (IllegalArgumentException iae) {
			
			JSONObject errorBody = errorConverter.criarJsonDeErro(
					ErroDaApi.TOKEN_INVALIDO, iae.getMessage());
			
			this.retornarErroCom(HttpStatus.UNAUTHORIZED, response, errorBody);

		}

	}
	
	private String validarTokenPresenteNo(String header, boolean isAdminToken) {			
		
		if (header != null && header.startsWith("Bearer ")) {
			
			String token = header.substring(7);
			
			String dadosDoToken[] = new String(Base64.getDecoder()
					.decode(token.getBytes())).split(",");
			
			int qtdeDeDados = isAdminToken ? 2 : 4;
			
			Preconditions.checkArgument(dadosDoToken.length == qtdeDeDados, "Token inválido");

			String login = dadosDoToken[0];
			
			String ultimoToken = null; 
					
			if (isAdminToken) {
				Administrador adminEncontrado = adminService.buscarPorLogin(login);				
				ultimoToken = adminEncontrado.getUltimoToken();				
			}else{
				Usuario usuarioEncontrado = usuarioService.buscarPorLogin(login);				
				ultimoToken = usuarioEncontrado.getUltimoToken();
			}
			
			Preconditions.checkArgument(token.equals(ultimoToken), "Token inválido");

			Long validadeInMillis = Long.valueOf(dadosDoToken[1]);
			
			Instant instant = Instant.ofEpochMilli(validadeInMillis);

			LocalDateTime validade = LocalDateTime.ofInstant(instant, 
					ZoneId.systemDefault());

			Preconditions.checkArgument(validade.isAfter(LocalDateTime.now()), 
					"Token fora do prazo de validade");
			
			//Se o token não for de administrador é preciso validar 
			//se a assinatura não está expirada
			if (!isAdminToken) {
				
				validadeInMillis = Long.valueOf(dadosDoToken[3]);
				
				instant = Instant.ofEpochMilli(validadeInMillis);
				
				validade = LocalDateTime.ofInstant(instant, 
						ZoneId.systemDefault());
				
				Preconditions.checkArgument(validade.isAfter(LocalDateTime.now()), 
						"A assinatura expirou");
				
			}

			return token;

		}else {
			throw new AutorizacaoException("Token inexistente ou inválido");
		}		

	}
	
	private void retornarErroCom(HttpStatus httpStatus, 
			HttpServletResponse response, JSONObject errorBody) {
		
		try {		
			response.setStatus(httpStatus.value());
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=UTF-8");
			response.getOutputStream().write(errorBody.toString().getBytes(StandardCharsets.UTF_8));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
