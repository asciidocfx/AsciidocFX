package com.kodedu.service;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class UnzipService {
	
	static final String ZIP_SLIP_ERROR_MSG = "Entry is outside of the target dir: ";

	/**
	 * Unzips a file into the given folder. Code taken from:
	 * https://stackoverflow.com/q/9324933/2021763 and also from:
	 * https://www.baeldung.com/java-compress-and-uncompress#unzip
	 * 
	 * @param fileZip
	 * @param destDir
	 * @param skipRoot  on true the root folder (if one exists) will be ignored e.g.
	 *                  each file and folder move one folder up in the hierarchy
	 * @param createNew on true the extracted files must be created newly. Fails if
	 *                  a file already exists.
	 * @throws IOException
	 */
	public void unzip(String fileZip, File destDir, boolean skipRoot, boolean createNew) throws IOException {
		try (ZipFile zipFile = new ZipFile(fileZip)) {
			int cutOff = skipRoot ? determineRootFolderNameLength(zipFile) : 0;

			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File entryDestination = newFile(destDir, entry, cutOff, createNew);
				if (entryDestination == null) {
					continue;
				}
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
				} else {
					entryDestination.getParentFile().mkdirs();
					try (InputStream in = zipFile.getInputStream(entry);
					        OutputStream out = new FileOutputStream(entryDestination)) {
						in.transferTo(out);
					}
				}
			}
		}
	}

	/**
	 * Needed to avoid ZipSlip. 
	 * https://security.snyk.io/research/zip-slip-vulnerability
	 * 
	 * In short: files must not be extracted outside of target folder. 
	 * 
	 * @param destinationDir
	 * @param zipEntry
	 * @param cutOff 
	 * @param createNew 
	 * @return the File to write to. Might be null, when the root folder is skipped
	 * @throws IOException
	 */
	private File newFile(File destinationDir, ZipEntry zipEntry, int cutOff, boolean createNew) throws IOException {
		String entryName = zipEntry.getName();
		entryName = entryName.substring(cutOff);
		if (entryName.isEmpty()) {
			return null;
		}

		File destFile = new File(destinationDir, entryName);

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException(ZIP_SLIP_ERROR_MSG + entryName);
		}
		if(createNew && destFile.exists()) {
			throw new FileAlreadyExistsException(destFile.getAbsolutePath());
		}

		return destFile;
	}

	/**
	 * Find a common name prefix (stops with first /) of all files in the zip archive.
	 * 
	 * 
	 * @param zipFile2
	 * @return
	 * @throws IOException
	 */
	private int determineRootFolderNameLength(ZipFile zipFile) throws IOException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		String prefix = null;
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String[] parts = entry.getName().split("/", 2);
			if (prefix == null) {
				prefix = parts[0];
			} else if (!prefix.equals(parts[0])) {
				prefix = null;
				break;
			}
		}
		
		if (prefix != null) {
			// split removes the '/', therefore + 1
			return prefix.length() + 1;
		}
		return 0;
	}

}
