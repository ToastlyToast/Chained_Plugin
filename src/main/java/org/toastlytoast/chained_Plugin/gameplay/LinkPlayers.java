package org.toastlytoast.chained_Plugin.gameplay;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.toastlytoast.chained_Plugin.commands.ChainCommand;
import org.toastlytoast.chained_Plugin.mechanics.GroupManager;

public class LinkPlayers implements Listener
{
    static GroupManager groupManager = ChainCommand.groupManager;
    static Player player;
    static String currentGroup;

    final double MAX_HEALTH = 20;
    final int MAX_FOOD_LEVEL = 20;

    static double health = 20;
    static int foodLevel = 20;

    boolean allMembersAlive = true;
    int playerAliveCount = 0;

    @EventHandler
    private void FoodLevelChangeEvent(FoodLevelChangeEvent event)
    {
        if (!allMembersAlive) return;
        player = (Player) event.getEntity();
        currentGroup = groupManager.getCurrentGroup(player);

        if (currentGroup != null)
        {
            foodLevel = event.getFoodLevel();
        }
    }

    @EventHandler
    private void EntityRegainHealthEvent(EntityRegainHealthEvent event)
    {
        if (!allMembersAlive) return;
        if(!(event.getEntity() instanceof Player)) return;

        player = (Player) event.getEntity();
        currentGroup = groupManager.getCurrentGroup(player);

        if(currentGroup != null)
        {
            health = health + event.getAmount();
        }
    }

    @EventHandler
    private void EntityDamageEvent(EntityDamageEvent event)
    {
        if(!(event.getEntity() instanceof Player)) return;
        if (!allMembersAlive) return;

        player = (Player) event.getEntity();
        currentGroup = groupManager.getCurrentGroup(player);

        if(currentGroup != null)
        {
            health = health - event.getDamage();
            groupManager.getGroup(currentGroup).getMembers().forEach(p ->
            {
                if (p != player)
                {
                    p.sendHurtAnimation(100);
                    p.playSound(p, Sound.ENTITY_PLAYER_HURT, 100, 1);
                }
            });
        }
    }

    @EventHandler
    private void PlayerRespawnEvent(PlayerRespawnEvent event)
    {
        player = event.getPlayer();
        currentGroup = groupManager.getCurrentGroup(player);

        if(currentGroup != null)
        {
            playerAliveCount += 1;
            if (playerAliveCount == groupManager.getGroup(currentGroup).getMembers().size())
            {
                health = MAX_HEALTH;
                foodLevel = MAX_FOOD_LEVEL;
                allMembersAlive = true;
            }
        }
    }

    @EventHandler
    private void EntityDeathEvent(EntityDeathEvent event)
    {
        if(!(event.getEntity() instanceof Player)) return;

        player = (Player) event.getEntity();
        currentGroup = groupManager.getCurrentGroup(player);

        if(currentGroup != null)
        {
            groupManager.getGroup(currentGroup).getMembers().forEach(p ->
            {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill " + p.getName());
            });
            playerAliveCount = 0;
            allMembersAlive = false;
        }
    }

    // Value Tracker...
    public static class ValueTracker extends BukkitRunnable
    {
        @Override
        public void run()
        {
            if(currentGroup != null)
            {
                double playerHealth = health;
                int playerHunger = foodLevel;

                groupManager.getGroup(currentGroup).getMembers().forEach( p ->
                {
                    p.setHealth(playerHealth);
                    p.setFoodLevel(playerHunger);
                    p.setSaturation(0);
                });
                currentGroup = null;
            }
        }
    }
}
