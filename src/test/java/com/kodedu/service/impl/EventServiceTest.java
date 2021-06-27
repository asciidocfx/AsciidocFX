package com.kodedu.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javafx.util.Pair;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.framework.junit5.ApplicationExtension;

import com.kodedu.service.EventService;
import com.kodedu.service.EventService.Event;
import com.kodedu.service.EventService.Subscription;

import io.github.netmikey.logunit.api.LogCapturer;

@ExtendWith(ApplicationExtension.class)
@SpringBootTest()
@ContextConfiguration(classes= {EventServiceImpl.class, ThreadServiceImpl.class})
public class EventServiceTest {
    // FIXME: Tests rely on ThreadService implementation, making it compatible withs tests/mocks would make tests execution more reliable

    /**
     * Event Service object being tested
     */
    @Autowired
    private EventService eventService;

    /**
     * Log auditing for Event service logs
     */
    @RegisterExtension
    LogCapturer logs = LogCapturer.create().captureForType(EventServiceImpl.class, Level.DEBUG);

    /**
     * Ensure that base Bean loading functions are satisfied
     */
    @Test
    void should_allow_subscription_and_unsubscription() {
        String eventLabel = "test::event::basicUsage";
        Subscription subscription = eventService.subscribe(eventLabel, e -> {});
        Assertions.assertNotNull(subscription);
        Assertions.assertDoesNotThrow(() -> eventService.unsubscribe(subscription));
    }

    /**
     * Test that event subscriptions are honored
     */
    @Test
    void should_notify_subscribed_events() {
        String eventLabel = "test::event::simpleNotify";
        List<Pair<String, Object>> callstack1 = new LinkedList<Pair<String, Object>>();
        List<Pair<String, Object>> callstack2 = new LinkedList<Pair<String, Object>>();

        // Add two event subscribers and ensure they are not called early
        eventService.subscribe(eventLabel, e -> callstack1.add(new Pair<String, Object>(e.getLabel(), e.getData())));
        eventService.subscribe(eventLabel, e -> callstack2.add(new Pair<String, Object>(e.getLabel(), e.getData())));
        Assertions.assertEquals(0, callstack1.size());
        Assertions.assertEquals(0, callstack2.size());

        // Send an event and ensure both subscribers receive them
        eventService.sendEvent(eventLabel, "payload");
        Assertions.assertDoesNotThrow(() -> Thread.sleep(10));
        Assertions.assertEquals(1, callstack1.size());
        Assertions.assertEquals(1, callstack2.size());
    }

    /**
     * Test handling of event subscribers with errors
     */
    @Test
    void should_gracefully_handle_throwing_subscribers() {
        String eventLabel = "test::event::simpleThrow";
        String exceptionMessage = "0001_This is a test exception";
        RuntimeException thrownException = new RuntimeException(exceptionMessage);

        // Prepare consumer
        eventService.subscribe(eventLabel, e -> {throw thrownException;});
        Assertions.assertTrue(logs.getEvents().isEmpty());

        // Send event to throwing consumer
        eventService.sendEvent(eventLabel, "payload");
        Assertions.assertDoesNotThrow(() -> Thread.sleep(10));
        Assertions.assertTrue(logs.getEvents().stream().anyMatch(e -> e.getLevel() == Level.DEBUG && e.getThrowable() == thrownException));
    }

    /**
     * Test invalid unsubscription parameters handling
     */
    @Test
    void should_warn_of_invalid_unsubscription_attempts() {
        Subscription unknownSubscription = new Subscription() {}; // inline class declaration
        String eventLabel = "test::event::unsubscriptionTest";
        Subscription knownSubscription = eventService.subscribe(eventLabel, e -> {});

        // Regular unsubscribe, no error expected
        eventService.unsubscribe(knownSubscription);
        Assertions.assertTrue(logs.getEvents().isEmpty());

        // Attempt to unsubscribe an already unsubscribed item
        eventService.unsubscribe(knownSubscription);
        Assertions.assertEquals(1, logs.getEvents().stream().count());

        // Attempt to unsubscribe a subscription from a different EventService implementation
        eventService.unsubscribe(unknownSubscription);
        Assertions.assertEquals(2, logs.getEvents().stream().filter(e -> e.getLevel() == Level.ERROR).count());
    }

    /**
     * Test several invalid subscription labels
     */
    @Test
    void should_reject_invalid_subscription_labels() {
        Consumer<Event> consumer = (Event e) -> {};
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventService.subscribe("", consumer);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventService.subscribe("::", consumer);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventService.subscribe("test::event::invalid::", consumer);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventService.subscribe("test::event:::invalid", consumer);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventService.subscribe("::test::event::invalid", consumer);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventService.subscribe("3test::event::invalid::", consumer);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventService.subscribe("test::event::invalid::*", consumer);
        });
    }
}
