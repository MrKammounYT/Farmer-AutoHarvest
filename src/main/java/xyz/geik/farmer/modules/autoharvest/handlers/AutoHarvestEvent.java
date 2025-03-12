package xyz.geik.farmer.modules.autoharvest.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.MaterialData;
import xyz.geik.farmer.Main;
import xyz.geik.farmer.api.managers.FarmerManager;
import xyz.geik.farmer.helpers.WorldHelper;
import xyz.geik.farmer.model.Farmer;
import xyz.geik.farmer.model.inventory.FarmerInv;
import xyz.geik.farmer.model.inventory.FarmerItem;
import xyz.geik.farmer.modules.autoharvest.AutoHarvest;
import xyz.geik.farmer.shades.jetbrains.NotNull;
import xyz.geik.glib.shades.xseries.XMaterial;

public class AutoHarvestEvent implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHarvestGrowEvent(@NotNull BlockGrowEvent event) {
        Block block = event.getNewState().getBlock();
        XMaterial material = parseMaterial(XMaterial.matchXMaterial(event.getNewState().getType()));
        if (!WorldHelper.isFarmerAllowed(block.getWorld().getName()))
            return;
        if (!AutoHarvest.checkCrop(material))
            return;
        if (!AutoHarvest.getInstance().isWithoutFarmer())
            try {
                String regionID = Main.getIntegration().getRegionID(block.getLocation());
                if (regionID == null || !FarmerManager.getFarmers().containsKey(regionID))
                    return;
                Farmer farmer = (Farmer)FarmerManager.getFarmers().get(regionID);
                if (!farmer.getAttributeStatus("autoharvest"))
                    return;
                if (!hasStock(farmer, material)) {
                    event.setCancelled(true);
                    return;
                }
            } catch (Exception exception) {}
        if (AutoHarvest.getInstance().isRequirePiston() &&
                !pistonCheck(event.getBlock().getLocation()))
            return;
        if (harvestCrops(event.getNewState(), material))
            return;
        if (harvestCocoa(event.getNewState(), material))
            return;
        if (harvestBlocks(event.getNewState(), material, event))
            return;
    }

    private boolean hasStock(Farmer farmer, XMaterial material) {
        if (AutoHarvest.getInstance().isCheckStock()) {
            if (farmer.getAttributeStatus("autoseller"))
                return true;
            long capacity = farmer.getInv().getCapacity();
            FarmerItem item = farmer.getInv().getStockedItem(material);
            if (item.getAmount() == capacity)
                return false;
            XMaterial seed = hasSeed(material);
            if (!seed.equals(XMaterial.AIR) &&
                    FarmerInv.checkMaterial(seed.parseItem()))
                return hasStock(farmer, seed);
        }
        return true;
    }

    private boolean harvestBlocks(BlockState state, @NotNull XMaterial material, BlockGrowEvent event) {
        if (isBlockHarvestable(material)) {
            event.setCancelled(true);
            ItemStack item = null;
            if (material.equals(XMaterial.SWEET_BERRIES)) {
                item = material.parseItem();
                item.setAmount(3);
            } else {
                item = material.parseItem();
            }
            assert item != null;
            state.getWorld().dropItemNaturally(state.getLocation(), item);
            state.setType(Material.AIR);
            return true;
        }
        return false;
    }

    private boolean isBlockHarvestable(@NotNull XMaterial material) {
        return (material.equals(XMaterial.SUGAR_CANE) ||
                material.equals(XMaterial.MELON) ||
                material.equals(XMaterial.PUMPKIN) ||
                material.equals(XMaterial.CACTUS) ||
                material.equals(XMaterial.CHORUS_FLOWER) ||
                material.equals(XMaterial.CHORUS_PLANT));
    }

    private boolean isCropsHarvestable(@NotNull XMaterial material) {
        boolean status = (material.equals(XMaterial.valueOf("WHEAT")) ||
                material.equals(XMaterial.valueOf("CARROT")) ||
                material.equals(XMaterial.valueOf("POTATO")) ||
                material.equals(XMaterial.valueOf("BEETROOT"))
                || material.equals(XMaterial.valueOf("SWEET_BERRIES"))
                || material.equals(XMaterial.valueOf("NETHER_WART")));
        return status;
    }

    private XMaterial hasSeed(@NotNull XMaterial material) {
        if (material.equals(XMaterial.valueOf("WHEAT")))
            return XMaterial.valueOf("WHEAT_SEEDS");
        if (material.equals(XMaterial.valueOf("BEETROOT")))
            return XMaterial.valueOf("BEETROOT_SEEDS");
        return XMaterial.AIR;
    }

    private boolean harvestCrops(@NotNull BlockState state, @NotNull XMaterial material) {
        if (isCropsHarvestable(material)) {
            MaterialData data = state.getData();
            if (data.getData() == 7 || ((material
                    .equals(XMaterial.valueOf("NETHER_WART")) || material
                    .equals(XMaterial.valueOf("BEETROOT")) || material
                    .equals(XMaterial.valueOf("SWEET_BERRIES"))) && data
                    .getData() == 3)) {
                ItemStack item = material.parseItem();
                state.getWorld().dropItemNaturally(state.getLocation(), item);
                state.getBlock().getDrops().forEach(seed -> state.getWorld().dropItemNaturally(state.getLocation(), seed));
                data.setData((byte)0);
                state.setData(data);
            }
            return true;
        }
        return false;
    }

    private XMaterial parseMaterial(XMaterial material) {
        if (material.equals(XMaterial.valueOf("BEETROOTS"))) {
            material = XMaterial.valueOf("BEETROOT");
        } else if (material.equals(XMaterial.valueOf("POTATOES"))) {
            material = XMaterial.valueOf("POTATO");
        } else if (material.equals(XMaterial.valueOf("CARROTS"))) {
            material = XMaterial.valueOf("CARROT");
        } else if (material.equals(XMaterial.valueOf("SWEET_BERRY_BUSH"))) {
            material = XMaterial.valueOf("SWEET_BERRIES");
        } else if (material.equals(XMaterial.valueOf("COCOA"))) {
            material = XMaterial.valueOf("COCOA_BEANS");
        }
        return material;
    }

    private boolean harvestCocoa(BlockState state, @NotNull XMaterial material) {
        if (material.equals(XMaterial.valueOf("COCOA_BEANS"))) {
            CocoaPlant data = (CocoaPlant)state.getData();
            if (data.getSize().equals(CocoaPlant.CocoaPlantSize.LARGE)) {
                ItemStack item = material.parseItem();
                item.setAmount(3);
                state.getWorld().dropItemNaturally(state.getLocation(), item);
                data.setSize(CocoaPlant.CocoaPlantSize.SMALL);
                state.setRawData(data.getData());
            }
            return true;
        }
        return false;
    }

    private boolean pistonCheck(@NotNull Location location) {
        if (AutoHarvest.getInstance().isCheckAllDirections()) {
            Location loc1 = location.clone().add(-1.0D, 0.0D, 0.0D);
            Location loc2 = location.clone().add(1.0D, 0.0D, 0.0D);
            Location loc3 = location.clone().add(0.0D, 0.0D, -1.0D);
            Location loc4 = location.clone().add(0.0D, 0.0D, 1.0D);
            if (loc1.getBlock().getType().name().contains("PISTON"))
                return true;
            if (loc2.getBlock().getType().name().contains("PISTON"))
                return true;
            if (loc3.getBlock().getType().name().contains("PISTON"))
                return true;
            if (loc4.getBlock().getType().name().contains("PISTON"))
                return true;
        }
        Location loc = location.clone().add(0.0D, 1.0D, 0.0D);
        if (loc.getBlock().getType().name().contains("PISTON"))
            return true;
        return false;
    }
}
