/*
 * This file is Copyright (c) 2020-2022 lokka30.
 * This file is/was present in the LevelledMobs resource.
 * Repository: <https://github.com/lokka30/LevelledMobs>
 * Use of this source code is governed by the GNU GPL v3.0
 * license that can be found in the LICENSE.md file.
 */

package me.lokka30.levelledmobs.plugin.bukkit.rule;

import java.util.ArrayList;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public record RuleCluster(
    @NotNull String identifier,
    boolean enabled,
    @NotNull Optional<String> description,
    @NotNull ArrayList<Rule> rules
) implements RuleEntry {

}
