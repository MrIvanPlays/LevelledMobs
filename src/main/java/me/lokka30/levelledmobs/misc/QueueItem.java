/*
 * Copyright (c) 2020-2021  lokka30. Use of this source code is governed by the GNU AGPL v3.0 license that can be found in the LICENSE.md file.
 */

package me.lokka30.levelledmobs.misc;

import java.util.List;

import me.lokka30.levelledmobs.result.NametagResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Holds data that goes into various queues for processing
 *
 * @author stumper66
 * @since 3.0.0
 */
public class QueueItem {

    public QueueItem(final LivingEntityWrapper lmEntity, final Event event) {
        this.lmEntity = lmEntity;
        this.event = event;
    }

    public QueueItem(final LivingEntityWrapper lmEntity, final @NotNull NametagResult nametag,
        final List<Player> players) {
        this.lmEntity = lmEntity;
        this.nametag = nametag;
        this.players = players;
    }

    public final LivingEntityWrapper lmEntity;
    public Event event;
    public List<Player> players;
    public NametagResult nametag;
}
