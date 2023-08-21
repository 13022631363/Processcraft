package cn.gionrose.facered.configure

import cn.gionrose.facered.ProcessCraft
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * 默认配置文件单例类
 * 有且只有一个
 */
object DatabaseProcess: ConfigProcess()
{


    /**
     * File 类型的 database.yml 文件
     */
    override val sourceFile = File (ProcessCraft.INSTANCE.dataFolder, "database.yml")

    /**
     * yaml 类型的 database.yml 文件
     */
    override lateinit var configFile: YamlConfiguration

    init {
        createDataFolder()
        release()
        load()
    }

    fun getUrl (): String
    {
        return getString("url")
    }

    fun getUsername (): String
    {
        return getString ("username")
    }

    fun getPassword (): String
    {
        return getString ("password")
    }


}