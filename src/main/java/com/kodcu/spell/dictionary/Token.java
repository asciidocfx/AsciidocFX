package com.kodcu.spell.dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by usta on 06.12.2015.
 */
public class Token {

    private final static Pattern tokenPattern = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);

    private int row;
    private int start;
    private int end;
    private String value;
    private String type;
    private boolean emptySuggestion;

    public Token() {
    }

    public Token(int row, int start, int end, String value, String type) {
        this.row = row;
        this.start = start;
        this.end = end;
        this.value = value;
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token{" +
                "row=" + row +
                ", start=" + start +
                ", end=" + end +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public List<Token> fromLines(String line) {
        List<Token> tokenList = new ArrayList<>();
        final Matcher matcher = tokenPattern.matcher(line);
        while (matcher.find()) {
            tokenList.add(new Token(row, matcher.start() + start, matcher.end() + start, matcher.group(), type));
        }

        return tokenList;
    }

    public void setEmptySuggestion(boolean emptySuggestion) {
        this.emptySuggestion = emptySuggestion;
    }

    public boolean isEmptySuggestion() {
        return emptySuggestion;
    }
}
