package br.com.larcash.controller.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
	
	private final Map<String, String[]> parameters = new HashMap<>();
	
    private final Map<String, DiskFileItem> fileItems = new HashMap<>();
	
    private byte[] cachedRequest;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) throws IOException {

        super(request);

        if (JakartaServletFileUpload.isMultipartContent(request)) {        	
        	this.processarMulitpartContentDa(request);
		}

        this.cachedRequest = request.getInputStream().readAllBytes();
        this.processarParametrosDa(request);

    }
    
    private void processarParametrosDa(HttpServletRequest request) {
    	
    	for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
    	    String paramName = entry.getKey();
    	    String[] paramValues = entry.getValue();
    	    this.parameters.put(paramName, paramValues);
    	}

    }
    
    private void processarMulitpartContentDa(HttpServletRequest request) throws IOException {
    	
    	File repository = (File) request.getServletContext().getAttribute("jakarta.servlet.context.tempdir");
    	
    	DiskFileItemFactory factory = DiskFileItemFactory.builder()
    			.setPath(repository.getPath())
    			.get();
    	
    	JakartaServletDiskFileUpload upload = new JakartaServletDiskFileUpload(factory);

    	List<DiskFileItem> items = upload.parseRequest(request);
    	
    	for (DiskFileItem item : items) {
    		
    		if (item.isFormField()) {
    			
    			String name = item.getFieldName();
    			
                String value = item.getString(Charset.forName("UTF-8"));
                
                parameters.computeIfAbsent(name, k -> new String[0]);
         
                String[] values = parameters.get(name);
                
                String[] newValues = new String[values.length + 1];
                
                System.arraycopy(values, 0, newValues, 0, values.length);
                
                newValues[values.length] = value;
                
                parameters.put(name, newValues);
                
    		}else {
    			fileItems.put(item.getFieldName(), item);
    		}
    	}    

    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(this.cachedRequest);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.cachedRequest)));
    }
    
    @Override
    public String getParameter(String name) {
        String[] values = parameters.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(parameters);
    }
    
    public DiskFileItem getFileItem(String name) {
        return fileItems.get(name);
    }

    private static class CachedServletInputStream extends ServletInputStream {
    	
        private final ByteArrayInputStream byteArrayInputStream;

        public CachedServletInputStream(byte[] cachedRequest) {
            this.byteArrayInputStream = new ByteArrayInputStream(cachedRequest);
        }

        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            // Not required for this implementation
        }

        @Override
        public int read() {
            return byteArrayInputStream.read();
        }
    }
	
}
