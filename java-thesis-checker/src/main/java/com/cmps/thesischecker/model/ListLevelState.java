package com.cmps.thesischecker.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Tracks the running state of a single numbered list (identified by numId) while
 * paragraphs are processed sequentially. Groups together the level-skip tracking,
 * per-level expected format tracking, and marker-validation deduplication that would
 * otherwise require three separate maps keyed by numId in the checker itself.
 */
@Getter
@Setter
public class ListLevelState {
    private Integer lastLevel;
    private final Map<BigInteger, String> expectedFormatByLevel = new HashMap<>();
    private final Set<BigInteger> validatedMarkerLevels = new HashSet<>();

    /**
     * Looks up the numbering format previously recorded for a given indentation level.
     *
     * @param ilvl the indentation level to look up
     * @return the numbering format observed earlier at this level, or null if this level
     *         has not been seen yet
     */
    public String getExpectedFormat(BigInteger ilvl) {
        return expectedFormatByLevel.get(ilvl);
    }

    /**
     * Records the numbering format observed for a given indentation level, so that later
     * paragraphs at the same level can be checked for consistency.
     *
     * @param ilvl   the indentation level
     * @param format the numbering format observed at this level
     */
    public void recordFormat(BigInteger ilvl, String format) {
        expectedFormatByLevel.put(ilvl, format);
    }

    /**
     * Marks the given indentation level as already validated for marker style (allowed
     * format, closing symbol, bullet character), so that it is not checked again for
     * every subsequent paragraph at the same level.
     *
     * @param ilvl the indentation level to mark as validated
     * @return true if this level had not been validated before this call, false if it
     *         was already validated
     */
    public boolean markMarkerValidated(BigInteger ilvl) {
        return validatedMarkerLevels.add(ilvl);
    }
}
