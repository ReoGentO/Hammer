package com.reogent.hammer.Utils;

import com.reogent.hammer.Hammer;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemUtils {
    private static final Hammer inst = Hammer.getInstance();
    private final LegacyComponentSerializer mmlegacy = LegacyComponentSerializer.legacySection().toBuilder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();;
    private static final MiniMessage mm = MiniMessage.miniMessage();

    private static final BukkitAudiences audiences = inst.getAudiences();

    /**
     * Выдает предмет
     *
     * @param player            Игрок (PlayerName)
     * @param name              Название предмета
     * @param lore              Описание предмета
     * @param material          Тип предмета (ex: Material.STONE)
     * @param nbtModifier       NBT ( ex: nbt -> { nbt.setInteger("CustomModelData", 100); } )
     */
    public void giveItem(String player, String name, String lore, Material material, Consumer<ReadWriteItemNBT> nbtModifier, Map<Attribute, Double> attributes, String attributeslot) {
        ItemStack item = new ItemStack(material, 1);
        NBTItem nbtItem = new NBTItem(item);

        if (name != null && !name.isEmpty()) {
            // Установка имени
            NBTCompound display = nbtItem.addCompound("display");
            display.setObject("Name", ChatColor.translateAlternateColorCodes('&', name));

            if (lore != null && !lore.isEmpty()) {
                NBTList<String> nbtList = display.getStringList("Lore");
                List<String> loreStrings = Arrays.stream(lore.split("\\n"))
                        .map(line -> "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', line) + "\"}")
                        .collect(Collectors.toList());

                for(String loreString : loreStrings){
                    nbtList.add(loreString);
                }
            }

        }

        if(attributes != null && !attributes.isEmpty()){
            ReadWriteNBTCompoundList  modifiers = nbtItem.getCompoundList("AttributeModifiers");

            for (Map.Entry<Attribute, Double> entry : attributes.entrySet()) {
                ReadWriteNBT  modifier = modifiers.addCompound();
                modifier.setString("AttributeName", entry.getKey().getKey().getKey()); // Используем Key для получения String ID
                modifier.setString("Name", "attribute_modifier");
                modifier.setInteger("Operation", 0); // 0 for addition, 1 for multiplication, 2 for base multiplier
                modifier.setDouble("Amount", entry.getValue());
                modifier.setString("Slot", attributeslot); // -1 for any slot
                UUID uuid = UUID.randomUUID(); // Создаем новый UUID для каждого атрибута
                modifier.setIntArray("UUID", new int[] {
                        (int) (uuid.getMostSignificantBits() >> 32),
                        (int) uuid.getMostSignificantBits(),
                        (int) (uuid.getLeastSignificantBits() >> 32),
                        (int) uuid.getLeastSignificantBits()
                });
            }
        }

        // Установка NBT
        if (nbtModifier != null) {
            nbtModifier.accept(nbtItem);
        }

        // Выдача предмета
        item = nbtItem.getItem();
        Bukkit.getPlayer(player).getInventory().addItem(item);
    }

    public void giveItem(String player, String name, String lore, Material material) {
        giveItem(player, name, lore, material, null, null, null);
    }

    public void giveItem(String player, Material material) {
        giveItem(player, null, null, material, null, null, null);
    }

    /**
     * Добавляет поддержку HEX-кодов через <#123456> и градиент через <g:#123456;...>
     *
     * @param msg            Сообщение, текст и т.п
     */
    public static String hex(String msg) {
        String version = Bukkit.getServer().getBukkitVersion();
        if (version.startsWith("1.15") || version.startsWith("1.14") || version.startsWith("1.13") || version.startsWith("1.12") || version.startsWith("1.11") || version.startsWith("1.10") || version.startsWith("1.9") || version.startsWith("1.8")) {
            return ChatColor.translateAlternateColorCodes('&', msg);
        } else {
            msg = ChatColor.translateAlternateColorCodes('&', msg);
            msg = handleGradients(msg);
            msg = handleSingleHexColors(msg);
            return msg;
        }
    }

    private static String handleSingleHexColors(String msg) {
        Matcher matcher = Pattern.compile("<(#[A-Fa-f0-9]{6})>").matcher(msg);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            String replacement = ChatColor.of(hex).toString();
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String handleGradients(String msg) {
        Pattern gradientPattern = Pattern.compile("<g:((#[A-Fa-f0-9]{6};*)+)>([^<]+)");
        Matcher matcher = gradientPattern.matcher(msg);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String fullMatch = matcher.group(0);
            String colors = matcher.group(1);
            String text = matcher.group(3);
            String[] hexColors = colors.split(";", -1);

            if (hexColors.length < 2) {
                matcher.appendReplacement(sb, fullMatch);
                continue;
            }

            String replacement = applyGradient(text, hexColors);
            matcher.appendReplacement(sb, replacement);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String applyGradient(String text, String[] hexColors) {
        StringBuilder gradientText = new StringBuilder();
        if (text.isEmpty()) return "";

        int length = text.length();
        for (int i = 0; i < length; i++) {
            double progress = (double) i / (length - 1);
            int colorIndex = Math.min((int) Math.floor(progress * (hexColors.length - 1)), hexColors.length - 2);

            String startColor = hexColors[colorIndex];
            String endColor = hexColors[colorIndex + 1];

            ChatColor start = ChatColor.of(startColor);
            ChatColor end = ChatColor.of(endColor);

            double progressBetweenColors = (progress * (hexColors.length - 1)) - colorIndex;

            String colorizedChar = getInterpolatedColor(start, end, progressBetweenColors) + "" + text.charAt(i);

            gradientText.append(colorizedChar);
        }
        return gradientText.toString();
    }
    private static ChatColor getInterpolatedColor(ChatColor start, ChatColor end, double progress) {
        int red = (int) (start.getColor().getRed() + (end.getColor().getRed() - start.getColor().getRed()) * progress);
        int green = (int) (start.getColor().getGreen() + (end.getColor().getGreen() - start.getColor().getGreen()) * progress);
        int blue = (int) (start.getColor().getBlue() + (end.getColor().getBlue() - start.getColor().getBlue()) * progress);

        return ChatColor.of(String.format("#%02x%02x%02x", red, green, blue));
    }
}
