package cn.gionrose.facered.listenner

import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

object ListenerRegistrator
{

    fun register (plugin: JavaPlugin, listener: Listener)
    {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
    fun unregister (listener: Listener)
    {
        HandlerList.unregisterAll(listener);
    }
}