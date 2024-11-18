package org.toastlytoast.chained_Plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.toastlytoast.chained_Plugin.mechanics.GroupManager;
import java.util.Set;

public class ChainCommand implements CommandExecutor
{
    public static GroupManager groupManager = new GroupManager();

    private Player target;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
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
                case "help":
                    sender.sendMessage("Possible subcommands: invite, join, kick, leave, disband, display");
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

        if (groupManager.getCurrentGroup(requester) != null)
        {
            requester.sendMessage("§cYou must leave your current group");
        }
        else if (target == requester)
        {
            Join(requester, Bukkit.getPlayer(args[1]));
            return;
        }

        this.target = Bukkit.getPlayer(args[1]);

        if (target == null)
        {
            requester.sendMessage("§cThe player you've specified is not online");
        }
        else
        {
            target.sendMessage("§aYou have been invited to join §b" + requester.getName() + "§a's group");
            requester.sendMessage("§aYour invite to §b" + target.getName() + "§a has been sent");
        }
    }

    private void JoinCommand(CommandSender sender, String[] args)
    {
        if (target == null || target != sender)
        {
            sender.sendMessage("§cYou have not been requested to chain with anyone");
        }

        Player requester = Bukkit.getPlayer(args[1]);

        if (requester != null)
        {
            Join(sender, requester);
        }
        else
        {
            sender.sendMessage("§cThat player is not online");
        }

    }

    private void Join(CommandSender sender, Player requester)
    {
        Player player = (Player) sender;
        if (target == null) return;
        else if (groupManager.getCurrentGroup(player) != null)
        {
            player.sendMessage("§cYou must leave your current group to join another");
        }

        groupManager.addMemberToGroup(requester.getName(), requester);
        groupManager.addMemberToGroup(requester.getName(), (Player)sender);

        player.setHealth(requester.getHealth());
        player.setFoodLevel(requester.getFoodLevel());

        requester.sendMessage("§b" + player.getName() + "§a has joined the group");
        player.sendMessage("§aYou have joined §b" + requester.getName() + "§a's group");

        Bukkit.getServer().broadcastMessage("Group: " + groupManager.getCurrentGroup(requester));
        target = null;
    }

    private void LeaveCommand(CommandSender sender)
    {
        Player player = (Player) sender;
        String currentGroup = groupManager.getCurrentGroup(player);

        player.sendMessage("§cYou've left: §6" + currentGroup);
        groupManager.removeMemberFromGroup(currentGroup, player);
    }

    private void KickCommand(CommandSender sender, String[] args)
    {
        Player player = Bukkit.getPlayer(args[1]);
        String currentGroup = groupManager.getCurrentGroup((Player) sender);

        if (!groupManager.groupExists(currentGroup))
        {
            sender.sendMessage("§cYou're not in a group");
        }

        if (player == null)
        {
            sender.sendMessage("§cPlayer " + args[1] + " does not exist");
            return;
        }

        if (sender != groupManager.getOwner(currentGroup))
        {
            sender.sendMessage("§c§lYou do not have permission to use this command");
        }

        if (!groupManager.getGroup(currentGroup).hasMember(player))
        {
            sender.sendMessage("§cThis player is not in the chain");
        }

        groupManager.removeMemberFromGroup(sender.getName(), player);
        sender.sendMessage("§6" + player.getName() + "§c has been kicked");
        player.sendMessage("§cYou have been kicked from §6" + sender.getName() + "'s§c group");

        if (groupManager.getGroup(currentGroup).getMembers().size() == 1)
        {
            player.sendMessage("§3Group got disbanded because everyone else left");
            groupManager.disbandGroup(currentGroup);
        }
    }

    private void DisbandCommand(CommandSender sender)
    {
        Player player = (Player)sender;
        String currentGroup = groupManager.getCurrentGroup((Player)sender);

        if (currentGroup == null)
        {
            sender.sendMessage("§cYou aren't in a group right now");
            return;
        }
        else if (player != groupManager.getOwner(currentGroup))
        {
            sender.sendMessage("§c§lYou do not have permission to use this command");
        }

        sender.sendMessage("§aYou have disbanded the group");
        for (Player member : groupManager.getGroup(currentGroup).getMembers())
        {
            if (member != player)
            {
                member.sendMessage("§6" + currentGroup + " §r§chas been disbanded");
                groupManager.removeMemberFromGroup(currentGroup, member);
            }
        }
        groupManager.removeMemberFromGroup(currentGroup, player);
        groupManager.disbandGroup(currentGroup);
    }

    private void DisplayCommand(CommandSender sender)
    {
        String currentGroup = groupManager.getCurrentGroup((Player)sender);
        Set<Player> playerList = groupManager.getPlayersInGroup(currentGroup);

        if (currentGroup != null)
        {
            sender.sendMessage("Players in group: " + playerList);
        }
        else
        {
            sender.sendMessage("§cYou're not in a group");
        }
    }
}
