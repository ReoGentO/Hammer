package com.reogent.hammer.Utils;

import com.reogent.hammer.Hammer;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
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
}
