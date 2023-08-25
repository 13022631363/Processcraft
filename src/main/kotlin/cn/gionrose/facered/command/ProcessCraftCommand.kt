package cn.gionrose.facered.command

import cn.gionrose.facered.task.CraftTaskManager
import cn.gionrose.facered.task.TaskHolderManager
import cn.gionrose.facered.util.getPlayer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

object ProcessCraftCommand: CommandExecutor, TabExecutor
{
    fun register ()
    {
        AboutCommandRegistration.register("processCraft", this)
    }


    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): MutableList<String>? {
        if (sender !is Player) return null

        val result = arrayListOf<String>()


        when (args.size)
        {
            1 -> result.addAll(arrayOf("reload", "startTask", "finishTask", "fastFinishTask"))
            2 -> {
                Bukkit.getOnlinePlayers().stream().forEach {
                    result.add(it.name)
                }
            }
            3 ->
            {
                when (args[0])
                {
                    "startTask" -> {
                        CraftTaskManager.keys().forEach {
                            result.add(it)
                        }
                    }
                    "finishTask" -> {
                        for (i in 0 until TaskHolderManager.getHolderByPlayer(sender).taskSize()) {
                            result.add(i.toString())
                        }
                    }
                    "fastFinishTask" -> {
                        val holder = TaskHolderManager.getHolderByPlayer(sender)
                        for (i in 0 until holder.taskSize()) {
                            if (!holder.getTask(i).updateIsFinish())
                                result.add(i.toString())
                        }
                    }
                }
            }
        }
        return result
    }



    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val action = args[0]

            when (args.size)
            {
                1 -> {
                    when(action)
                    {
                        "reload" -> CraftTaskManager.reload()
                    }
                }

                // /processCraft start facered 1
                3 -> {
                    when (action)
                    {
                        "fastFinishTask", "finishTask" ->
                        {
                            val index = args[2].let {
                                val result: Int
                                try {
                                    result = it.toInt()
                                }catch (e: NumberFormatException)
                                {
                                    return false
                                }
                                result
                            }
                            val playerName = args[1]
                            val targetPlayer = playerName.getPlayer()

                            if (targetPlayer == null)
                            {
                                sender.sendMessage("${ChatColor.RED} 指定的玩家不存在 或不在线")
                                return false
                            }

                            val isFastFinish = args[0] == "fastFinishTask"

                            if (TaskHolderManager.getHolderByPlayer(targetPlayer).isEmpty())
                                return false

                            return TaskHolderManager.finish(
                                targetPlayer,
                                index,
                                isFastFinish
                            )
                        }
                        "startTask" -> {
                            when (args.size) {

                                // /processCraft start facered task1
                                3 -> {
                                    val id = args[2]
                                    val playerName = args[1]
                                    val targetPlayer = playerName.getPlayer()

                                    if (targetPlayer == null) {
                                        sender.sendMessage("${ChatColor.RED} 指定的玩家不存在 或不在线")
                                        return false
                                    }

                                    TaskHolderManager.start(
                                        targetPlayer,
                                        id
                                    ) ?: return false
                                    return true
                                }
                            }
                        }
                    }
                }
            }



        return true
    }
}