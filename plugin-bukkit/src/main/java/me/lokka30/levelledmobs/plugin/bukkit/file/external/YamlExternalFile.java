/*
 * This file is Copyright (c) 2020-2022 lokka30.
 * This file is/was present in the LevelledMobs resource.
 * Repository: <https://github.com/lokka30/LevelledMobs>
 * Use of this source code is governed by the GNU GPL v3.0
 * license that can be found in the LICENSE.md file.
 */

package me.lokka30.levelledmobs.plugin.bukkit.file.external;

import de.leonhard.storage.Yaml;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import me.lokka30.levelledmobs.plugin.bukkit.LevelledMobs;
import org.jetbrains.annotations.NotNull;

public interface YamlExternalFile extends ExternalFile {

    @NotNull
    Yaml getData();

    void migrate();

    @Override
    default String getName() {
        return getNameWithoutExtension() + ".yml";
    }

    default void backup() {
        final File from = new File(getFullPath());
        final File backupDirectory = new File(
            LevelledMobs.getInstance().getDataFolder() + File.separator + "backups");
        final File to = new File(
            backupDirectory.getPath()
                + File.separator
                + System.nanoTime()
                + File.separator + getName()
        );

        try {
            Files.copy(from.toPath(), to.toPath());
        } catch (IOException ex) {
            LevelledMobs.logger().severe("Unable to copy file '&b" + getName() + "&7': &f" + ex.getMessage());
        }
    }

}
