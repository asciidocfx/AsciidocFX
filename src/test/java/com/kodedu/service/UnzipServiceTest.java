package com.kodedu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UnzipServiceTest {

	private UnzipService zipUtils;
	
	@BeforeEach
	public void setUp() {
		zipUtils = new UnzipService();
	}
	
	@Test
	public void testUnzip_flatZip(@TempDir Path targetDir) throws IOException {
		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/flat_files.zip", targetDir.toFile(), false, true);

		Path file = targetDir.resolve("1.txt");
		assertThat(file).exists();
		file = targetDir.resolve("2.txt");
		assertThat(file).exists();
		file = targetDir.resolve("3.txt");
		assertThat(file).exists();
	}
	
	@Test
	public void testUnzip_unzipTwice_overwrite(@TempDir Path targetDir) throws IOException {
		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/flat_files.zip", targetDir.toFile(), false, false);
		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/flat_files.zip", targetDir.toFile(), false, false);

		Path file = targetDir.resolve("1.txt");
		assertThat(file).exists();
		file = targetDir.resolve("2.txt");
		assertThat(file).exists();
		file = targetDir.resolve("3.txt");
		assertThat(file).exists();
	}
	
	@Test
	public void testUnzip_unzipTwice_noOverwrite(@TempDir Path targetDir) throws IOException {
		assertThatThrownBy(() -> {
		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/flat_files.zip", targetDir.toFile(), false, true);
		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/flat_files.zip", targetDir.toFile(), false, true);
		}).isInstanceOf(FileAlreadyExistsException.class);
	}
	
	@Test
	public void testUnzip_zipSlip(@TempDir Path targetDir) throws IOException {
		// prepared zip file taken from:
		// https://github.com/snyk/zip-slip-vulnerability/tree/master/archives
		assertThatThrownBy(() -> {
			zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/zip-slip.zip", targetDir.toFile(), false, true);
		}).isInstanceOf(IOException.class)
		  .hasMessageContaining(UnzipService.ZIP_SLIP_ERROR_MSG);
	}

	@Test
	public void testUnzip_skipRoot(@TempDir Path targetDir) throws IOException {
		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/skip_root_flat.zip", targetDir.toFile(), true, true);

		Path file = targetDir.resolve("1.txt");
		assertThat(file).exists();
		file = targetDir.resolve("2.txt");
		assertThat(file).exists();
		file = targetDir.resolve("3.txt");
		assertThat(file).exists();
	}
	
	@Test
	public void testUnzip_skipRoot_false(@TempDir Path targetDir) throws IOException {
		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/skip_root_flat.zip", targetDir.toFile(), false, true);

		Path file = targetDir.resolve("root/1.txt");
		assertThat(file).exists();
		file = targetDir.resolve("root/2.txt");
		assertThat(file).exists();
		file = targetDir.resolve("root/3.txt");
		assertThat(file).exists();
	}
	
	@Test
	public void testUnzip_skipRoot_prefixButNoRoot(@TempDir Path targetDir) throws IOException {
		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/prefix_but_no_root.zip", targetDir.toFile(), true, true);

		Path file = targetDir.resolve("1_1.txt");
		assertThat(file).exists();
		file = targetDir.resolve("1_2.txt");
		assertThat(file).exists();
		file = targetDir.resolve("1_3.txt");
		assertThat(file).exists();
	}
	
	@Test
	public void testUnzip_skipRoot_nested(@TempDir Path targetDir) throws IOException {
		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/skip_root_subfolder.zip", targetDir.toFile(), true, true);
		var expectedFiles = List.of("1_1.txt", "1_2.txt", "1_3.txt",
		                            "sub/2_1.txt", "sub/2_2.txt", "sub/2_3.txt");
		Stream<Path> stream = expectedFiles.stream()
		                                   .map(targetDir::resolve);
		assertThat(stream).allMatch(p -> Files.exists(p));
	}
	
	@Test
	public void testUnzip_twoRootFolders(@TempDir Path targetDir, @TempDir Path targetDir2) throws IOException {
		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/two_folders.zip", targetDir.toFile(), false, true);
		var expectedFiles = List.of("root1/1.txt", "root1/2.txt", "root1/3.txt",
		                            "root2/1.txt", "root2/2.txt", "root2/3.txt");
		Stream<Path> stream = expectedFiles.stream()
		                                   .map(targetDir::resolve);
		assertThat(stream).allMatch(p -> Files.exists(p));

		zipUtils.unzip("src/test/resources/com/kodedu/other/ZipUtils/two_folders.zip", targetDir2.toFile(), true, true);
		stream = expectedFiles.stream()
		                      .map(targetDir2::resolve);
		assertThat(stream).allMatch(p -> Files.exists(p));
	}
}
