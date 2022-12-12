package com.kodedu.service;

import org.asciidoctor.Asciidoctor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class AsciidoctorFactory {

    private static CountDownLatch standardDoctorReady = new CountDownLatch(1);
    private static CountDownLatch plainDoctorReady = new CountDownLatch(1);
    private static Asciidoctor standardDoctor;
    private static Asciidoctor plainDoctor;

    @EventListener
    public void handleContextRefreshEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Thread.startVirtualThread(() -> {
            standardDoctor = context.getBean("standardDoctor", Asciidoctor.class);
            standardDoctorReady.countDown();
        });
        Thread.startVirtualThread(() -> {
            plainDoctor = context.getBean("plainDoctor", Asciidoctor.class);
            plainDoctorReady.countDown();
        });
    }

    public static Asciidoctor getStandardDoctor() {
        waitLatch(standardDoctorReady);
        return standardDoctor;
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
