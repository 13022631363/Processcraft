package cn.gionrose.facered.task

import cn.gionrose.facered.configure.task.CraftTaskProcessManager

object CraftTaskManager
{
    /**
     * 存储所有合成任务
     * 任务id 任务本身
     */
    val allCraftTask = mapOf<String, CraftTask>().toMutableMap()


    fun keys (): MutableSet<String>
    {
        return allCraftTask.keys
    }

    fun reload ()
    {
        allCraftTask.clear()
        loadAll()
    }

    /**
     * 通过 id 获取任务
     */
    fun getTaskById (id: String): CraftTask?
    {
        return allCraftTask[id]
    }

    /**
     * 读取所有任务到容器中
     */
    fun loadAll ()
    {
        CraftTaskProcessManager.reload()

        for (taskProcess in CraftTaskProcessManager.allTaskProcess)
        {
            val tasks = taskProcess.loadAll()
            for (task in tasks)
            {
                allCraftTask [task.id] = task
            }
        }
    }

}