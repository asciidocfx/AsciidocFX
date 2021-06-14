package com.kodedu.service.extension;

public interface MermaidService extends DefaultSettings {
    public void createMermaidDiagram(String mermaidContent, String type, String imagesDir, String imageTarget, String nodename, boolean rerender);
}
