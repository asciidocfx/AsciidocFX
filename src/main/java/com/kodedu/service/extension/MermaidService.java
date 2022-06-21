package com.kodedu.service.extension;

public interface MermaidService extends DefaultSettings {
    public final static String label = "core::service::extension::Mermaid";
    public void createMermaidDiagram(String mermaidContent, String type, String imagesDir, String imageTarget, String nodename, boolean rerender);
}
