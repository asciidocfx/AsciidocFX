package com.kodedu.other;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class ZipUtils {
	
	/**
	 * Unzips a file into the given folder.
	 * Code taken from: https://stackoverflow.com/q/9324933/2021763
	 * and also from: https://www.baeldung.com/java-compress-and-uncompress#unzip
	 * 
	 * @param fileZip
	 * @param destDir
	 * @throws IOException
	 */
	public void unzip(String fileZip, File destDir) throws IOException {
		try (ZipFile zipFile = new ZipFile(fileZip)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File entryDestination = newFile(destDir, entry);
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
	 * @return
	 * @throws IOException
	 */
	private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

}
