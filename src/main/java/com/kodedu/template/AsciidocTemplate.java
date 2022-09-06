package com.kodedu.template;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AsciidocTemplate implements MetaAsciidocTemplateI {
    private Logger logger = LoggerFactory.getLogger(AsciidocTemplate.class);

    private static final Set<String> SUPPORTED_PROTOCOLS = Set.of("http", "https", "file");
    
	@Override
	public void furnish(Path directory) {
		URI location;
		try {
			location = getLocationUri();
		} catch (URISyntaxException e) {
			logger.error("Location %s is unkown. Can not proceed with template.".formatted(getLocation()));
			return;
		}
		
		String protocol = location.getScheme();
		if(protocol == null && !SUPPORTED_PROTOCOLS.contains(protocol.toLowerCase())){
			String msg = "Protocol of location %s is unkown. Supported Protocols are: %s.".formatted(getLocation(),
			                                                                                         String.join(", " ,SUPPORTED_PROTOCOLS));
			logger.error(msg);
			return;
		}
		
		Path resource;
		if(protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https")) {
			resource = download(directory, location);
		} else {
			resource = Path.of(location);
		}
		
		

		
	}
	
	private URI getLocationUri() throws URISyntaxException {
		var location = getLocation();
		try {
			return new URI(location);
		} catch (URISyntaxException ex) {
			Path path = Path.of(location);
			if (Files.exists(path)) {
				return path.toUri();
			}
			throw ex;
		}

	}

	private Path download(Path directory, URI location) {
		HttpRequest request = HttpRequest.newBuilder()
				  .uri(location)
				  .GET()
				  .build();
		
		var client = HttpClient.newBuilder()
				.followRedirects(Redirect.ALWAYS)
				.build();
		try {
			
			String path = location.getPath();
			String lastSegment = path.substring(path.lastIndexOf('/') + 1);
			
			var target = directory.resolve(lastSegment);
			HttpResponse<Path> response = client.send(request,
			            BodyHandlers.ofFile(target));
			System.out.println(response.statusCode());
			return response.body();
		} catch (IOException | InterruptedException e) {
			
			return null;
		}
	}
	
	

}
