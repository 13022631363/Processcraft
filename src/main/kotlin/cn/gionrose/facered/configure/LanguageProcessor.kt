package cn.gionrose.facered.configure

import cn.gionrose.facered.ProcessCraft
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * 语言配置文件单例类
 * 有且只有一个
 */
object LanguageProcessor: ConfigProcess ()
{


    /**
     * File 类型的 Language.yml 文件
     */
    override val sourceFile = File (ProcessCraft.INSTANCE.dataFolder, "Language.yml")

    /**
     * yaml 类型的 Language.yml 文件
     */
    override lateinit var configFile: YamlConfiguration

    /**
     * 当任何本类的字段或方法被调用前
     * 会调用此静态初始化方法
     */
    init {
        createDataFolder()
        release()
        load()

    }

    fun getPluginStartPreLine (): String
    {
        return getString ("pluginStartPreLine")
    }

    fun getPluginStartSufLine (): String
    {
        return getString("pluginStartSufLine")
    }

    fun getHello (): String
    {
        return getString("hello")
    }

    fun getPluginEndPreLine (): String
    {
        return getString ("pluginEndPreLine")
    }

    fun getPluginEndSufLine (): String
    {
        return getString("pluginEndSufLine")
    }

    fun getSeeYou (): String
    {
        return getString("seeyou")
    }

}