package cn.gionrose.facered.hook

import cn.gionrose.facered.ProcessCraft
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object VaultHook: Hook
{
    var isLoad = false
    lateinit var econ: Economy

    override fun load ()
    {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
        {
            Bukkit.getServicesManager().getRegistration(Economy::class.java)?.let {
                econ = it.provider
                ProcessCraft.INSTANCE.logger.info("Vault 准备就绪")
                isLoad = true
            }

        }
    }




    fun getMoney (player: Player): Double
    {
        if (!isLoad)
            return 0.0
        return econ.getBalance(player)
    }

    fun giveMoney (player: Player, amount: Double)
    {
        if (!isLoad)
            return
        econ.depositPlayer(player, amount)
    }

    fun takeMoney (player: Player, amount: Double)
    {
        if (!isLoad)
            return
        econ.withdrawPlayer(player, amount)
    }


}