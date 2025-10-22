package me.dark_infect.specialevents.classes.FishModifer;
import org.bukkit.entity.FishHook;
import java.util.UUID;

public class FishingAttempt {

    private final UUID playerId;
    private final long biteTime;
    private final int reactionWindow;
    private final FishHook hook;
    private final int taskId;

    public FishingAttempt(UUID playerId, long biteTime, int reactionWindow,
                          FishHook hook, int taskId) {
        this.playerId = playerId;
        this.biteTime = biteTime;
        this.reactionWindow = reactionWindow;
        this.hook = hook;
        this.taskId = taskId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public long getBiteTime() {
        return biteTime;
    }

    public int getReactionWindow() {
        return reactionWindow;
    }

    public FishHook getHook() {
        return hook;
    }

    public int getTaskId() {
        return taskId;
    }

    public long getReactionTime() {
        return System.currentTimeMillis() - biteTime;
    }

    public int getReactionTicks() {
        return (int)(getReactionTime() / 50);
    }

    public boolean isSuccessful() {
        return getReactionTicks() <= reactionWindow;
    }
}

