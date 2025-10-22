package me.dark_infect.specialevents.classes.FishModifer;
import org.bukkit.ChatColor;

public enum RarityTier {
    COMMON("Обычный", ChatColor.GRAY, 60.0, 3, 1.0),
    UNCOMMON("Необычный", ChatColor.GREEN, 25.0, 5, 1.5),
    RARE("Редкий", ChatColor.BLUE, 10.0, 7, 2.5),
    EPIC("Эпический", ChatColor.LIGHT_PURPLE, 4.0, 9, 5.0),
    LEGENDARY("Легендарный", ChatColor.GOLD, 1.0, 12, 10.0);

    private final String displayName;
    private final ChatColor color;
    private final double baseChance;
    private final int itemCount;
    private final double valueMultiplier;

    RarityTier(String displayName, ChatColor color, double baseChance,
               int itemCount, double valueMultiplier) {
        this.displayName = displayName;
        this.color = color;
        this.baseChance = baseChance;
        this.itemCount = itemCount;
        this.valueMultiplier = valueMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getColorCode() {
        return color.toString();
    }

    public double getBaseChance() {
        return baseChance;
    }

    public int getItemCount() {
        return itemCount;
    }

    public double getValueMultiplier() {
        return valueMultiplier;
    }

    public String getFormattedName() {
        return color + displayName;
    }

    public static RarityTier getByChance(double roll) {
        if (roll < LEGENDARY.baseChance) return LEGENDARY;
        if (roll < LEGENDARY.baseChance + EPIC.baseChance) return EPIC;
        if (roll < LEGENDARY.baseChance + EPIC.baseChance + RARE.baseChance) return RARE;
        if (roll < 100 - COMMON.baseChance) return UNCOMMON;
        return COMMON;
    }
}
