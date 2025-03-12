package xyz.geik.farmer.modules.autoharvest.configuration;

import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import xyz.geik.glib.shades.okaeri.configs.OkaeriConfig;
import xyz.geik.glib.shades.okaeri.configs.annotation.Comment;
import xyz.geik.glib.shades.okaeri.configs.annotation.NameStrategy;
import xyz.geik.glib.shades.okaeri.configs.annotation.Names;

@Names(strategy = NameStrategy.IDENTITY)
public class ConfigFile extends OkaeriConfig {
    @Generated
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Generated
    public void setRequirePiston(boolean requirePiston) {
        this.requirePiston = requirePiston;
    }

    @Generated
    public void setCheckAllDirections(boolean checkAllDirections) {
        this.checkAllDirections = checkAllDirections;
    }

    @Generated
    public void setWithoutFarmer(boolean withoutFarmer) {
        this.withoutFarmer = withoutFarmer;
    }

    @Generated
    public void setCheckStock(boolean checkStock) {
        this.checkStock = checkStock;
    }

    @Generated
    public void setDefaultStatus(boolean defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    @Generated
    public void setCustomPerm(String customPerm) {
        this.customPerm = customPerm;
    }

    @Generated
    public void setItems(List<String> items) {
        this.items = items;
    }

    @Comment({"auto harvest crops addon harvest crops automatically", "if you want this module to be enabled, you can set this to true"})
    private boolean status = false;

    @Generated
    public boolean isStatus() {
        return this.status;
    }

    @Comment({"if you want to make a little difficult to players, you can set this to true", "also it can do 1 check for each crop can impact performance small amount, but it can be a problem if you have a lot of crops", "if you set this to true, the crops will be harvested only if the piston is top direction of the crops", "if you set this to false, the crops will be harvested immediately even there is no piston"})
    private boolean requirePiston = false;

    @Generated
    public boolean isRequirePiston() {
        return this.requirePiston;
    }

    @Comment({"this setting required requirePiston to be true to work", "recommended to set this to false if you have performance issues. (does 5 block check for each crop)", "if you set this to false, the crops will be harvested only if the piston is top direction of the crops", "if you set this to true, the crops will be harvested if the piston in any direction of the crops"})
    private boolean checkAllDirections = false;

    @Generated
    public boolean isCheckAllDirections() {
        return this.checkAllDirections;
    }

    @Comment({"Harvest crops without farmer"})
    private boolean withoutFarmer = false;

    @Generated
    public boolean isWithoutFarmer() {
        return this.withoutFarmer;
    }

    @Comment({"when stock is full, the crops cannot drop to ground", "recommended to set this to false if you have performance issues. (does 5 block check for each crop)", "if you set this to false, the crops will grow even if the stock is full.", "and the crops will be harvested and dropped on the ground."})
    private boolean checkStock = true;

    @Generated
    public boolean isCheckStock() {
        return this.checkStock;
    }

    @Comment({"default value of autoharvest module"})
    private boolean defaultStatus = false;

    @Generated
    public boolean isDefaultStatus() {
        return this.defaultStatus;
    }

    @Comment({"there is perm for use this module default: autoharvest.harvest"})
    private String customPerm = "farmer.autoharvest";

    @Generated
    public String getCustomPerm() {
        return this.customPerm;
    }

    @Comment({"write the crops you want to harvest here", "*IMPORTANT* Write only base item of crops. for example, if you want to harvest wheat, you must write WHEAT here", "if you want to harvest all crops, you must write all items here (*if you remove this setting, it can cause errors*)", "also you must write the crop names same as items.yml items.", "Available harvests: WHEAT, CARROT, POTATO, PUMPKIN, MELON, BEETROOT, NETHER_WART, SUGAR_CANE, COCOA_BEANS"})
    private List<String> items = Arrays.asList(new String[] { "WHEAT", "CARROT", "POTATO", "PUMPKIN" });

    @Generated
    public List<String> getItems() {
        return this.items;
    }
}
