package com.kodedu.template;

import com.kodedu.service.UnzipService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AsciidocTemplate implements MetaAsciidocTemplateI {
    private Logger logger = LoggerFactory.getLogger(AsciidocTemplate.class);

    
	@Override
	public final void provide(Path targetDir, UnzipService zipUtils) throws Exception {

		var locationLowCase = getLocation().toLowerCase();

		Path templateInTarget;
		if (locationLowCase.startsWith("http")) {
			logger.debug("Downloading template from: {}", getLocation());
			templateInTarget = download(targetDir, new URI(getLocation()));
		} else {
			logger.debug("Coping template from: {}", getLocation());
			var locPath = Path.of(getLocation());
			templateInTarget = targetDir.resolve(locPath.getFileName());
			Files.copy(locPath, templateInTarget);
		}

		if (locationLowCase.endsWith(".zip")) {
			String zipFile = templateInTarget.toAbsolutePath().toString();
			logger.debug("Extracting zip: {}", zipFile);
			zipUtils.unzip(zipFile, targetDir.toFile(), true, true);
			Files.delete(templateInTarget);
		}
		logger.debug("Template provided: {}", getLocation());
	}

	private Path download(Path directory, URI location) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
		                                 .uri(location)
		                                 .GET()
		                                 .build();

		var client = HttpClient.newBuilder()
		                       .followRedirects(Redirect.ALWAYS)
		                       .build();

		String path = location.getPath();
		String lastSegment = path.substring(path.lastIndexOf('/') + 1);

		var target = directory.resolve(lastSegment);
		HttpResponse<Path> response = client.send(request,
		                                          BodyHandlers.ofFile(target,
		                                                              StandardOpenOption.CREATE_NEW,
		                                                              StandardOpenOption.WRITE));
		return response.body();

	}
	
	

}
