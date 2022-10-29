package io.github.arcaneplugins.levelledmobs.bukkit.listener;

import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.Action;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.ActionParseEvent;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.AddNbtTagAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.BroadcastMessageToNearbyPlayersAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.BroadcastMessageToServerAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.BroadcastMessageToWorldAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.ExitAllAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.ExitFunctionAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.ExitProcessAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.RunFunctionAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.SetBuffsAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.SetDeathLabelAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.SetDropTableIdAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.SetPacketLabelAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.SetPermanentLabelAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.TestAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.UpdateLabelsAction;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.setlevel.SetLevelAction;
import java.util.Locale;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

public class ActionParseListener extends ListenerWrapper {

    /* constructor */

    public ActionParseListener() {
        super(true);
    }

    /* methods */

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onActionParse(final @NotNull ActionParseEvent event) {
        final var process = event.getProcess();
        final var node = event.getNode();

        switch (event.getIdentifier().toLowerCase(Locale.ROOT)) {
            case "add-nbt-tag" -> addAction(event, new AddNbtTagAction(process, node));
            case "broadcast-message-to-nearby-players" -> addAction(event, new BroadcastMessageToNearbyPlayersAction(process, node));
            case "broadcast-message-to-server" -> addAction(event, new BroadcastMessageToServerAction(process, node));
            case "broadcast-message-to-world" -> addAction(event, new BroadcastMessageToWorldAction(process, node));
            case "exit-all" -> addAction(event, new ExitAllAction(process, node));
            case "exit-function" -> addAction(event, new ExitFunctionAction(process, node));
            case "exit-process" -> addAction(event, new ExitProcessAction(process, node));
            case "run-function" -> addAction(event, new RunFunctionAction(process, node));
            case "set-buffs" -> addAction(event, new SetBuffsAction(process, node));
            case "set-death-label" -> addAction(event, new SetDeathLabelAction(process, node));
            case "set-drop-table-id" -> addAction(event, new SetDropTableIdAction(process, node));
            case "set-level" -> addAction(event, new SetLevelAction(process, node));
            case "set-packet-label" -> addAction(event, new SetPacketLabelAction(process, node));
            case "set-permanent-label" -> addAction(event, new SetPermanentLabelAction(process, node));
            case "test" -> addAction(event, new TestAction(process, node));
            case "update-labels" -> addAction(event, new UpdateLabelsAction(process, node));
        }
    }

    private void addAction(final @NotNull ActionParseEvent event, final @NotNull Action action) {
        event.getProcess().getActions().add(action);
        event.setClaimed(true);
    }
}
