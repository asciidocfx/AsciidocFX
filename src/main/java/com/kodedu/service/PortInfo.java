package com.kodedu.service;

import com.kodedu.terminalfx.helper.ThreadHelper;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class PortInfo implements ApplicationListener<ServletWebServerInitializedEvent> {

    private final Environment environment;
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);
    private static int port = 8080;

    public PortInfo(Environment environment) {
        this.environment = environment;
    }

    public static int getPort() {
        ThreadHelper.awaitLatch(countDownLatch);
        return port;
    }

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        port = event.getWebServer().getPort();
        countDownLatch.countDown();
    }
}
