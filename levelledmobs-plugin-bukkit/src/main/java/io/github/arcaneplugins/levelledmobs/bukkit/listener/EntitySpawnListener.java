package io.github.arcaneplugins.levelledmobs.bukkit.listener;

import io.github.arcaneplugins.levelledmobs.bukkit.LevelledMobs;
import io.github.arcaneplugins.levelledmobs.bukkit.data.InternalEntityDataUtil;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.context.Context;
import io.github.arcaneplugins.levelledmobs.bukkit.api.data.keys.EntityKeyStore;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;

public final class EntitySpawnListener extends ListenerWrapper {

    /*
    Constructors
     */

    public EntitySpawnListener() {
        super(true);
    }

    /*
    Methods
     */

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void handle(final EntitySpawnEvent event) {

        /*
        LevelledMobs only concerns LivingEntities
         */
        if(!(event.getEntity() instanceof final LivingEntity entity))
            return;

        /*
        Check if the entity has any non-persistent metadata to migrate
         */
        // wasSummoned
        var wasSummonedKeyStr = EntityKeyStore.WAS_SUMMONED.toString();
        InternalEntityDataUtil.setWasSummoned(
            entity,
            entity.hasMetadata(wasSummonedKeyStr) &&
                entity.getMetadata(wasSummonedKeyStr).stream().anyMatch(val -> val.asInt() == 1),
            true
        );
        entity.removeMetadata(wasSummonedKeyStr, LevelledMobs.getInstance());

        /*
        Fire the associated trigger.
         */
        LevelledMobs.getInstance().getLogicHandler().runFunctionsWithTriggers(
            new Context().withEntity(entity), "on-entity-spawn"
        );
    }
}
