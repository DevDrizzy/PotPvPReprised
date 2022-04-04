package net.frozenorb.potpvp.match;

/**
 * Describes a reason for a match's termination
 */
public enum MatchEndReason {

    /**
     * All enemies have been eliminated,
     * leaving only one {@link MatchTeam} with >= 1 alive players.
     */
    ENEMIES_ELIMINATED,

    /**
     * The match duration exceeded a predefined limit.
     * @see net.frozenorb.potpvp.match.listener.MatchDurationLimitListener
     */
    DURATION_LIMIT_EXCEEDED

}