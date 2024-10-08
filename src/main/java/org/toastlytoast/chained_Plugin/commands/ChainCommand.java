package org.toastlytoast.chained_Plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.toastlytoast.chained_Plugin.mechanics.GroupManager;

public class ChainCommand implements CommandExecutor
{
    public static GroupManager groupManager = new GroupManager();

    private Player target;

    // Cooldown stuff

    //private boolean inviting = false;

    //private boolean timeout = true;
    //private final HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            //if (!timeout && inviting) cooldown(requester);
            switch(args[0].toLowerCase())
            {
                case "invite":  InviteCommand(sender, args);
                    break;
                case "join":    JoinCommand(sender, args);
                    break;
                case "leave":   LeaveCommand(sender);
                    break;
                case "kick":    KickCommand(sender, args);
                    break;
                case "disband": DisbandCommand(sender);
                    break;
                case "display": DisplayCommand(sender);
                    break;
                default:
                    sender.sendMessage("This argument does not exist");
                    return false;
            }
        }
        else
        {
            System.out.println("This command can only be used by players");
        }
        return true;
    }

    private void InviteCommand(CommandSender sender, String[] args)
    {
        Player requester = (Player) sender;

        //if(!timeout)
        //{
            //return;
        //}

        if (groupManager.getCurrentGroup(requester) == null)
        {
            if (target == requester)
            {
                Join(requester, Bukkit.getPlayer(args[1]));
                return;
            }

            this.target = Bukkit.getPlayer(args[1]);

            if (target != null)
            {
                //timeout = false;
                target.sendMessage("§a§lYou have been invited to join §b§l" + requester.getName() + "§a§l's group");
                requester.sendMessage("§a§lYour invite to §b§l" + target.getName() + "§a§l has been sent");
            }
            else
            {
                requester.sendMessage("§c§lThe player you've specified is not online");
            }
        }
        else
        {
            requester.sendMessage("§c§lYou must leave your current group");
        }
    }

    /*private void cooldown(CommandSender sender)
    {
        Player player = (Player)sender;
        if(!cooldown.containsKey(player.getUniqueId()))
        {
            cooldown.put(player.getUniqueId(), System.currentTimeMillis());
        }
        else
        {
            long elapsedTime = System.currentTimeMillis() - cooldown.get(player.getUniqueId());

            if(elapsedTime >= 10000)
            {
                timeout = true;
                target = null;
                cooldown.put(player.getUniqueId(), System.currentTimeMillis());
            }
            else
            {
                timeout = false;
                sender.sendMessage("§c§lYou cannot use this command for §6§l" + (10 - elapsedTime / 1000) + "§c§l seconds");
            }
        }
    }*/

    private void JoinCommand(CommandSender sender, String[] args)
    {
        if(target != null)
        {
            if (target == sender)
            {
                Player requester = Bukkit.getPlayer(args[1]);

                if (requester != null)
                {
                    Join(sender, requester);
                }
                else
                {
                    sender.sendMessage("§c§lThat player is not online");
                }
            }
            else
            {
                sender.sendMessage("§c§lYou have not been requested to chain with anyone");
            }
        }
        else
        {
            sender.sendMessage("§c§lYou have not been requested to chain with anyone");
        }
    }

    private void Join(CommandSender sender, Player requester)
    {
        Player player = (Player) sender;
        if (target != null)
        {
            if(groupManager.getCurrentGroup(player) == null)
            {
                groupManager.addMemberToGroup(requester.getName(), requester);
                groupManager.addMemberToGroup(requester.getName(), (Player)sender);

                player.setHealth(requester.getHealth());
                player.setFoodLevel(requester.getFoodLevel());

                requester.sendMessage("§b§l" + player.getName() + "§a§l has joined the group");
                player.sendMessage("§a§lYou have joined §b§l" + requester.getName() + "§a§l's group");

                Bukkit.getServer().broadcastMessage("Group: " + groupManager.getCurrentGroup(requester));
                target = null;
            }
            else
            {
                player.sendMessage("§c§lYou must leave your current group to join another");
            }
        }
    }

    private void LeaveCommand(CommandSender sender)
    {
        Player player = (Player) sender;
        String currentGroup = groupManager.getCurrentGroup(player);

        player.sendMessage("§c§lYou've left: §6§l" + currentGroup);
        groupManager.removeMemberFromGroup(currentGroup, player);
    }

    private void KickCommand(CommandSender sender, String[] args)
    {
        Player player = Bukkit.getPlayer(args[1]);
        String currentGroup = groupManager.getCurrentGroup((Player) sender);

        if (groupManager.groupExists(currentGroup))
        {
            if(player == null) return;

            if(sender == groupManager.getOwner(groupManager.getCurrentGroup((Player)sender)))
            {
                if(groupManager.getGroup(currentGroup).hasMember(player))
                {
                    groupManager.removeMemberFromGroup(sender.getName(), player);
                    sender.sendMessage("§6§l" + player.getName() + "§c§l has been kicked");
                    player.sendMessage("§c§lYou have been kicked from §6§l" + sender.getName() + "'s§c§l group");

                    if (groupManager.getGroup(currentGroup).getMembers().size() == 1)
                    {
                        groupManager.disbandGroup(currentGroup);
                    }
                }
                else
                {
                    sender.sendMessage("§c§lThis player is not in the chain");
                }
            }
            else
            {
                sender.sendMessage("§c§lYou do not have permission to use this command");
            }
        }
        else
        {
            sender.sendMessage("§c§lYou're not in a group");
        }
    }

    private void DisbandCommand(CommandSender sender)
    {
        Player player = (Player)sender;
        String currentGroup = groupManager.getCurrentGroup((Player)sender);

        if (player == groupManager.getOwner(currentGroup))
        {
            sender.sendMessage("§a§lYou have disbanded the group");
            for (Player member : groupManager.getGroup(currentGroup).getMembers())
            {
                if(member != player)
                {
                    member.sendMessage("§6§l" + currentGroup + " §c§lhas been disbanded");
                    groupManager.removeMemberFromGroup(currentGroup, member);
                }
            }
            groupManager.removeMemberFromGroup(currentGroup, player);
            groupManager.disbandGroup(currentGroup);
        }
        else
        {
            sender.sendMessage("§c§lYou do not have permission to use this command");
        }
    }

    private void DisplayCommand(CommandSender sender)
    {
        String currentGroup = groupManager.getCurrentGroup((Player)sender);
        if (currentGroup != null)
        {
            sender.sendMessage("Players in group: " + groupManager.getPlayersInGroup(groupManager.getCurrentGroup((Player) sender)));
        }
        else
        {
            sender.sendMessage("§c§lYou're not in a group");
        }
    }
}
