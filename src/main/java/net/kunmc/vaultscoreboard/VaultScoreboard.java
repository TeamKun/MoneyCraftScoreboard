package net.kunmc.vaultscoreboard;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public final class VaultScoreboard extends JavaPlugin {

    private static Economy economy;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - no economy plugin found.", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
            Objective objective = sb.registerNewObjective("money", "dummy", getConfig().getString("displayname"));
        } catch (IllegalArgumentException e){
            getLogger().info("objective already exists");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player p:Bukkit.getOnlinePlayers()){
                    Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
                    Objective objective = sb.getObjective("money");
                    objective.getScore(p).setScore((int) getEconomy().getBalance(p));
                }
            }
        }.runTaskTimer(this,0,5);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Economy getEconomy() {
        return economy;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

}
