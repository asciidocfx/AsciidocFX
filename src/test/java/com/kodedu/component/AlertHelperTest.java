package com.kodedu.component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AlertHelperTest extends ApplicationTest {
		
	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(new AnchorPane());
		stage.setScene(scene);
	}

	@Test
	public void deleteAlertWithOnePath() throws InterruptedException, ExecutionException {
		List<Path> paths = FXCollections.observableArrayList(Paths.get("C:/Temp/Index.adoc"));
		takeScreenShot(paths, "AlertHelper_Delete_oneShortPath.png");
	}
	
	@Test
	public void deleteAlertWithOneVeryLongPath() throws InterruptedException, ExecutionException {
		
		List<Path> paths = FXCollections.observableArrayList(
				Paths.get("/var/opt/lib/shared/properties/properties/properties/i18n/test/other/something/else/properties.txt"));
		
		
		takeScreenShot(paths, "AlertHelper_Delete_oneVeryLongPath.png");
	}
	
	@Test
	public void deleteAlertWithMultipleShortPaths() throws InterruptedException, ExecutionException {
		
		List<Path> paths = FXCollections.observableArrayList(
				Paths.get("C:/Temp/Index.adoc"),
				Paths.get("C:/Temp/Chapter1.adoc"),
				Paths.get("C:/Temp/Chapter2.adoc"));
		
		takeScreenShot(paths, "AlertHelper_Delete_MultipleShortPaths.png");
	}
	
	@Test
	public void deleteAlertWithMultiplePaths() throws InterruptedException, ExecutionException {
		
		List<Path> paths = FXCollections.observableArrayList(
				Paths.get("C:/Temp/Index.adoc"),
				Paths.get("C:/Temp/Chapter1.adoc"),
				Paths.get("C:/Temp/Chapter2.adoc"),
				Paths.get("C:/Temp/Chapter3.md"),
				Paths.get("C:/Temp/Chapter5.md"),
				Paths.get("C:/Temp/References.adoc"),
				Paths.get("C:/Temp/Images.adoc"),
				Paths.get("C:/Temp/Tables.adoc"),
				Paths.get("C:/Temp/README.md"),
				Paths.get("C:/Temp/LICENSE.md"));
		
		takeScreenShot(paths, "AlertHelper_Delete_multiplePaths.png");
		
	}
	
	@Test
	public void deleteAlertWithoutPaths() throws InterruptedException, ExecutionException {
			List<Path> paths = Collections.emptyList();
			takeScreenShot(paths, "AlertHelper_Delete_pathsEmpty.png");
	}
	
	private void takeScreenShot(List<Path> paths, String filename) {
		runInFXThread( () -> {
			Pane pane = AlertHelper.buildDeleteAlertDialog(paths).getDialogPane();
			screenCaptureNode(pane, filename);	
		});
	}
	

	private void screenCaptureNode(Node node, String filename) {
		BufferedImage image = SwingFXUtils.fromFXImage(capture(node).getImage(), null);
		try {
			ImageIO.write(image, "png", new File(filename));
		} catch (IOException e) {
			
		}
	}
	
	private void runInFXThread(Runnable r) {
		if (Platform.isFxApplicationThread()) {
			r.run();
		} else {
			Platform.runLater(r);
		}
	}
	
	

}
