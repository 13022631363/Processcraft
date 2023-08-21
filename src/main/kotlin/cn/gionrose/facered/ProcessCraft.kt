package cn.gionrose.facered

import cn.gionrose.facered.command.ProcessCraftCommand
import cn.gionrose.facered.configure.task.CraftTaskProcess
import cn.gionrose.facered.database.CraftTaskStorage
import cn.gionrose.facered.hook.HookManager
import cn.gionrose.facered.hook.PlayerPointsHook
import cn.gionrose.facered.hook.VaultHook
import cn.gionrose.facered.hook.papi.PapiHook
import cn.gionrose.facered.hook.papi.expansion.ExpansionRegistrator
import cn.gionrose.facered.hook.papi.expansion.ProcessCraftExpansion
import cn.gionrose.facered.listenner.AboutPlayerListener
import cn.gionrose.facered.task.CraftTaskManager
import cn.gionrose.facered.task.TaskHolderManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ProcessCraft: JavaPlugin ()
{
    companion object
    {
        lateinit var INSTANCE: JavaPlugin
    }


    override fun onEnable()
    {
        INSTANCE = this

        AboutPlayerListener.register(this)

        CraftTaskStorage.createTable()
        CraftTaskProcess (File (File (dataFolder, "craftTask"), "craftTask.yml"))
        CraftTaskManager.loadAll()

        HookManager.register(VaultHook, PlayerPointsHook, PapiHook)
        HookManager.load()

        ExpansionRegistrator.register(ProcessCraftExpansion)
        ExpansionRegistrator.registerAll()

        ProcessCraftCommand.register()
        hello ()



    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach {
            CraftTaskStorage.save (TaskHolderManager.getHolderByPlayer(it))
        }
        seeyou()
    }



    private fun hello ()
    {
        this.logger.info(
            "\n    ██░ ██ ▓█████  ██▓     ██▓     ▒█████                 \n" +
                "   ▓██░ ██▒▓█   ▀ ▓██▒    ▓██▒    ▒██▒  ██▒               \n" +
                "   ▒██▀▀██░▒███   ▒██░    ▒██░    ▒██░  ██▒               \n" +
                "   ░▓█ ░██ ▒▓█  ▄ ▒██░    ▒██░    ▒██   ██░               \n" +
                "   ░▓█▒░██▓░▒████▒░██████▒░██████▒░ ████▓▒░ ██▓           \n" +
                "    ▒ ░░▒░▒░░ ▒░ ░░ ▒░▓  ░░ ▒░▓  ░░ ▒░▒░▒░  ▒▓▒           \n" +
                "    ▒ ░▒░ ░ ░ ░  ░░ ░ ▒  ░░ ░ ▒  ░  ░ ▒ ▒░  ░▒            \n" +
                "    ░  ░░ ░   ░     ░ ░     ░ ░   ░ ░ ░ ▒   ░             \n" +
                "    ░  ░  ░   ░  ░    ░  ░    ░  ░    ░ ░    ░            \n" +
                "                                             ░            \n" +
                "             ██░ ██  ▒█████   ▄▄▄▄    ██▓    ▓█████  ▐██▌ \n" +
                "            ▓██░ ██▒▒██▒  ██▒▓█████▄ ▓██▒    ▓█   ▀  ▐██▌ \n" +
                "            ▒██▀▀██░▒██░  ██▒▒██▒ ▄██▒██░    ▒███    ▐██▌ \n" +
                "            ░▓█ ░██ ▒██   ██░▒██░█▀  ▒██░    ▒▓█  ▄  ▓██▒ \n" +
                "            ░▓█▒░██▓░ ████▓▒░░▓█  ▀█▓░██████▒░▒████▒ ▒▄▄  \n" +
                "             ▒ ░░▒░▒░ ▒░▒░▒░ ░▒▓███▀▒░ ▒░▓  ░░░ ▒░ ░ ░▀▀▒ \n" +
                "             ▒ ░▒░ ░  ░ ▒ ▒░ ▒░▒   ░ ░ ░ ▒  ░ ░ ░  ░ ░  ░ \n" +
                "             ░  ░░ ░░ ░ ░ ▒   ░    ░   ░ ░      ░       ░ \n" +
                "             ░  ░  ░    ░ ░   ░          ░  ░   ░  ░ ░   ")
    }
    private fun seeyou ()
    {

        this.logger.info("\n     ██████ ▓█████ ▓█████    ▓██   ██▓ ▒█████   █    ██   \n" +
                "   ▒██    ▒ ▓█   ▀ ▓█   ▀     ▒██  ██▒▒██▒  ██▒ ██  ▓██▒  \n" +
                "   ░ ▓██▄   ▒███   ▒███        ▒██ ██░▒██░  ██▒▓██  ▒██░  \n" +
                "     ▒   ██▒▒▓█  ▄ ▒▓█  ▄      ░ ▐██▓░▒██   ██░▓▓█  ░██░  \n" +
                "   ▒██████▒▒░▒████▒░▒████▒     ░ ██▒▓░░ ████▓▒░▒▒█████▓   \n" +
                "   ▒ ▒▓▒ ▒ ░░░ ▒░ ░░░ ▒░ ░      ██▒▒▒ ░ ▒░▒░▒░ ░▒▓▒ ▒ ▒   \n" +
                "   ░ ░▒  ░ ░ ░ ░  ░ ░ ░  ░    ▓██ ░▒░   ░ ▒ ▒░ ░░▒░ ░ ░   \n" +
                "   ░  ░  ░     ░      ░       ▒ ▒ ░░  ░ ░ ░ ▒   ░░░ ░ ░   \n" +
                "         ░     ░  ░   ░  ░    ░ ░         ░ ░     ░       \n" +
                "                              ░ ░                         \n" +
                "             ██░ ██  ▒█████   ▄▄▄▄    ██▓    ▓█████  ▐██▌ \n" +
                "            ▓██░ ██▒▒██▒  ██▒▓█████▄ ▓██▒    ▓█   ▀  ▐██▌ \n" +
                "            ▒██▀▀██░▒██░  ██▒▒██▒ ▄██▒██░    ▒███    ▐██▌ \n" +
                "            ░▓█ ░██ ▒██   ██░▒██░█▀  ▒██░    ▒▓█  ▄  ▓██▒ \n" +
                "            ░▓█▒░██▓░ ████▓▒░░▓█  ▀█▓░██████▒░▒████▒ ▒▄▄  \n" +
                "             ▒ ░░▒░▒░ ▒░▒░▒░ ░▒▓███▀▒░ ▒░▓  ░░░ ▒░ ░ ░▀▀▒ \n" +
                "             ▒ ░▒░ ░  ░ ▒ ▒░ ▒░▒   ░ ░ ░ ▒  ░ ░ ░  ░ ░  ░ \n" +
                "             ░  ░░ ░░ ░ ░ ▒   ░    ░   ░ ░      ░       ░ \n" +
                "             ░  ░  ░    ░ ░   ░          ░  ░   ░  ░ ░    \n")
    }

}