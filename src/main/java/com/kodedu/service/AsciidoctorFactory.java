package com.kodedu.service;

import org.asciidoctor.Asciidoctor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
    }

    public static Asciidoctor getHtmlDoctor() {
        waitLatch(htmlDoctorReady);
        return htmlDoctor;
    }

    public static Asciidoctor getNonHtmlDoctor() {
        waitLatch(nonHtmlDoctorReady);
        return nonHtmlDoctor;
    }

    public static Asciidoctor getPlainDoctor() {
        waitLatch(plainDoctorReady);
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
