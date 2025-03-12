package xyz.geik.farmer.modules.autoharvest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import xyz.geik.farmer.Main;
import xyz.geik.farmer.modules.FarmerModule;
import xyz.geik.farmer.modules.autoharvest.configuration.ConfigFile;
import xyz.geik.farmer.modules.autoharvest.handlers.AutoHarvestEvent;
import xyz.geik.farmer.modules.autoharvest.handlers.AutoHarvestGuiCreateEvent;
import xyz.geik.glib.GLib;
import xyz.geik.glib.chat.ChatUtils;
import xyz.geik.glib.shades.okaeri.configs.ConfigManager;
import xyz.geik.glib.shades.okaeri.configs.configurer.Configurer;
import xyz.geik.glib.shades.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import xyz.geik.glib.shades.xseries.XMaterial;

public class AutoHarvest extends FarmerModule {
    private static AutoHarvest instance;

    private static AutoHarvestEvent autoHarvestEvent;

    private static AutoHarvestGuiCreateEvent autoHarvestGuiCreateEvent;

    @Generated
    public static AutoHarvest getInstance() {
        return instance;
    }

    private boolean requirePiston = false;

    private boolean checkAllDirections = false;

    private boolean withoutFarmer = false;

    private boolean checkStock = true;

    @Generated
    public boolean isRequirePiston() {
        return this.requirePiston;
    }

    @Generated
    public boolean isCheckAllDirections() {
        return this.checkAllDirections;
    }

    @Generated
    public boolean isWithoutFarmer() {
        return this.withoutFarmer;
    }

    @Generated
    public boolean isCheckStock() {
        return this.checkStock;
    }

    private String customPerm = "farmer.autoharvest";

    @Generated
    public String getCustomPerm() {
        return this.customPerm;
    }

    private final List<String> crops = new ArrayList<>();

    private ConfigFile configFile;

    @Generated
    public List<String> getCrops() {
        return this.crops;
    }

    @Generated
    public ConfigFile getConfigFile() {
        return this.configFile;
    }

    public void onEnable() {
        instance = this;
        setLang(Main.getConfigFile().getSettings().getLang(), getClass());
        setupFile();
        if (this.configFile.isStatus()) {
            setHasGui(true);
            autoHarvestEvent = new AutoHarvestEvent();
            autoHarvestGuiCreateEvent = new AutoHarvestGuiCreateEvent();
            Bukkit.getPluginManager().registerEvents((Listener)autoHarvestEvent, (Plugin)Main.getInstance());
            Bukkit.getPluginManager().registerEvents((Listener)autoHarvestGuiCreateEvent, (Plugin)Main.getInstance());
            getCrops().addAll(this.configFile.getItems());
            this.requirePiston = this.configFile.isRequirePiston();
            this.checkAllDirections = this.configFile.isCheckAllDirections();
            this.withoutFarmer = this.configFile.isWithoutFarmer();
            this.checkStock = this.configFile.isCheckStock();
            this.customPerm = this.configFile.getCustomPerm();
            setDefaultState(this.configFile.isDefaultStatus());
            String messagex = "&3[" + GLib.getInstance().getName() + "] &a" + getName() + " enabled.";
            ChatUtils.sendMessage((CommandSender)Bukkit.getConsoleSender(), messagex);
        } else {
            String messagex = "&3[" + GLib.getInstance().getName() + "] &c" + getName() + " is not loaded.";
            ChatUtils.sendMessage((CommandSender)Bukkit.getConsoleSender(), messagex);
        }
    }

    public void onReload() {
        if (!isEnabled())
            return;
        if (!getCrops().isEmpty())
            getCrops().clear();
        getCrops().addAll(this.configFile.getItems());
        this.requirePiston = this.configFile.isRequirePiston();
        this.checkAllDirections = this.configFile.isCheckAllDirections();
        this.withoutFarmer = this.configFile.isWithoutFarmer();
        this.checkStock = this.configFile.isCheckStock();
        this.customPerm = this.configFile.getCustomPerm();
        setDefaultState(this.configFile.isDefaultStatus());
    }

    public void onDisable() {
        HandlerList.unregisterAll((Listener)autoHarvestEvent);
        HandlerList.unregisterAll((Listener)autoHarvestGuiCreateEvent);
    }

    public static boolean checkCrop(XMaterial material) {
        return getInstance().getCrops().stream().anyMatch(crop -> material.equals(XMaterial.valueOf(crop)));
    }

    public void setupFile() {
        this.configFile = (ConfigFile)ConfigManager.create(ConfigFile.class, it -> {
            it.withConfigurer((Configurer)new YamlBukkitConfigurer());
            it.withBindFile(new File(Main.getInstance().getDataFolder(), String.format("/modules/%s/config.yml", new Object[] { getName().toLowerCase() })));
            it.saveDefaults();
            it.load(true);
        });
    }
}
