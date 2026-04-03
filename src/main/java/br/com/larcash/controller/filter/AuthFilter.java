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

import br.com.larcash.entity.Usuario;
import br.com.larcash.exception.AutorizacaoException;
import br.com.larcash.exception.ConverterException;
import br.com.larcash.exception.ErroDaApi;
import br.com.larcash.exception.ErrorConverter;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter{

	private final String ENDPOINT_LOGIN = "/auth";
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired	
	private ErrorConverter errorConverter;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
			FilterChain filterChain) throws ServletException, IOException {

		try {
			
			//Cria um cache com a cópia do request para poder manipular
			CustomHttpServletRequestWrapper requestCache = new CustomHttpServletRequestWrapper(request);
			
			String pathDoEndpoint = requestCache.getRequestURI();
			
			if (!ENDPOINT_LOGIN.equals(pathDoEndpoint)) {
				
				String authHeader = requestCache.getHeader("Authorization");
				
				if (authHeader != null && authHeader.startsWith("Bearer ")) {
					
				    String token = authHeader.substring(7);
				    
				    String dadosDoToken[] = new String(Base64.getDecoder()
				    		.decode(token.getBytes())).split(",");
				    
				    Preconditions.checkArgument(dadosDoToken.length == 2, "Token inválido");
				    
				    String login = dadosDoToken[0];
				    
				    Usuario usuarioEncontrado = usuarioService.buscarPorLogin(login);
				    
				    Preconditions.checkArgument(token.equals(usuarioEncontrado
				    		.getUltimoToken()), "Token inválido");
				    
				    Long validadeInMillis = Long.valueOf(dadosDoToken[1]);
				    
				    Instant instant = Instant.ofEpochMilli(validadeInMillis);
				    
				    LocalDateTime validade = LocalDateTime.ofInstant(instant, 
				    		ZoneId.systemDefault());
				    
				    Preconditions.checkArgument(validade.isAfter(LocalDateTime.now()), 
				    		"Token fora do prazo de validade");

				}else {
					throw new AutorizacaoException("Token inexistente ou inválido");
				}
				
			}
			
			filterChain.doFilter(requestCache, response);
			
		}catch (AutorizacaoException ae) {
			
			JSONObject errorBody = errorConverter.criarJsonDeErro(
					ErroDaApi.ACESSO_NAO_PERMITIDO, ae.getMessage());
			
			this.retornarErroCom(response, errorBody);
			
		}catch (RegistroNaoEncontradoException ex) { 
			
			JSONObject errorBody = errorConverter.criarJsonDeErro(
					ErroDaApi.ACESSO_NAO_PERMITIDO, "Token inválido");
			
			this.retornarErroCom(response, errorBody);
			
		}catch (ConverterException ce) {
			
			JSONObject errorBody = errorConverter.criarJsonDeErro(
					ErroDaApi.BODY_INVALIDO, ce.getMessage());
			
			this.retornarErroCom(response, errorBody);
					
		}catch (IllegalArgumentException iae) {
			
			JSONObject errorBody = errorConverter.criarJsonDeErro(
					ErroDaApi.TOKEN_INVALIDO, iae.getMessage());
			
			this.retornarErroCom(response, errorBody);
		}

	}
	
	private void retornarErroCom(HttpServletResponse response, JSONObject errorBody) {
		try {		
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=UTF-8");
			response.getOutputStream().write(errorBody.toString().getBytes(StandardCharsets.UTF_8));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
