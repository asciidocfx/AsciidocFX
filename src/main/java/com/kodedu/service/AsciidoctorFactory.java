package com.kodedu.service;

import com.kodedu.helper.IOHelper;
import org.asciidoctor.Asciidoctor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Component
public class AsciidoctorFactory {
    private static CountDownLatch plainDoctorReady = new CountDownLatch(1);
    private static CountDownLatch htmlDoctorReady = new CountDownLatch(1);
    private static CountDownLatch nonHtmlDoctorReady = new CountDownLatch(1);
    private static Asciidoctor plainDoctor;
    private static Asciidoctor htmlDoctor;
    private static Asciidoctor nonHtmlDoctor;
    private static DirectoryService directoryService;
    private static Map<Asciidoctor, UserExtension> userExtensionMap = new ConcurrentHashMap<>();


    @EventListener
    @Order(HIGHEST_PRECEDENCE)
    public void handleContextRefreshEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Thread.startVirtualThread(() -> {
            plainDoctor = context.getBean("plainDoctor", Asciidoctor.class);
            plainDoctorReady.countDown();
        });
        Thread.startVirtualThread(() -> {
            waitLatch(plainDoctorReady);
            htmlDoctor = context.getBean("htmlDoctor", Asciidoctor.class);
            htmlDoctorReady.countDown();
        });
        Thread.startVirtualThread(() -> {
            waitLatch(plainDoctorReady);
            waitLatch(htmlDoctorReady);
            nonHtmlDoctor = context.getBean("nonHtmlDoctor", Asciidoctor.class);
            nonHtmlDoctorReady.countDown();
        });
        Thread.startVirtualThread(() -> {
            directoryService = context.getBean(DirectoryService.class);
        });
    }

    private static void checkUserExtensions(Asciidoctor doctor) {
        if (Objects.isNull(directoryService)) {
            return;
        }
        Path workingDir = directoryService.workingDirectory();
        Path libDir = workingDir.resolve(".asciidoctor/lib");
        if (Files.notExists(libDir)) {
            return;
        }

        List<Path> extensions = IOHelper.list(libDir).filter(p -> p.toString().endsWith(".rb") || p.toString().endsWith(".jar")).sorted().toList();
        if(extensions.isEmpty()){
            return;
        }
        UserExtension userExtension = userExtensionMap.compute(doctor, (adoc, uEx) -> {
            if (Objects.nonNull(uEx)) {
                uEx.registerExtensions(adoc, extensions);
                return uEx;
            }
            UserExtension extension = new UserExtension();
            extension.setExtensionGroup(adoc.createGroup());
            extension.registerExtensions(adoc, extensions);
            return extension;
        });
    }

    public static Asciidoctor getHtmlDoctor() {
        waitLatch(htmlDoctorReady);
        checkUserExtensions(htmlDoctor);
        return htmlDoctor;
    }

    public static Asciidoctor getNonHtmlDoctor() {
        waitLatch(nonHtmlDoctorReady);
        checkUserExtensions(nonHtmlDoctor);
        return nonHtmlDoctor;
    }

    public static Asciidoctor getPlainDoctor() {
        waitLatch(plainDoctorReady);
        checkUserExtensions(plainDoctor);
        return plainDoctor;
    }

    private static void waitLatch(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
