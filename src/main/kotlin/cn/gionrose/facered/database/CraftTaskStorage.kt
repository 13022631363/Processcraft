package cn.gionrose.facered.database

import cn.gionrose.facered.task.CraftTask
import cn.gionrose.facered.task.TaskHolder
import cn.gionrose.facered.task.TaskHolderManager
import cn.gionrose.facered.util.fromJson
import cn.gionrose.facered.util.toJson
import org.bukkit.entity.Player

object CraftTaskStorage
{
    /**
     * 创建表 只执行一次
     */
    fun createTable (): Boolean
    {
       try {
           MysqlWrapper.connection().use {
               return it.prepareStatement("create table  if not exists `processcraft` (`uuid` varchar(64), `tasks` varchar(2000))charset=utf8;").execute()
           }
       }catch (e: ExceptionInInitializerError)
       {
           throw RuntimeException ("请配置好数据库信息 ...")
       }
    }

    /**
     * 一个玩家对应一个 tasks
     * 每个 task 由序列化后在转 Json 后用 # 字符隔开 存放
     */
    fun save (holder: TaskHolder): Int
    {
        val result: Int
        try {
            MysqlWrapper.connection().use {
                result = it.prepareStatement("insert into `processcraft` (`uuid`, `tasks`) values (?, ?);").apply {
                    setString(1, holder.player.uniqueId.toString())
                    var tasks: String = ""
                    for (i in 0 until holder.taskSize())
                    {
                        tasks += "${holder.getTask(i).serialize().toJson()}"
                        if (i != holder.taskSize() -1)
                            tasks += "#"
                    }
                    setString(2, tasks)
                }.executeUpdate()
            }
            return result
        }catch (e: NoClassDefFoundError)
        {
            throw RuntimeException ("请配置好数据库信息 ...")
        }

    }
    fun load (player: Player): TaskHolder
    {
        val holder: TaskHolder
        try {
            MysqlWrapper.connection().use {
                val resultSet =
                    it.prepareStatement("select `tasks` from `processcraft` where `uuid` = ?;").apply {
                        setString(1, player.uniqueId.toString())
                    }.executeQuery()

                holder = TaskHolderManager.createHolder(player).apply {
                    resultSet.takeIf { it.next() }?.let {
                        val tasks = resultSet.getString("tasks")
                        val allTasks = tasks.split("#")

                        allTasks.forEach {
                            addTask (CraftTask.deserialize(it.fromJson()))
                        }
                    }
                }

                return holder

            }
        }catch (e: NoClassDefFoundError)
        {
            throw RuntimeException ("请配置好数据库信息 ...")
        }
    }

    fun deleteByPlayer (player: Player): Int
    {
        val result: Int;
       try {
           MysqlWrapper.connection().use {
               result = it.prepareStatement("delete from `processcraft` where `uuid` = ?;").apply {
                   setString(1, player.uniqueId.toString())
               }.executeUpdate()
           }
           return result
       }catch (e : NoClassDefFoundError)
       {
           throw RuntimeException ("请配置好数据库信息 ...")
       }

    }
}