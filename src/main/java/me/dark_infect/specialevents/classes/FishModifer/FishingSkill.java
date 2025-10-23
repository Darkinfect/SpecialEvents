package me.dark_infect.specialevents.classes.FishModifer;

import me.dark_infect.specialevents.SpecialEvents;
import me.dark_infect.specialevents.utils.Plugininit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public enum FishingSkill {
    NOVICE(0, "Новичок", 0, ChatColor.GRAY),
    APPRENTICE(100, "Подмастерье", 5, ChatColor.GREEN),
    EXPERT(500, "Эксперт", 10, ChatColor.BLUE),
    MASTER(2000, "Мастер", 15, ChatColor.LIGHT_PURPLE),
    LEGEND(5000, "Легенда", 20, ChatColor.GOLD);

    private final int requiredCatches;
    private final String displayName;
    private final int extraReactionTicks;
    private final ChatColor color;

    FishingSkill(int requiredCatches, String displayName,
                 int extraReactionTicks, ChatColor color) {
        this.requiredCatches = requiredCatches;
        this.displayName = displayName;
        this.extraReactionTicks = extraReactionTicks;
        this.color = color;
    }

    public int getRequiredCatches() {
        return requiredCatches;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Бонус удачи теперь берётся из конфига
    public int getLuckBonus() {
        JavaPlugin plugin = SpecialEvents.getInstance();
        if (plugin == null) return 0;

        String configPath = "bonus-chances.skill." + this.name().toLowerCase();
        return plugin.getConfig().getInt(configPath, 0);
    }

    public int getExtraReactionTicks() {
        return extraReactionTicks;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getFormattedName() {
        return color + displayName;
    }

    public static FishingSkill getByProgress(int catches) {
        FishingSkill result = NOVICE;
        for (FishingSkill skill : values()) {
            if (catches >= skill.requiredCatches) {
                result = skill;
            }
        }
        return result;
    }

    public FishingSkill getNext() {
        int nextOrdinal = ordinal() + 1;
        if (nextOrdinal >= values().length) {
            return this;
        }
        return values()[nextOrdinal];
    }

    public int getProgressToNext(int currentCatches) {
        FishingSkill next = getNext();
        if (next == this) return 100;

        int progressNeeded = next.requiredCatches - this.requiredCatches;
        int currentProgress = currentCatches - this.requiredCatches;

        return Math.min(100, (currentProgress * 100) / progressNeeded);
    }
}
