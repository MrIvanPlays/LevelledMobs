/*
 * Copyright (c) 2020-2021  lokka30. Use of this source code is governed by the GNU AGPL v3.0 license that can be found in the LICENSE.md file.
 */

package me.lokka30.levelledmobs.integrations;

import me.lokka30.levelledmobs.levelling.LevelledMob;
import org.bukkit.Bukkit;

/**
 * @author lokka30
 * @since v4.0.0
 * A LevelledMobs integration.
 * Plugin:     Lorinths' RPG Mobs
 * Author:     Lorinthio
 * Link:       https://www.spigotmc.org/resources/lorinthsrpgmobs.53568/
 */
public class LorinthsRPGMobsIntegration implements Integration, MobOwner {

    @Override
    public boolean isInstalled() {
        return Bukkit.getPluginManager().isPluginEnabled("LorinthsRpgMobs");
    }

    @Override
    public boolean isForceDisabled() {
        // TODO
        return false;
    }

    @Override
    public boolean isMobOwner(LevelledMob mob) {
        // TODO
        return false;
    }

}