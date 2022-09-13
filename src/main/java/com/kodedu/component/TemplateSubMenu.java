package com.kodedu.component;

import com.kodedu.controller.ApplicationController;
import com.kodedu.service.ThreadService;
import com.kodedu.service.UnzipService;
import com.kodedu.service.ui.IndikatorService;
import com.kodedu.template.AsciidocTemplateI;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;

@Component
public class TemplateSubMenu {

    private Logger logger = LoggerFactory.getLogger(TemplateSubMenu.class);

    private final ApplicationController applicationController;
    private final ThreadService threadService;
    private final UnzipService zipUtils;
	private final IndikatorService indikatorService;
    
	public TemplateSubMenu(ApplicationController applicationController,
	        ThreadService threadService, UnzipService zipUtils,
	        IndikatorService indikatorService) {
		super();
		this.applicationController = applicationController;
		this.threadService = threadService;
		this.zipUtils = zipUtils;
		this.indikatorService = indikatorService;
	}

	public void setTemplateMenuItems(final List<? extends AsciidocTemplateI> templates) {
		Menu templateMenu = applicationController.getTemplateMenu();
		templateMenu.getItems().clear();
		templates.stream()
		         .map(t -> createMenuItem(t))
		         .forEach(mi -> templateMenu.getItems().add(mi));
	}

	private MenuItem createMenuItem(final AsciidocTemplateI template) {
		var item = new CustomMenuItem(new Label(template.getName()));

		StringBuilder msg = new StringBuilder();
		msg.append("Location: ").append(template.getLocation());
		if (!StringUtils.isBlank(template.getDescription())) {
			msg.append("\n\n").append(template.getDescription());
		}

		var tooltip = new Tooltip(msg.toString());
		Tooltip.install(item.getContent(), tooltip);

		item.setOnAction((evt) -> templateMenuItemOnClick(template));

		return item;
	}

	private void templateMenuItemOnClick(final AsciidocTemplateI template) {
		Optional<Path> targetPath = applicationController.getSelectedItemOrWorkspacePath();
		if (targetPath.isPresent()) {
			Path tarPath = targetPath.get();
			if (!Files.isDirectory(tarPath)) {
				tarPath = tarPath.getParent();
			}
			if (tarPath != null) {
				final var targetDir = tarPath;
				threadService.runTaskLater(() -> {
					try {
						indikatorService.startProgressBar();
						template.provide(targetDir, zipUtils);
					} catch (Exception e) {
						logger.error("Could not supply the template %s".formatted(template.getName()), e);
					} finally {
						indikatorService.stopProgressBar();
					}
				});
			}
		}
	}
}
