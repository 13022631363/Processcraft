package cn.gionrose.facered.hook

import cn.gionrose.facered.ProcessCraft
import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PlayerPointsHook: Hook
{
    var isLoad = false

    lateinit var  api :PlayerPointsAPI

    override fun load ()
    {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints"))
        {
            api = PlayerPoints.getInstance().api
            ProcessCraft.INSTANCE.logger.info("PlayerPoints 已加载就绪")
            isLoad = true
        }
    }

    fun addOrSubtractPoint (player: Player, amount: Int)
    {
        if (isLoad)
            api.give(player.uniqueId, amount)
    }

    fun getPlayerPoint (player: Player): Int
    {
        if (isLoad)
            return api.look(player.uniqueId)
        return -1
    }

}