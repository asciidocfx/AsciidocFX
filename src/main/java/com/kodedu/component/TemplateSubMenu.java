package com.kodedu.component;

import com.kodedu.controller.ApplicationController;
import com.kodedu.other.ZipUtils;
import com.kodedu.service.ThreadService;
import com.kodedu.template.MetaAsciidocTemplateI;

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
    private final ZipUtils zipUtils;
    
	public TemplateSubMenu(ApplicationController applicationController,
	        ThreadService threadService, ZipUtils zipUtils) {
		super();
		this.applicationController = applicationController;
		this.threadService = threadService;
		this.zipUtils = zipUtils;
	}

	public void setTemplateMenuItems(List<? extends MetaAsciidocTemplateI> templates) {
		Menu templateMenu = applicationController.getTemplateMenu();
		templateMenu.getItems().clear();
		templates.stream()
		         .map(t -> createMenuItem(t))
		         .forEach(mi -> templateMenu.getItems().add(mi));
	}

	private MenuItem createMenuItem(MetaAsciidocTemplateI t) {
		var item = new CustomMenuItem(new Label(t.getName()));

		StringBuilder msg = new StringBuilder();
		msg.append("Location: ").append(t.getLocation());
		if (!StringUtils.isBlank(t.getDescription())) {
			msg.append("\n\n").append(t.getDescription());
		}

		var tooltip = new Tooltip(msg.toString());
		Tooltip.install(item.getContent(), tooltip);

		item.setOnAction((evt) -> templateMenuItemOnClick(t));

		return item;
	}

	private void templateMenuItemOnClick(MetaAsciidocTemplateI t) {
		Optional<Path> targetPath =  applicationController.getSelectedItemOrWorkspacePath();
		if(targetPath.isPresent()) {
			Path tarPath = targetPath.get();
			if(!Files.isDirectory(tarPath)) {
				tarPath = tarPath.getParent();
			}
			if(tarPath != null) {
				final var target = tarPath;
				threadService.runTaskLater(()->{
				try {
					t.provide(target, zipUtils);
				} catch (Exception e) {
					logger.error("Could not supply the template %s".formatted(t.getName()), e);
				}}
				);
			}
		}
	}
}
