package com.sugar_tree.showoffitems;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

public class Listeners implements Listener {

    private static final Component SHOW_OFF_TITLE = Component.text(ChatColor.GREEN + "자랑 할 아이템을 올려주세요!");

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().title().equals(SHOW_OFF_TITLE)) return;

        Inventory inv = event.getInventory();
        ItemStack item = inv.getItem(4);

        if (item != null && !item.getType().isAir()) {
            Player player = (Player) event.getPlayer();

            // 자랑 메시지 브로드캐스트
            Component message = player.displayName()
                .append(Component.text("님이 "))
                .append(item.displayName())
                .append(Component.text(" x" + item.getAmount() + " 을(를) 자랑합니다!"));
            Bukkit.broadcast(message);

            // 아이템 지급 시도
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item.clone());
            if (!leftovers.isEmpty()) {
                for (ItemStack leftover : leftovers.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                }
                player.sendMessage(ChatColor.YELLOW + "인벤토리가 가득 차서 아이템 일부가 바닥에 떨어졌습니다!");
            }

            // 슬롯 정리
            inv.setItem(4, null);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getWhoClicked().getOpenInventory().title().equals(SHOW_OFF_TITLE)) return;

        if (event.getClick() == ClickType.NUMBER_KEY) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.CHEST) {
            if (event.getSlot() != 4 || event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
                event.setCancelled(true);
            }
        }
    }
}
