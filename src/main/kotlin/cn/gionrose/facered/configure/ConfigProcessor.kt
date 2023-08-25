package cn.gionrose.facered.configure

import cn.gionrose.facered.ProcessCraft
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * 语言配置文件单例类
 * 有且只有一个
 */
object ConfigProcessor: ConfigProcess ()
{


    /**
     * File 类型的 config.yml 文件
     */
    override val sourceFile = File (ProcessCraft.INSTANCE.dataFolder, "config.yml")

    /**
     * yaml 类型的 config.yml 文件
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

    fun getDefaultButtonName (): String
    {
        return configFile.getString ("defaultButtonName") ?: throw RuntimeException ("请配置 [${configFile.name}] 文件中的 defaultButtonName 参数...")
    }

    fun getDefaultModelData (): Int
    {
        return configFile.getInt ("defaultModelData")
    }

}