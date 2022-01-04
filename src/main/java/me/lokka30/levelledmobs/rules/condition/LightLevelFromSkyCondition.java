package me.lokka30.levelledmobs.rules.condition;

import me.lokka30.levelledmobs.levelling.LevelledMob;
import org.jetbrains.annotations.NotNull;

/*
TODO:
    - Add javadoc comment.
 */
public record LightLevelFromSkyCondition(
        int min,
        int max
) implements RuleCondition {

    @Override
    public boolean appliesTo(@NotNull LevelledMob levelledMob) {
        final byte lightLevel = levelledMob.getLivingEntity().getLocation().getBlock().getLightFromSky();
        return lightLevel >= min && lightLevel <= max;
    }
}
