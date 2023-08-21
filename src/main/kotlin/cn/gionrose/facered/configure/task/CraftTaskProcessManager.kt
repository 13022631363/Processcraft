package cn.gionrose.facered.configure.task

import cn.gionrose.facered.ProcessCraft

import java.io.File

object CraftTaskProcessManager
{
    /**
     * 持有所有任务读取器的容器
     */
    val allTaskProcess = arrayListOf<CraftTaskProcess>()

    /**
     * 重新加载
     */
    fun reload ()
    {
        allTaskProcess.clear()
        loadAll ()
    }

    /**
     * 加载所有任务读取器到容器中
     */
    fun loadAll ()
    {
        val targetDir = File (ProcessCraft.INSTANCE.dataFolder, "craftTask")

        recursiveLoad (targetDir)
    }

    /**
     * 递归读取一个文件夹中的 yml 文件
     */
    private fun recursiveLoad (targetDir: File)
    {
        val listFiles = targetDir.listFiles()

        if (listFiles != null && listFiles.isNotEmpty())
        {
            for (file in listFiles)
            {
                if (file.isDirectory)
                {
                    recursiveLoad(file)
                }

                if (!file.name.endsWith(".yml"))
                    continue

                allTaskProcess.add (CraftTaskProcess (file))
            }
        }
    }

}