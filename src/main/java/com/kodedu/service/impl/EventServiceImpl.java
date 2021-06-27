package com.kodedu.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kodedu.service.EventService;
import com.kodedu.service.ThreadService;

@Component(EventService.label)
public class EventServiceImpl implements EventService {

    /**
     * Map event labels to the associated subscriptions
     */
    private final Map<String, List<Subscription>> subscriptionsMap = new HashMap<String, List<Subscription>>();

    /**
     * String used to split event scopes
     */
    private final String EVENT_SCOPE_SEPARATOR = "::";

    /**
     * Logger for {@link EventServiceImpl}
     */
    private static Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    /**
     * The thread service instance to use when scheduling notifications
     */
    @Autowired
    private ThreadService threadService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEvent(String label, Object data) {
        Event event = new Event(label, data);
        getMatchingSubscriptionsCallbacks(label).forEach(consumer -> threadService.runActionLater(() -> {
            try {
                consumer.accept(event);
            } catch (Exception e) {
                logger.debug(String.format("An exception occured while dispatching an event with label %s", label), e);
            }
        }));
    }

    /**
     * Gather all unique callbacks to call for an event dispatch
     * @param eventLabel The qualifier of the event whose listeners should be looked-up
     * @return A set of Consumers matching the label
     */
    private Set<Consumer<EventService.Event>> getMatchingSubscriptionsCallbacks(String eventLabel) {
        Set<Consumer<EventService.Event>> matchSet = new HashSet<Consumer<EventService.Event>>();
        String labelExplorer = "";
        synchronized (subscriptionsMap) {
            for(String scope: eventLabel.split(EVENT_SCOPE_SEPARATOR)) {
                labelExplorer += (labelExplorer.length()>0 ? EVENT_SCOPE_SEPARATOR : "") + scope;
                if(subscriptionsMap.containsKey(labelExplorer)) {
                    matchSet.addAll(
                            subscriptionsMap.get(labelExplorer).stream()
                            .map(s -> s.getConsumer())
                            .collect(Collectors.toCollection(HashSet::new)));
                }
            }
        }
        return matchSet;
    }

    /**
     * Ensure that an event label is well-formed
     * @param label The event to dispatch
     * @return Whether the label is well-formed
     */
    private boolean isLabelValid(String label) {
        if(label.startsWith(EVENT_SCOPE_SEPARATOR) || label.endsWith(EVENT_SCOPE_SEPARATOR)) {
            return false;
        }
        for(String scope: label.split(EVENT_SCOPE_SEPARATOR)) {
            if(!scope.matches("[a-zA-Z][a-zA-Z0-9]+")) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventService.Subscription subscribe(String eventLabel, Consumer<EventService.Event> callback) {
        if(!isLabelValid(eventLabel)) {
            // FIXME: Should throw an extension of IllegalFormatException
            IllegalArgumentException exception = new IllegalArgumentException(String.format("%s%s", "Invalid event label received: ", eventLabel));
            //logger.error("Subscription error", exception);
            throw exception;
        }

        Subscription listenerSubscription = new Subscription(eventLabel, callback);
        synchronized (subscriptionsMap) {
            if(subscriptionsMap.containsKey(eventLabel)) {
                // Premature optimization: Add first so that short-lived subscriptions end up being found quickly @ removal
                subscriptionsMap.get(eventLabel).add(0, listenerSubscription);
            } else {
                List<Subscription> subscriptionList = new LinkedList<Subscription>();
                subscriptionList.add(listenerSubscription);
                subscriptionsMap.put(eventLabel, subscriptionList);
            }
        }
        return listenerSubscription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe(EventService.Subscription subscription) {
        if (!(subscription instanceof Subscription)) {
            // Might happen if several EventService implementations are provided and swap between one another
            logger.error("Attempted to cancel a subscription provided by a different EventService Implementation");
            return;
        }
        Subscription internalSubscription = (Subscription) subscription;
        String subscriptionLabel = internalSubscription.getLabel();
        synchronized (subscriptionsMap) {
            if(subscriptionsMap.containsKey(subscriptionLabel)) {
                List<Subscription> subscriptionList = subscriptionsMap.get(subscriptionLabel);
                if(subscriptionList.remove(internalSubscription)) {
                    // Do not clean-up on empty list, the memory impact
                    // does not justify the additional complexity and processing
                    return;
                }
            }
        }
        // We reach this point only if the subscription was not in store
        logger.error(String.format("%s%s", "Attempted to cancel a subscription but it could not be found for label ", internalSubscription.getLabel() ));
    }

    private class Event implements EventService.Event {
        /**
         * The Event's label
         */
        private final String label;

        /**
         * The Event's data
         */
        private final Object data;

        /**
         * The Event's creation timestamp
         */
        private final Date timestamp;

        /**
         * Base Event constructor
         * @param label The Event's label
         * @param data The Event's data
         */
        public Event(String label, Object data) {
            this.label = label;
            this.data = data;
            this.timestamp = new Date();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getLabel() {
            return label;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object getData() {
            return data;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Date getTimestamp() {
            return this.timestamp;
        }

    }

    private class Subscription implements EventService.Subscription {
        /**
         * The Event label handled by the subscription
         */
        private String label;
        /**
         * The Consumer used by the subscriptions
         */
        private Consumer<EventService.Event> consumer;

        /**
         * Constructor
         * @param label
         * @param consumer
         */
        public Subscription(String label, Consumer<EventService.Event> consumer) {
            this.label = label;
            this.consumer = consumer;
        }

        /**
         * Get the subscription's event label
         * @return The event label
         */
        public String getLabel() {
            return this.label;
        }

        /**
         * Get the subscription's callback
         * @return The callback
         */
        public Consumer<EventService.Event> getConsumer() {
            return this.consumer;
        }
    }
}
