package com.kodedu.template;

import com.kodedu.other.ZipUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AsciidocTemplate implements MetaAsciidocTemplateI {
    private Logger logger = LoggerFactory.getLogger(AsciidocTemplate.class);

    
	@Override
	public void provide(Path targetDir, ZipUtils zipUtils) throws Exception {

		var locationLowCase = getLocation().toLowerCase();

		Path templateInTarget;
		if (locationLowCase.startsWith("http")) {
			templateInTarget = download(targetDir, new URI(getLocation()));
		} else {
			var locPath = Path.of(getLocation());
			templateInTarget = targetDir.resolve(locPath.getFileName());
			Files.copy(locPath, templateInTarget);
		}

		if (locationLowCase.endsWith(".zip")) {
			zipUtils.unzip(templateInTarget.toAbsolutePath().toString(), targetDir.toFile(), true);
			Files.delete(templateInTarget);
		}
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
		                                          BodyHandlers.ofFile(target));
		return response.body();

	}
	
	

}
