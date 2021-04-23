package com.kodedu.spell.dictionary;

import com.kodedu.component.EditorPane;
import com.kodedu.config.SpellcheckConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.service.ThreadService;
import com.kodedu.spell.filter.SpellFilterProvider;
import morfologik.speller.Speller;
import morfologik.stemming.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by usta on 07.12.2015.
 */
@Component
public class DictionaryService {

    private Logger logger = LoggerFactory.getLogger(DictionaryService.class);

    private final ApplicationController controller;
    private final SpellFilterProvider spellFilterProvider;
    private final ThreadService threadService;
    private final SpellcheckConfigBean spellcheckConfigBean;

    private Map<Path, LanguageSpeller> languageSpellerMap = new ConcurrentHashMap<>();
    private Map<String, List<String>> suggestionMap = new ConcurrentHashMap<>();

    @Autowired
    public DictionaryService(ApplicationController controller, SpellFilterProvider spellFilterProvider, ThreadService threadService, SpellcheckConfigBean spellcheckConfigBean) {
        this.controller = controller;
        this.spellFilterProvider = spellFilterProvider;
        this.threadService = threadService;
        this.spellcheckConfigBean = spellcheckConfigBean;
    }


    private LanguageSpeller getLanguageSpeller(Path defaultLanguage) {
        try {
            Dictionary dictionary = Dictionary.read(defaultLanguage);
            Speller speller = new Speller(dictionary);
            final LanguageSpeller languageSpeller = new LanguageSpeller();
            languageSpeller.setDictionary(dictionary);
            languageSpeller.setSpeller(speller);
            languageSpeller.setEncoding(dictionary.metadata.getEncoding());
            languageSpellerMap.putIfAbsent(defaultLanguage, languageSpeller);
            return languageSpellerMap.get(defaultLanguage);
        } catch (NullPointerException e) {
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }


    public void processTokens(EditorPane editorPane, String jsonToken, String mode) {

        final Path language = Optional.ofNullable(editorPane.getSpellLanguage()).orElseGet(spellcheckConfigBean::getDefaultLanguage);

        if (Objects.isNull(language)) {
            return;
        }

        final LanguageSpeller languageSpeller = getLanguageSpeller(language);

        if (Objects.isNull(languageSpeller)) {
            return;
        }

        final List<Token> tokenList = languageSpeller.getTokenList(jsonToken);

        final List<Token> tokens = tokenList.stream()
                .flatMap(token -> token.fromLines((token.getValue())).stream())
                .filter(spellFilterProvider.filterByMode(mode))
                .filter(t -> {
                    try {
                        return languageSpeller.isMisspelled(t.getValue());
                    } catch (Exception e) {
                        logger.info("Couldn't spell the word: {}", t.getValue(), e);
                        return false;
                    }
                })
                .peek(t -> {
                    final List<String> suggestions = languageSpeller.findSuggestions(t.getValue());
                    t.setEmptySuggestion(suggestions.isEmpty());
                    suggestionMap.put(t.getValue(), suggestions);
                })
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            return;
        }

        threadService.runActionLater(() -> {
            for (Token token : tokens) {
                editorPane.addTypo(token);
            }
        });

    }

    public Map<String, List<String>> getSuggestionMap() {
        return suggestionMap;
    }
}
