package com.kodcu.spell.dictionary;

import com.fasterxml.jackson.databind.ObjectMapper;
import morfologik.speller.Speller;
import morfologik.stemming.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usta on 07.12.2015.
 */
public class LanguageSpeller {

    private Logger logger = LoggerFactory.getLogger(LanguageSpeller.class);

    private Dictionary dictionary;
    private Speller speller;
    private String encoding;


    public boolean isMisspelled(String word) {
        return speller.isMisspelled(word);
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Speller getSpeller() {
        return speller;
    }

    public void setSpeller(Speller speller) {
        this.speller = speller;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }

    public List<Token> getTokenList(String jsonToken) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonToken, mapper.getTypeFactory().constructCollectionType(List.class, Token.class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean isInDictionary(String word) {
        return speller.isInDictionary(word);
    }

    public List<String> findSuggestions(String word) {
        final ArrayList<String> resultList = new ArrayList<>();
        try {
            final List<String> runOnWords = speller.replaceRunOnWords(word);
            final List<String> replacements = speller.findReplacements(word);
            resultList.addAll(runOnWords);
            resultList.addAll(replacements);
            return resultList;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return resultList;
        }
    }
}
