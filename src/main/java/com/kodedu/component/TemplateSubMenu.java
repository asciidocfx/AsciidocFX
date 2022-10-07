package com.kodedu.component;

import com.kodedu.config.templates.AsciidocTemplateI;
import com.kodedu.controller.ApplicationController;
import com.kodedu.service.TemplateService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.IndikatorService;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * Component which handles the creation of template menu entries.
 * Also adds them to the Menu.
 *
 */
@Component
public class TemplateSubMenu {

    private Logger logger = LoggerFactory.getLogger(TemplateSubMenu.class);

    private final ApplicationController applicationController;
    private final ThreadService threadService;
	private final IndikatorService indikatorService;
	private final TemplateService templateService;
    
	public TemplateSubMenu(ApplicationController applicationController,
	        ThreadService threadService,
	        IndikatorService indikatorService,
	        TemplateService templateService) {
		super();
		this.applicationController = applicationController;
		this.threadService = threadService;
		this.indikatorService = indikatorService;
		this.templateService = templateService;
	}

	public void setMenuItems(final List<? extends AsciidocTemplateI> templates) {
		Menu templateMenu = applicationController.getTemplateMenu();
		templateMenu.getItems().clear();
		templates.stream()
		         .map(t -> createMenuItem(t))
		         .forEach(mi -> templateMenu.getItems().add(mi));
	}

	private MenuItem createMenuItem(final AsciidocTemplateI template) {
		var builder = MenuItemBuilt.item(template.getName()); 

		StringBuilder tooltipMsg = new StringBuilder();
		tooltipMsg.append("Location: ").append(template.getLocation());
		if (!StringUtils.isBlank(template.getDescription())) {
			tooltipMsg.append("\n\n").append(template.getDescription());
		}
		builder.tip(tooltipMsg.toString());

		return builder.click((evt) -> templateMenuItemOnClick(template));
	}

	private void templateMenuItemOnClick(final AsciidocTemplateI template) {
		applicationController.getSelectedItemOrWorkspacePath()
				.map(p -> !Files.isDirectory(p) ? p.getParent() : p)
				.filter(Objects::nonNull)
				.ifPresent(targetDir -> {
					threadService.runTaskLater(() -> {
						try {
							indikatorService.startProgressBar();
							templateService.provide(template, targetDir);
						} catch (Exception e) {
							logger.error("Could not supply the template %s".formatted(template.getName()), e);
						} finally {
							indikatorService.stopProgressBar();
						}
					});
				});
	}
}
