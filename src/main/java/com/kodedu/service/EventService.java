package com.kodedu.service;

import java.util.Date;
import java.util.function.Consumer;

/**
 * Used to share state changes accross the application
 * @author Ayowel
 * @since RC-SNAPSHOT
 */
public interface EventService {
    /**
     * The default service label to use
     */
    public final static String label = "core::service::EventService";

    /**
     * Propagate an event to all subscribed listeners
     * @param label
     *   The label of the event to subscribe to.
     *   Label have scopes separated with :: scopes must be alpha-numerical
     * @param data The dataset associated to the dispatched event
     */
    public void sendEvent(String label, Object data);

    /**
     * Subscribe to receive event notifications
     * @param eventLabel The name of the event to listen for. May use a whole-scope wildcard with * instead of a scope's name
     * @param callback A consumer to call when the event is triggered
     * @return An event subscription object used to unsubscribe if needs be
     * @see #sendEvent(String, Object)
     */
    public Subscription subscribe(String eventLabel, Consumer<Event> callback);

    /**
     * Stop receiving event notifications for a subscription
     * @param subscription The event subscription to cancel
     */
    public void unsubscribe(Subscription subscription);

    /**
     * Represents a subscription to an event
     * @author Ayowel
     * @since RC-SNAPSHOT
     */
    public interface Subscription { }

    /**
     * Event object
     * @author Ayowel
     * @since RC-SNAPSHOT
     * @param <T> The data type propagated by the event
     */
    public interface Event {
        /**
         * The label of the event
         * @return
         */
        public String getLabel();
        /**
         * The event's payload
         * @return
         */
        public Object getData();

        /**
         * The event's creation timestamp
         * @return
         */
        public Date getTimestamp();
    }
}
