package cn.gionrose.facered.listenner

import cn.gionrose.facered.database.CraftTaskStorage
import cn.gionrose.facered.task.TaskHolderManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

object AboutPlayerListener: Listener
{

    fun register (plugin: JavaPlugin)
    {
        ListenerRegistrator.register (plugin, this)
    }

    @EventHandler
    fun playerLeave (event: PlayerQuitEvent)
    {
        val player = event.player
        val holder = TaskHolderManager.getHolderByPlayer(player)
        holder.takeIf { !it.isEmpty() }?.let {
            CraftTaskStorage.save(it)
        }

        TaskHolderManager.clearTaskByPlayer(player)
        TaskHolderManager.removeHolderIfTaskIsEmpty(player)
    }

    @EventHandler
    fun playerJoin (event: PlayerJoinEvent)
    {
        val player = event.player
        val holder = CraftTaskStorage.load(player)
        TaskHolderManager.addHolder(holder)
        CraftTaskStorage.deleteByPlayer(player)
    }

}