package cn.gionrose.facered.task

import org.bukkit.entity.Player

/**
 * 任务所有者
 * 任务所有者持有正在运行的任务列表
 * 由于他是所有者  故它来操作任务的开始和结束是情理之中
 */
class  TaskHolder (val player: Player)
{
    val runningTask = arrayListOf<CraftTask>()

    fun clear ()
    {
        runningTask.clear()
    }

    fun taskSize (): Int
    {
        return runningTask.size
    }
    /**
     * 可重复添加任务
     */
    fun addTask (task: CraftTask)
    {
        runningTask.add(task)
    }

    /**
     * 删除任务
     */
    fun removeTask (index: Int)
    {
        runningTask.removeAt(index)
    }

    /**
     * 获取任务
     */
    fun getTask (index: Int): CraftTask
    {
        return runningTask[index]
    }

    /**
     * 任务所有者中持有的正在运行的任务列表是否为 empty
     */
    fun isEmpty (): Boolean
    {
        return runningTask.isEmpty()
    }






}