package org.toastlytoast.chained_Plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.toastlytoast.chained_Plugin.commands.ChainCommand;
import org.toastlytoast.chained_Plugin.gameplay.LinkPlayers;

import java.util.Objects;

public final class Chained_Plugin extends JavaPlugin
{
    public static Chained_Plugin instance;

    public static Chained_Plugin getInstance()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
        instance = this;

        System.out.println("The plugin is enabled");

        Objects.requireNonNull(getCommand("chain")).setExecutor(new ChainCommand());

        Bukkit.getPluginManager().registerEvents(new LinkPlayers(), this);
        new LinkPlayers.ValueTracker().runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable()
    {
        instance = null;
        System.out.println("The plugin is disabled");
    }
}
