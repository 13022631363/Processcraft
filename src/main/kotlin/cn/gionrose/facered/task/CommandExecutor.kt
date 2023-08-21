package cn.gionrose.facered.task

import cn.gionrose.facered.util.onlyKeepCommand
import cn.gionrose.facered.util.papiParse
import cn.gionrose.facered.util.sendCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CommandExecutor (val commands: List<String>)
{
    /**
     * 转 op 后执行的命令
     */
    val opExecutorCommands = mapOf<Int, String>().toMutableMap()

    /**
     * 命令行执行的命令
     */
    val consoleExecutorCommands = mapOf<Int, String>().toMutableMap()

    /**
     * 根据设置的命令执行者来分类
     */
    fun classify ()
    {
        for (index in commands.indices) {
            val command = commands[index]
            if (command.contains("by op"))
                opExecutorCommands[index] = command.onlyKeepCommand("by op")
            else if (command.contains("by console"))
                consoleExecutorCommands[index] = command.onlyKeepCommand("by console")
            else throw RuntimeException ("命令 [$command] 必须指定谁来执行 例如 by op / by console")
        }
    }

    /**
     * 按顺序执行命令
     */
    fun sequentialExecution (player: Player)
    {
        val temp = mutableMapOf<Int, String>()
        temp.putAll(opExecutorCommands)
        temp.putAll(consoleExecutorCommands)


        for (number in 0 until temp.size)
        {
            // 如果有报错或许可以?.发送command
            val command = temp[number]!!
            if (opExecutorCommands.containsKey(number))
            {
                player.isOp = true
                command.papiParse(player).sendCommand(player)
                player.isOp = false
            }
            else
                command.papiParse(player).sendCommand(Bukkit.getConsoleSender())

        }
    }

    fun execute (player: Player)
    {
        classify ()

        sequentialExecution (player)
    }


}