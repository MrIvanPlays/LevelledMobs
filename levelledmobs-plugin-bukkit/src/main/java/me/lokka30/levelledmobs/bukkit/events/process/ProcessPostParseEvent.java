package me.lokka30.levelledmobs.bukkit.events.process;

import me.lokka30.levelledmobs.bukkit.logic.Process;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ProcessPostParseEvent extends CancellableProcessEvent {

    /* vars */

    private static final HandlerList HANDLERS = new HandlerList();

    /* constructors */

    public ProcessPostParseEvent(final @NotNull Process process) {
        super(process);
    }

    /* getters and setters */

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
