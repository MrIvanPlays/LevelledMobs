package io.github.arcaneplugins.levelledmobs.bukkit.logic.customdrops;

import io.github.arcaneplugins.levelledmobs.bukkit.LevelledMobs;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.customdrops.recipient.DropTableRecipient;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.customdrops.recipient.EntityTypeRecipient;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.customdrops.recipient.MobGroupRecipient;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.group.Group;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class CustomDropHandler {

    public static final Map<DropTableRecipient, Collection<CustomDrop>>
        DROP_TABLE_CUSTOM_DROPS_MAP = new HashMap<>();

    public static final Map<EntityTypeRecipient, Collection<CustomDrop>>
        ENTITY_TYPE_CUSTOM_DROPS_MAP = new HashMap<>();

    public static final Map<MobGroupRecipient, Collection<CustomDrop>>
        MOB_GROUP_CUSTOM_DROPS_MAP = new HashMap<>();

    //TODO use
    public static void handleEntitySpawn(
        final @Nonnull EntitySpawnEvent event
    ) {
        if(!(event.getEntity() instanceof LivingEntity entity)) return;
    }

    public static EntityDeathCustomDropResult handleEntityDeath(
        final @Nonnull EntityDeathEvent event
    ) {
        //TODO this is just a placeholder to make the other code work. needs a proper impl
        return new EntityDeathCustomDropResult(
            Collections.emptyList(),
            false,
            false
        );
    }

    //TODO use
    public static @Nonnull Collection<CustomDrop> getDefinedCustomDropsForEntityType(
        final @Nonnull EntityType entityType
    ) {
        final Collection<CustomDrop> applicableCds = new LinkedList<>();

        DROP_TABLE_CUSTOM_DROPS_MAP.forEach((recip, cds) -> {
            if(recip.getApplicableEntities().contains(entityType)) {
                applicableCds.addAll(cds);
            }

            //TODO also get the drop tables applied to the entity thru a LmFunction
            //TODO in lm3 that's called 'usedroptableid'
        });

        ENTITY_TYPE_CUSTOM_DROPS_MAP.forEach((recip, cds) -> {
            if(recip.getEntityType() == entityType) {
                applicableCds.addAll(cds);
            }
        });

        MOB_GROUP_CUSTOM_DROPS_MAP.forEach((recip, cds) -> {
            final Optional<Group> groupOpt = LevelledMobs.getInstance()
                .getLogicHandler()
                .getGroups()
                .stream()
                .filter(g -> g.getIdentifier().equalsIgnoreCase(recip.getMobGroupId()))
                .findFirst();

            groupOpt.ifPresent(group -> {
                for(final String groupItem : group.getItems()) {
                    if(groupItem.equalsIgnoreCase(entityType.name())) {
                        applicableCds.addAll(cds);
                        return;
                    }
                }
            });
        });

        return applicableCds;
    }

}
