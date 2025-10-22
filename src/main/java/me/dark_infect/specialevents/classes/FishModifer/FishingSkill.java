package me.dark_infect.specialevents.classes.FishModifer;

import org.bukkit.ChatColor;

public enum FishingSkill {
    NOVICE(0, "Новичок", 0, 0, ChatColor.GRAY),
    APPRENTICE(100, "Подмастерье", 5, 5, ChatColor.GREEN),
    EXPERT(500, "Эксперт", 15, 10, ChatColor.BLUE),
    MASTER(2000, "Мастер", 30, 15, ChatColor.LIGHT_PURPLE),
    LEGEND(5000, "Легенда", 50, 20, ChatColor.GOLD);

    private final int requiredCatches;
    private final String displayName;
    private final int luckBonus;
    private final int extraReactionTicks;
    private final ChatColor color;

    FishingSkill(int requiredCatches, String displayName, int luckBonus,
                 int extraReactionTicks, ChatColor color) {
        this.requiredCatches = requiredCatches;
        this.displayName = displayName;
        this.luckBonus = luckBonus;
        this.extraReactionTicks = extraReactionTicks;
        this.color = color;
    }

    public int getRequiredCatches() {
        return requiredCatches;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLuckBonus() {
        return luckBonus;
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
