package theeluke.referrals;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Ref;

public final class Main extends JavaPlugin {


    //##############################################
    //##############################################
    //##############################################
    //PLUGIN MADE BY THEELUKE :) ENJOY!
    ////////////////////////////////////////////////

    String prefix;
    ReferralFile file;
    private static Economy econ = null;

    public Main() {
        prefix = getConfig().getString("prefix");
    }
    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        getCommand("ref").setExecutor(new RefCmd(this));

        if(!getDataFolder().exists()) {getDataFolder().mkdir();}
        this.file = new ReferralFile(this);

        if (!setupEconomy() ) {
            System.out.println(getConfig().get("config-prefix") + " No Vault plugin found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


    }

    @Override
    public void onDisable() {
        saveConfig();


    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public String getPrefix() {
        return prefix;
    }
}
