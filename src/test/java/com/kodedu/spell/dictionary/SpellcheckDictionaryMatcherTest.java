package com.kodedu.spell.dictionary;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpellcheckDictionaryMatcherTest {

    private static final Path EN = Path.of("conf/spellcheck/en/English.dict");
    private static final Path EN_US = Path.of("conf/spellcheck/en/hunspell/English (en_US).dict");
    private static final Path EN_GB = Path.of("conf/spellcheck/en/hunspell/English (en_GB).dict");
    private static final Path EN_AU = Path.of("conf/spellcheck/en/hunspell/English (en_AU).dict");
    private static final Path DE = Path.of("conf/spellcheck/de/German.dict");
    private static final Path DE_AT = Path.of("conf/spellcheck/de/hunspell/German (de_AT).dict");
    private static final Path DE_DE = Path.of("conf/spellcheck/de/hunspell/German (de_DE).dict");
    private static final Path TR = Path.of("conf/spellcheck/tr/Türkçe.dict");
    private static final Path PT = Path.of("conf/spellcheck/pt/Portuguese.dict");
    private static final Path FR = Path.of("conf/spellcheck/fr/French.dict");

    private static final List<Path> SHIPPED = List.of(
            TR, EN_AU, EN, DE_AT, EN_GB, DE, EN_US, DE_DE, PT, FR
    );

    @Test
    void usEnglishPicksEnUsNotTurkish() {
        assertEquals(EN_US, SpellcheckDictionaryMatcher.selectDefault(SHIPPED, Locale.US).orElseThrow());
    }

    @Test
    void britishEnglishPicksEnGb() {
        assertEquals(EN_GB, SpellcheckDictionaryMatcher.selectDefault(SHIPPED, Locale.UK).orElseThrow());
    }

    @Test
    void languageOnlyEnglishStillPicksAnEnglishDictionary() {
        Path chosen = SpellcheckDictionaryMatcher.selectDefault(SHIPPED, Locale.ENGLISH).orElseThrow();
        assertTrue(chosen.equals(EN) || chosen.equals(EN_US) || chosen.equals(EN_GB) || chosen.equals(EN_AU));
        assertTrue(SHIPPED.indexOf(TR) < SHIPPED.indexOf(EN_US), "fixture must list Turkish before English");
    }

    @Test
    void turkishOsKeepsTurkish() {
        assertEquals(TR, SpellcheckDictionaryMatcher.selectDefault(SHIPPED, Locale.of("tr", "TR")).orElseThrow());
    }

    @Test
    void germanAustriaPicksDeAt() {
        assertEquals(DE_AT, SpellcheckDictionaryMatcher.selectDefault(SHIPPED, Locale.of("de", "AT")).orElseThrow());
    }

    @Test
    void portugueseBrazilUsesPortugueseFolder() {
        assertEquals(PT, SpellcheckDictionaryMatcher.selectDefault(SHIPPED, Locale.of("pt", "BR")).orElseThrow());
    }

    @Test
    void missingLanguageFallsBackToEnglishNotTurkish() {
        assertEquals(EN_US, SpellcheckDictionaryMatcher.selectDefault(SHIPPED, Locale.JAPAN).orElseThrow());
    }

    @Test
    void emptyListIsEmpty() {
        assertEquals(Optional.empty(), SpellcheckDictionaryMatcher.selectDefault(List.of(), Locale.US));
    }
}
