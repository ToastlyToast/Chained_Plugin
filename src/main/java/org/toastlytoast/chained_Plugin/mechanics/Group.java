package org.toastlytoast.chained_Plugin.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Group
{
    private Set<Player> members;
    private String groupName;

    public Group(String groupName)
    {
        createGroup(groupName);
    }

    public void createGroup(String name)
    {
        groupName = name;
        this.members = new HashSet<>();
    }
    
    public void addMember(Player player)
    {
        if(!members.contains(player))
        {
            members.add(player);

        }else
        {
            Objects.requireNonNull(Bukkit.getServer().getPlayer(groupName)).sendMessage("§c§lThis player has already been added to your group");
        }
    }

    public void removeMember(Player player)
    {
        if(!members.contains(player))
        {
            Objects.requireNonNull(Bukkit.getPlayer(groupName)).sendMessage("§c§lThis player is not in the group");
        }
        else
        {
            members.remove(player);
        }
    }

    public Set<Player> getMembers()
    {
        return members;
    }

    public boolean hasMember(Player player)
    {
        return members.contains(player);
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void deleteGroup()
    {
        members.clear();
    }
}
