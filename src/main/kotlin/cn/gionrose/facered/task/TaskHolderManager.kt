package cn.gionrose.facered.task

import cn.gionrose.facered.util.TimeContainer
import org.bukkit.entity.Player

object TaskHolderManager
{
    val allPlayerAndRunningTask = arrayListOf<TaskHolder>()


    fun clearTaskByPlayer (player: Player)
    {
        val holder = getHolderByPlayer(player)

        holder.clear()
    }

    /**
     * 从 allPlayerAndRunningTask 容器中获取 taskHolder
     * 如果获取不到就创建一个返回
     */
    fun getHolderByPlayer (player: Player): TaskHolder
    {
        val holder = createHolder(player)

        addHolder(holder)

        for (aHolder in allPlayerAndRunningTask)
            if (aHolder.player.uniqueId == holder.player.uniqueId)
                return aHolder
        return holder
    }

    /**
     * 传入 player 创建 taskHolder
     */
    fun createHolder (player: Player): TaskHolder
    {
        return TaskHolder (player)
    }

    /**
     * 如果 holder 中的玩家是同一个人就代表存在 返回 true
     */
    fun contains (target: TaskHolder): Boolean
    {
        if (allPlayerAndRunningTask.isNotEmpty())
            for (holder in allPlayerAndRunningTask)
                if (target.player.uniqueId == holder.player.uniqueId)
                    return true

        return false
    }

    /**
     * 如果传入的玩家的正在跑的任务列表是 empty 时
     * 就从 allPlayerAndRunningTask 中删除它
     */
    fun removeHolderIfTaskIsEmpty (player: Player)
    {
        getHolderByPlayer(player).let {
            if (it.isEmpty())
                allPlayerAndRunningTask.remove(it)
        }
    }

    /**
     * 如果不存在就添加
     */
    fun addHolder (holder: TaskHolder)
    {
        if (!contains(holder))
            allPlayerAndRunningTask.add(holder)
    }

    fun start (player: Player, id: String): CraftTask?
    {
        CraftTaskManager.getTaskById(id)?.let { task ->

            val newTask = task.clone()

            //开始时判断是否有足够的金币和点卷进行消费
            if(!(newTask.isEnoughCoinForStartTask(player) && newTask.isEnoughPointForStartTask(player)))
                return null
            //扣款
            newTask.takePoint(player, newTask.startTaskNeedPoint)
            newTask.takeCoin(player, newTask.startTaskNeedCoins)
            //此时一定存在 所以 ！！ 断言 添加任务
            getHolderByPlayer(player).addTask(newTask)

            //先更新到期时间
            newTask.updateEndTime()
            //更新剩余时间
            newTask.updateRemainTime()
//            task.updateProgress()
            //更新是否完成属性
            newTask.updateIsFinish()

            return newTask
        }
        return null
    }

    fun finish (player: Player, index: Int, isFastFinish: Boolean): Boolean
    {
        val holder = getHolderByPlayer(player)

        holder.getTask(index).let {
            //如果是快速完成任务
            if (isFastFinish) {
                //判断是否有足够的点卷消耗
                // 没有就返回false 代表结束失败
                if (!it.isEnoughPointForFastFinish(player))
                    return false
                //如果有足够的点卷
                //就消耗掉快速完成需要的点卷
                it.takePoint(player, it.fastFinishNeedPoints())
                //将到期时间设置到现在
                //代表立刻结束
                it.endTime = TimeContainer.getCurrentTime()
            }
            //更新剩余时间
            it.updateRemainTime()
            //更新是否完成参数
            if (it.updateIsFinish()) {
                //如果完成就调用命令集合
                //并返回 true
                it.runCommands(player)
                holder.removeTask(index)
                removeHolderIfTaskIsEmpty(player)
                return true
            }
            return false
        }
    }




}