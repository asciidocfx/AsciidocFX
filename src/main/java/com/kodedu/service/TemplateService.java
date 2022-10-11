package com.kodedu.service;

import com.kodedu.component.DialogBuilder;
import com.kodedu.config.templates.AsciidocTemplateI;

import com.kodedu.helper.IOHelper;
import net.lingala.zip4j.ZipFile;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.StandardOpenOption.*;

@Component
public class TemplateService {

    private Logger logger = LoggerFactory.getLogger(TemplateService.class);

	private final ThreadService threadService;

	public TemplateService(ThreadService threadService) {
		this.threadService = threadService;
	}

	/**
	 * Depending on the location of the template downloads it or copies the file. If
	 * it is a zip-archive it should be extracted. During the process no existing
	 * files should be overwritten.
	 * 
	 * @param targetDir
	 * @throws Exception
	 */
	public final void provide(AsciidocTemplateI template, Path targetDir) throws Exception {

		final var location = template.getLocation();
		final var locationLowCase = location.toLowerCase();

		boolean isGitRepository = false;
		try {
			Collection<Ref> refs = Git.lsRemoteRepository()
					.setRemote(location)
					.call();
			if (Objects.nonNull(refs) && !refs.isEmpty()) {
				isGitRepository = true;
			}
		} catch (Exception e) {

		}

		Path filePath = getTargetFilePath(targetDir, locationLowCase, location);

		if(isGitRepository){
			if(Files.exists(filePath)){
				filePath = getNewFolder(targetDir);
			}
			Files.createDirectories(filePath);
			logger.info("Cloning template from {}", location);
			try (Git git = Git.cloneRepository()
					.setURI(locationLowCase)
					.setDirectory(filePath.toFile())
					.call();) {
			}
		} else if (locationLowCase.startsWith("http")) {
			logger.debug("Downloading template from: {}", location);
			Path zipPath = download(filePath, new URI(location));
			if (locationLowCase.endsWith(".zip")) {
				Path newFolder = getNewFolder(targetDir);
				Files.createDirectories(newFolder);
				logger.debug("Extracting zip: {}", zipPath);
				new ZipFile(zipPath.toFile()).extractAll(newFolder.toString());
				squeezeFolder(newFolder);
				IOHelper.deleteIfExists(zipPath);
			}
		} else {
			Path locationPath = Paths.get(location);
			if (Files.exists(locationPath)) {
				logger.debug("Coping template from: {}", location);
				Path newFolder = getNewFolder(targetDir);
				Files.createDirectories(newFolder);
				IOHelper.copyDirectory(locationPath, newFolder);
			} else {
				logger.error("Template not exist in the provided path: {}", locationPath);
			}
		}

		logger.debug("Template provided: {}", location);
	}

	private Path getTargetFilePath(Path targetDir, String location, final String locationLowCase) {
		if (locationLowCase.startsWith("http:") || locationLowCase.startsWith("https:")) {
			// remove only first colon, as we are not interested in it
			// colons are not allowed in WindowsPath, on PosixPath they are allowed
			location = location.replaceFirst(":", "");
		}
		Path locationPath = Paths.get(location);
		Path fileName = locationPath.getFileName();
		return targetDir.resolve(fileName);
	}

	private void squeezeFolder(Path newFolder) throws IOException {
		try(Stream<Path> pathStream = Files.list(newFolder);){
			List<Path> extractedFiles = pathStream.collect(Collectors.toList());
			if (extractedFiles.size() == 1) {
				Path path = extractedFiles.get(0);
				if (Files.isDirectory(path)) {
					Path tempDirectory = Files.createTempDirectory("TemplateTempDir");
					Files.move(path, tempDirectory, StandardCopyOption.REPLACE_EXISTING);
					Files.move(tempDirectory, newFolder, StandardCopyOption.REPLACE_EXISTING);
					IOHelper.deleteDirectory(tempDirectory);
				}
			}
		}
	}

	private Path getNewFolder(Path targetDir) {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		final Path[] folder = new Path[1];
			threadService.runActionLater(()->{
				try{
					DialogBuilder dialog = DialogBuilder.newFolderDialog();
					dialog.showAndWait().map(String::trim).ifPresent(folderName -> {
						if (dialog.isShowing()) {
							dialog.hide();
						}
						if (folderName.matches(DialogBuilder.FOLDER_NAME_REGEX)) {
							folder[0] = targetDir.resolve(folderName);
							countDownLatch.countDown();
						}
					});
				} catch (Exception e) {
					logger.error(e.getMessage());
					countDownLatch.countDown();
				}
			});
		try {
			countDownLatch.await(2, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RuntimeException("Couldn't provide folder name in given time", e);
		}
		if (folder.length == 0 || Objects.isNull(folder[0])) {
			throw new RuntimeException("Folder name is required");
		}
		return folder[0];
	}

	private Path download(Path target, URI location) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
		                                 .uri(location)
		                                 .GET()
		                                 .build();

		var client = HttpClient.newBuilder()
		                       .followRedirects(Redirect.ALWAYS)
		                       .build();

		String path = location.getPath();
		String lastSegment = path.substring(path.lastIndexOf('/') + 1);

		HttpResponse<Path> response = client.send(request,
		                                          BodyHandlers.ofFile(target.getParent().resolve(lastSegment),
														  CREATE, WRITE, TRUNCATE_EXISTING));
		return response.body();

	}
}
