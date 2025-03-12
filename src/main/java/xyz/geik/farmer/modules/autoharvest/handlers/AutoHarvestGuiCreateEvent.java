package xyz.geik.farmer.modules.autoharvest.handlers;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.geik.farmer.api.handlers.FarmerModuleGuiCreateEvent;
import xyz.geik.farmer.helpers.gui.GuiHelper;
import xyz.geik.farmer.model.Farmer;
import xyz.geik.farmer.modules.autoharvest.AutoHarvest;
import xyz.geik.farmer.shades.jetbrains.NotNull;
import xyz.geik.glib.shades.inventorygui.DynamicGuiElement;
import xyz.geik.glib.shades.inventorygui.GuiElement;
import xyz.geik.glib.shades.inventorygui.StaticGuiElement;

public class AutoHarvestGuiCreateEvent implements Listener {
    @EventHandler
    public void onGuiCreateEvent(@NotNull FarmerModuleGuiCreateEvent e) {
        char icon = AutoHarvest.getInstance().getLang().getString("moduleGui.icon.guiInterface").charAt(0);
        e.getGui().addElement(
                new DynamicGuiElement(icon, (viewer) ->
                        new StaticGuiElement(
                                icon,
                                // Item here
                                getGuiItem(e.getFarmer()),
                                1,
                                // Event written bottom
                                click -> {
                                    // If player don't have permission do nothing
                                    if (!e.getPlayer().hasPermission(AutoHarvest.getInstance().getCustomPerm()))
                                        return true;
                                    // Change attribute
                                    e.getFarmer().changeAttribute("autoharvest");
                                    e.getGui().draw();
                                    return true;
                                })
                )
        );
    }

    @NotNull
    private ItemStack getGuiItem(@NotNull Farmer farmer) {
        ItemStack item = GuiHelper.getItem("moduleGui.icon", AutoHarvest.getInstance().getLang());
        ItemMeta meta = item.getItemMeta();
        String status = farmer.getAttributeStatus("autoharvest") ? AutoHarvest.getInstance().getLang().getText("enabled") : AutoHarvest.getInstance().getLang().getText("disabled");
        meta.setLore((List)meta.getLore().stream().map(line -> line.replace("{status}", status))
                .collect(Collectors.toList()));
        item.setItemMeta(meta);
        return item;
    }
}
