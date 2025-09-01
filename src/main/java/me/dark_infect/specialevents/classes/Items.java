package me.dark_infect.specialevents.classes;

import me.dark_infect.specialevents.utils.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.List;
import java.util.Objects;

public class Items {
    private static final ItemStack SpecialEnder_eye = new ItemStack(Material.ENDER_EYE);
    private static final ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
    private static final ItemStack Chesplayte = new ItemStack(Material.NETHERITE_CHESTPLATE);
    private static final ItemStack Leggins = new ItemStack(Material.NETHERITE_LEGGINGS);
    private static final ItemStack Boots = new ItemStack(Material.NETHERITE_BOOTS);
    private static final ItemStack Sword = new ItemStack(Material.NETHERITE_SWORD);
    private static final ItemStack debugstick = new ItemStack(Material.STICK);
    private static final List<String> LoreEnd = List.of(
            "Око гнева.............."
    );
    public static void GenerateItems(){
        helmet.addEnchantment(Enchantment.PROTECTION, 3);
        helmet.addEnchantment(Enchantment.UNBREAKING,3);
        helmet.addEnchantment(Enchantment.THORNS,3);

        Chesplayte.addEnchantment(Enchantment.PROTECTION, 3);
        Chesplayte.addEnchantment(Enchantment.UNBREAKING,3);
        Chesplayte.addEnchantment(Enchantment.THORNS,3);

        Leggins.addEnchantment(Enchantment.PROTECTION, 3);
        Leggins.addEnchantment(Enchantment.UNBREAKING,3);
        Leggins.addEnchantment(Enchantment.THORNS,3);

        Boots.addEnchantment(Enchantment.PROTECTION, 3);
        Boots.addEnchantment(Enchantment.UNBREAKING,3);
        Boots.addEnchantment(Enchantment.THORNS,3);

        Sword.addEnchantment(Enchantment.SHARPNESS, 3);
        Sword.addEnchantment(Enchantment.UNBREAKING, 3);
        Sword.addEnchantment(Enchantment.SWEEPING_EDGE, 3);

            ItemMeta itemMeta = SpecialEnder_eye.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName("§5Древний Глаз");
            itemMeta.setLore(LoreEnd);
            SpecialEnder_eye.setItemMeta(itemMeta);

            debugstick.getItemMeta().setDisplayName(ChatColor.DARK_RED + "DebugStick");
            debugstick.getItemMeta().setLore(List.of("Debug"));

    }
    public static boolean PortalActivation(ItemStack item, List<String> Lore) {
        if (item == null || !item.hasItemMeta() || !Objects.requireNonNull(item.getItemMeta()).hasLore()) {
            return false;
        }
        return Objects.equals(item.getItemMeta().getLore(), Lore);
    }
    public static ItemStack GetSpecialEnderEye(){return SpecialEnder_eye;}
    public static List<String> GetLore(){
        return LoreEnd;
    }
    public static ItemStack getChesplayte(){
        return Chesplayte;
    }
    public static ItemStack getLeggins(){
        return Leggins;
    }
    public static ItemStack getHelmet(){
        return helmet;
    }
    public static ItemStack getBoots(){
        return Boots;
    }
    public static ItemStack getSword(){
        return Sword;
    }
}
