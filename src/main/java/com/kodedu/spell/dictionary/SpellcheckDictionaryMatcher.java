package com.kodedu.spell.dictionary;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Picks a shipped spell-check dictionary from the OS locale.
 * Dictionary order must not decide the default (reverse path sort puts Turkish first).
 */
public final class SpellcheckDictionaryMatcher {

    private static final Pattern LOCALE_IN_NAME =
            Pattern.compile("\\(([a-zA-Z]{2,3})(?:[_-]([a-zA-Z]{2}))?\\)");

    private SpellcheckDictionaryMatcher() {
    }

    /**
     * Best dictionary for {@code locale} among {@code dictionaries}.
     * Prefers an exact {@code (en_US)}-style filename, then a language-only dict in that
     * language folder, then any dict for that language. If the OS language has no
     * shipped dictionary, falls back to English ({@code en_US}, then generic English).
     */
    public static Optional<Path> selectDefault(List<Path> dictionaries, Locale locale) {
        if (dictionaries == null || dictionaries.isEmpty()) {
            return Optional.empty();
        }

        Locale resolvedLocale = locale == null ? Locale.getDefault() : locale;
        return bestMatch(dictionaries, resolvedLocale)
                .or(() -> englishFallback(dictionaries));
    }

    static Optional<Path> bestMatch(List<Path> dictionaries, Locale locale) {
        String language = normalizeLanguage(locale.getLanguage());
        String country = normalizeCountry(locale.getCountry());
        if (language.isEmpty()) {
            return Optional.empty();
        }

        Path best = null;
        int bestScore = 0;

        for (Path dictionary : dictionaries) {
            int score = score(dictionary, language, country);
            if (score > bestScore) {
                bestScore = score;
                best = dictionary;
            }
        }

        return bestScore > 0 ? Optional.of(best) : Optional.empty();
    }

    static Optional<Path> englishFallback(List<Path> dictionaries) {
        return bestMatch(dictionaries, Locale.US)
                .or(() -> dictionaries.stream()
                        .filter(SpellcheckDictionaryMatcher::isGenericEnglish)
                        .findFirst())
                .or(() -> dictionaries.stream()
                        .filter(path -> "en".equals(languageFolder(path)))
                        .findFirst());
    }

    static int score(Path dictionary, String language, String country) {
        FilenameLocale tag = parseFilenameLocale(dictionary);
        String folderLanguage = languageFolder(dictionary);

        int score = 0;

        if (tag != null && language.equals(tag.language)) {
            if (!country.isEmpty() && country.equals(tag.country)) {
                score = 100;
            } else if (tag.country.isEmpty()) {
                score = 80;
            } else {
                score = 60;
            }
        }

        if (language.equals(folderLanguage)) {
            if (isGenericDictionary(dictionary)) {
                score = Math.max(score, 75);
            } else {
                score = Math.max(score, 50);
            }
        }

        return score;
    }

    static FilenameLocale parseFilenameLocale(Path dictionary) {
        String fileName = fileNameWithoutExtension(dictionary);
        Matcher matcher = LOCALE_IN_NAME.matcher(fileName);
        if (!matcher.find()) {
            return null;
        }
        String language = normalizeLanguage(matcher.group(1));
        String country = matcher.group(2) == null ? "" : normalizeCountry(matcher.group(2));
        return new FilenameLocale(language, country);
    }

    static String languageFolder(Path dictionary) {
        if (dictionary == null) {
            return "";
        }
        Path current = dictionary.getParent();
        while (current != null && current.getFileName() != null) {
            String name = current.getFileName().toString();
            if ("spellcheck".equalsIgnoreCase(name)) {
                break;
            }
            if (name.matches("[a-zA-Z]{2,3}")) {
                return normalizeLanguage(name);
            }
            current = current.getParent();
        }
        return "";
    }

    static boolean isGenericDictionary(Path dictionary) {
        return parseFilenameLocale(dictionary) == null;
    }

    static boolean isGenericEnglish(Path dictionary) {
        return "en".equals(languageFolder(dictionary)) && isGenericDictionary(dictionary)
                && fileNameWithoutExtension(dictionary).equalsIgnoreCase("English");
    }

    static String fileNameWithoutExtension(Path dictionary) {
        if (dictionary == null || dictionary.getFileName() == null) {
            return "";
        }
        String name = dictionary.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private static String normalizeLanguage(String language) {
        return language == null ? "" : language.toLowerCase(Locale.ROOT);
    }

    private static String normalizeCountry(String country) {
        return country == null ? "" : country.toUpperCase(Locale.ROOT);
    }

    public static List<Path> sortedByFileName(List<Path> dictionaries) {
        List<Path> copy = new ArrayList<>(dictionaries);
        copy.sort(Comparator.comparing(SpellcheckDictionaryMatcher::fileNameWithoutExtension,
                String.CASE_INSENSITIVE_ORDER));
        return copy;
    }

    static final class FilenameLocale {
        final String language;
        final String country;

        FilenameLocale(String language, String country) {
            this.language = Objects.requireNonNullElse(language, "");
            this.country = Objects.requireNonNullElse(country, "");
        }
    }
}
