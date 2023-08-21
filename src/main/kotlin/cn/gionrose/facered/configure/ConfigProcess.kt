package cn.gionrose.facered.configure

import cn.gionrose.facered.ProcessCraft
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

abstract class ConfigProcess
{

    /**
     * File 类型的 xxx.yml 文件
     */
    abstract val sourceFile: File

    /**
     * yaml 类型的 xxx.yml 文件
     */
    abstract var configFile: YamlConfiguration

    open var isRelease = true

    fun getString (path: String): String
    {
        return configFile.getString(path)!!
    }

    fun getSection (parentSection :ConfigurationSection, element: String): ConfigurationSection?
    {
        return parentSection.get(element) as? ConfigurationSection
    }



    /**
     * 将配置文件输出到 字段 configFile
     * 例如 configFile = ./插件文件夹/database.yml  就将 database.yml 输出到插件文件夹下
     * 只释放一次
     */
    fun release ()
    {
       if (isRelease)
           sourceFile.takeIf { !it.exists() }?.let {
               ProcessCraft.INSTANCE.saveResource(sourceFile.name,false )
           }
    }

    /**
     * 读取 释放的 database.yml 文件成 Yaml格式
     * 当使用者修改配置文件时，需要再次读取才会统一 （获取的到修改后的参数 ）
     */
    fun load ()
    {
        configFile = YamlConfiguration.loadConfiguration(sourceFile)
    }

    /**
     * 创建插件文件夹 如存在则什么都不做
     * 只创建一次
     */
    fun createDataFolder ()
    {
        ProcessCraft.INSTANCE.dataFolder.takeIf { !it.exists() }?.mkdir()
    }

}