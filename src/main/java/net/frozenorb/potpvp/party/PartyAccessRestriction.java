package net.frozenorb.potpvp.party;

/**
 * Determines what requirements are in place for
 * players attempting to join a party
 */
public enum PartyAccessRestriction {

    /**
     * Anyone can join the party (with /party join)
     * Typically used by staff / famous players
     */
    PUBLIC,

    /**
     * Players need to be invited (with /party invite)
     * to be able to join. Default restricction level,
     * used by most parties.
     */
    INVITE_ONLY,

    /**
     * Players need to have a password to be able to join.
     * (with /party join password) Typically used by players
     * in TeamSpeak / Skype / etc (when it's more convenient to
     * distribute one password than to invite everyone)
     * @see Party#password
     */
    PASSWORD

}