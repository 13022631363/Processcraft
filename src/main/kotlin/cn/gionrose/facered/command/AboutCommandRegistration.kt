package cn.gionrose.facered.command

import org.bukkit.Bukkit
import org.bukkit.command.TabExecutor

object AboutCommandRegistration
{
    fun register (commandName: String, commandTabExecutor: TabExecutor)
    {
        Bukkit.getPluginCommand(commandName)?.setExecutor(commandTabExecutor)
        Bukkit.getPluginCommand(commandName)?.tabCompleter = commandTabExecutor
    }
}