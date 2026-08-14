package br.com.larcash.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CloneUtil {

	@Autowired
	private ObjectMapper objectMapper;

	public <T> T deepClone(T object, Class<T> valueType) {
		try {
			return objectMapper.readValue(objectMapper.writeValueAsString(object), valueType);
		} catch (Exception e) {
			throw new RuntimeException("Deep clone failed", e);
		}
    }
	
}
