/*
 * Copyright (c) 2020-2021  lokka30. Use of this source code is governed by the GNU AGPL v3.0 license that can be found in the LICENSE.md file.
 */

package me.lokka30.levelledmobs.listeners;

import me.lokka30.levelledmobs.LevelledMobs;
import me.lokka30.levelledmobs.misc.LivingEntityWrapper;
import me.lokka30.levelledmobs.misc.QueueItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Listens for when chunks are loaded and processes any mobs accordingly Needed for server startup
 * and for mostly passive mobs when players are moving around
 *
 * @author stumper66
 * @since 2.4.0
 */
public class ChunkLoadListener implements Listener {

    private final LevelledMobs main;

    public ChunkLoadListener(final LevelledMobs main) {
        this.main = main;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkLoad(final ChunkLoadEvent event) {

        if (!main.helperSettings.getBoolean(main.settingsCfg,
            "ensure-mobs-are-levelled-on-chunk-load", true)) {
            return;
        }

        // Check each entity in the chunk
        for (final Entity entity : event.getChunk().getEntities()) {

            // Must be a *living* entity
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            final LivingEntityWrapper lmEntity = LivingEntityWrapper.getInstance(
                (LivingEntity) entity, main);

            if (lmEntity.isLevelled()) {
                lmEntity.free();
                continue;
            }

            main.mobsQueueManager.addToQueue(new QueueItem(lmEntity, event));
            lmEntity.free();
        }
    }
}
