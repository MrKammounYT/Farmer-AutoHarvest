package xyz.geik.farmer.modules.autoharvest;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import xyz.geik.farmer.Main;
import xyz.geik.farmer.modules.FarmerModule;
import xyz.geik.farmer.modules.autoharvest.configuration.ConfigFile;
import xyz.geik.farmer.modules.autoharvest.handlers.AutoHarvestEvent;
import xyz.geik.farmer.modules.autoharvest.handlers.AutoHarvestGuiCreateEvent;
import xyz.geik.glib.GLib;
import xyz.geik.glib.chat.ChatUtils;
import xyz.geik.glib.shades.okaeri.configs.ConfigManager;
import xyz.geik.glib.shades.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import xyz.geik.glib.shades.xseries.XMaterial;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * AutoHarvest module main class
 */
@Getter
public class AutoHarvest extends FarmerModule {

    /**
     * Constructor of class
     */
    public AutoHarvest() {}

    @Getter
    private static AutoHarvest instance;

    private static AutoHarvestEvent autoHarvestEvent;

    private static AutoHarvestGuiCreateEvent autoHarvestGuiCreateEvent;

    private boolean requirePiston = false, checkAllDirections = false, withoutFarmer = false, checkStock = true;

    private String customPerm = "farmer.autoharvest";

    private final List<String> crops = new ArrayList<>();

    private ConfigFile configFile;

    /**
     * onEnable method of module
     */
    @Override
    public void onEnable() {
        instance = this;
        this.setLang(Main.getConfigFile().getSettings().getLang(), this.getClass());
        setupFile();
        if (configFile.isStatus()) {
            this.setHasGui(true);
            autoHarvestEvent = new AutoHarvestEvent();
            autoHarvestGuiCreateEvent = new AutoHarvestGuiCreateEvent();
            Bukkit.getPluginManager().registerEvents(autoHarvestEvent, Main.getInstance());
            Bukkit.getPluginManager().registerEvents(autoHarvestGuiCreateEvent, Main.getInstance());
            getCrops().addAll(configFile.getItems());
            requirePiston = configFile.isRequirePiston();
            checkAllDirections = configFile.isCheckAllDirections();
            withoutFarmer = configFile.isWithoutFarmer();
            checkStock = configFile.isCheckStock();
            customPerm = configFile.getCustomPerm();
            setDefaultState(configFile.isDefaultStatus());
            String messagex = "&3[" + GLib.getInstance().getName() + "] &a" + getName() + " enabled.";
            ChatUtils.sendMessage(Bukkit.getConsoleSender(), messagex);
        }
        else {
            String messagex = "&3[" + GLib.getInstance().getName() + "] &c" + getName() + " is not loaded.";
            ChatUtils.sendMessage(Bukkit.getConsoleSender(), messagex);
        }
    }

    /**
     * onReload method of module
     */
    @Override
    public void onReload() {
        if (!this.isEnabled())
            return;
        if (!getCrops().isEmpty())
            getCrops().clear();
        getCrops().addAll(configFile.getItems());
        requirePiston = configFile.isRequirePiston();
        checkAllDirections = configFile.isCheckAllDirections();
        withoutFarmer = configFile.isWithoutFarmer();
        checkStock = configFile.isCheckStock();
        customPerm = configFile.getCustomPerm();
        setDefaultState(configFile.isDefaultStatus());
    }

    /**
     * onDisable method of module
     */
    @Override
    public void onDisable() {
        HandlerList.unregisterAll(autoHarvestEvent);
        HandlerList.unregisterAll(autoHarvestGuiCreateEvent);
    }

    /**
     * Checks if auto harvest collect this crop.
     *
     * @param material of crop
     * @return is crop can harvestable
     */
    public static boolean checkCrop(XMaterial material) {
        return getInstance().getCrops().stream().anyMatch(crop -> material.equals(XMaterial.valueOf(crop)));
    }

    public void setupFile() {
        configFile = ConfigManager.create(ConfigFile.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withBindFile(new File(Main.getInstance().getDataFolder(), String.format("/modules/%s/config.yml", getName().toLowerCase())));
            it.saveDefaults();
            it.load(true);
        });
    }

}
