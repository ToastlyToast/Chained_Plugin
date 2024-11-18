package org.toastlytoast.chained_Plugin.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GroupManager
{
    private final Map<String, Group> groupManager;
    private final Map<Player, String> playerGroupMap;

    private final ArrayList<String> groupNames = new ArrayList<>();

    public GroupManager()
    {
        groupManager = new HashMap<>();
        playerGroupMap = new HashMap<>();
    }

    public ArrayList<String> getGroups()
    {
        if (groupManager.isEmpty()) return null;

        for (Group group : groupManager.values())
        {
            groupNames.add(group.getGroupName());
        }

        return groupNames;
    }

    public void addMemberToGroup(String name, Player player)
    {
        Group group = groupManager.get(name);

        if (group == null)
        {
            group = new Group(name);
            group.createGroup(name);
            groupManager.put(name, group);
        }
        group.addMember(player);
        playerGroupMap.put(player, name);
    }

    public void removeMemberFromGroup(String name, Player player)
    {
        Group group = groupManager.get(name);

        if (group == null)
        {
            Objects.requireNonNull(Bukkit
                .getPlayer(name)).sendMessage("§cPlayer " + player.getName() + " isn't in the group");
            return;
        }

        group.removeMember(player);
        playerGroupMap.remove(player);
    }

    public Set<Player> getPlayersInGroup(String name)
    {
        Group group = groupManager.get(name);
        if (group != null)
        {
            return group.getMembers();
        }
        else
        {
            return null;
        }
    }

    public void disbandGroup(String name)
    {
        getGroup(name).deleteGroup();
        groupManager.remove(name);
    }

    public String getCurrentGroup(Player player)
    {
        return playerGroupMap.get(player);
    }

    public Group getGroup(String name)
    {
        return groupManager.get(name);
    }

    public Player getOwner(String name)
    {
        return Bukkit.getPlayer(name);
    }

    public boolean groupExists(String name)
    {
        return groupManager.containsKey(name);
    }
}