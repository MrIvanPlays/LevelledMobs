package me.lokka30.levelledmobs.bukkit.logic.context;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import me.lokka30.levelledmobs.bukkit.LevelledMobs;
import me.lokka30.levelledmobs.bukkit.logic.function.LmFunction;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class Context {

    /* vars */

    private Entity entity;
    private EntityType entityType;
    private Player player;
    private World world;
    private Location location;
    private final List<LmFunction> linkedFunctions = new LinkedList<>();

    /* methods */

    public String replacePlaceholders(final String from) {
        return LevelledMobs.getInstance()
            .getLogicHandler()
            .getContextPlaceholderHandler()
            .replace(from, this);
    }

    /* getters and setters */

    @NotNull
    public Context withEntity(final @NotNull Entity entity) {
        this.entity = Objects.requireNonNull(entity, "entity");

        return this
            .withEntityType(entity.getType())
            .withLocation(entity.getLocation());
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }

    @NotNull
    public Context withEntityType(final @NotNull EntityType entityType) {
        this.entityType = Objects.requireNonNull(entityType, "entityType");
        return this;
    }

    @Nullable
    public EntityType getEntityType() {
        return entityType;
    }

    @NotNull
    public Context withPlayer(final @NotNull Player player) {
        this.player = Objects.requireNonNull(player, "player");
        return this;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public Context withWorld(final @NotNull World world){
        this.world = Objects.requireNonNull(world, "world");
        return this;
    }

    @Nullable
    public World getWorld() {
        return world;
    }

    @NotNull
    public Context withLocation(final @NotNull Location location){
        this.location = Objects.requireNonNull(location, "location");

        if(location.getWorld() != null) {
            withWorld(location.getWorld());
        }

        return this;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Context withLinkedFunction(final LmFunction linkedFunction) {
        getLinkedFunctions().add(Objects.requireNonNull(linkedFunction, "linkedFunction"));
        return this;
    }

    @NotNull
    public List<LmFunction> getLinkedFunctions() { return linkedFunctions; }

}
