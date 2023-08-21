package cn.gionrose.facered.hook.papi

import cn.gionrose.facered.ProcessCraft
import cn.gionrose.facered.hook.Hook
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PapiHook: Hook
{
    var isLoad = false

    override fun load ()
    {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
        {
            ProcessCraft.INSTANCE.logger.info("PlaceholderApi 已加载就绪")
            isLoad = true
        }
    }

    fun parse (player: Player, target: String): String
    {
        if(!isLoad)
            return target
        return PlaceholderAPI.setPlaceholders(player, target)

    }

    fun parseAll (player: Player, target: List<String>): List<String>
    {
        val result = arrayListOf<String>()

        for (s in target) {
            result.add(parse(player, s))
        }
        return result
    }


}