package com.kodedu.service;

import com.kodedu.config.templates.AsciidocTemplateI;

import org.springframework.stereotype.Component;

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

@Component
public class TemplateService {

    private Logger logger = LoggerFactory.getLogger(TemplateService.class);

	/**
	 * Depending on the location of the template downloads it or copies the file. If
	 * it is a zip-archive it should be extracted. During the process no existing
	 * files should be overwritten.
	 * 
	 * @param targetDir
	 * @param zipUtils
	 * @throws Exception
	 */
	public final void provide(AsciidocTemplateI template, Path targetDir, UnzipService zipUtils) throws Exception {

		final var location = template.getLocation();
		var locationLowCase = location.toLowerCase();

		Path templateInTarget;
		if (locationLowCase.startsWith("http")) {
			logger.debug("Downloading template from: {}", location);
			templateInTarget = download(targetDir, new URI(location));
		} else {
			logger.debug("Coping template from: {}", location);
			var locPath = Path.of(location);
			templateInTarget = targetDir.resolve(locPath.getFileName());
			Files.copy(locPath, templateInTarget);
		}

		if (locationLowCase.endsWith(".zip")) {
			String zipFile = templateInTarget.toAbsolutePath().toString();
			logger.debug("Extracting zip: {}", zipFile);
			zipUtils.unzip(zipFile, targetDir.toFile(), true, true);
			Files.delete(templateInTarget);
		}
		logger.debug("Template provided: {}", location);
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
