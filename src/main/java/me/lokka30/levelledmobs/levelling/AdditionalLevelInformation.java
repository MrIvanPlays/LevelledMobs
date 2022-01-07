package me.lokka30.levelledmobs.levelling;

/**
 * When a mob is levelled, a set of these constants
 * can be provided to describe some circumstances
 * about the mob being levelled.
 * TODO add description.
 *
 * @author stumper66, lokka30
 * @since v3.1.0
 * @see me.lokka30.levelledmobs.events.MobPreLevelEvent
 * @see me.lokka30.levelledmobs.events.MobPostLevelEvent
 */
public enum AdditionalLevelInformation {

    /**
     * The mob was spawned in via LM's summon subcommand.
     *
     * @since v4.0.0
     */
    SUMMONED
}
