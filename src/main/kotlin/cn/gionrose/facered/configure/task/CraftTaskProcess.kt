package cn.gionrose.facered.configure.task

import cn.gionrose.facered.ProcessCraft
import cn.gionrose.facered.configure.ConfigProcess
import cn.gionrose.facered.task.CraftTask
import cn.gionrose.facered.util.isNotNullCharacter
import cn.gionrose.facered.util.notExistAddDot
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * 工艺任务配置文件类
 * 可有多个
 * @param name 在插件文件夹中的 craftTask 专门存放工艺任务的文件夹中的配置文件名字
 * @param isRelease 是否释放资源文件中的配置到指定文件夹
 */
class CraftTaskProcess(override val sourceFile: File , override var isRelease: Boolean = false): ConfigProcess() {

    var currentTaskNameSection: ConfigurationSection? = null



    override lateinit var configFile: YamlConfiguration

    init {
        createDataFolder()
        createCraftTaskDir ()
        release()
        load()
    }

    private fun createCraftTaskDir ()
    {
        File (ProcessCraft.INSTANCE.dataFolder, "craftTask").takeIf { !it.exists() }?.mkdir()
    }

    fun getRoot (): ConfigurationSection?
    {
        return configFile.root
    }

    fun getTaskNameSection (taskName: String): ConfigurationSection?
    {
        currentTaskNameSection = getRoot ()?.get(taskName) as? ConfigurationSection
        return currentTaskNameSection
    }

    fun getTime (): String
    {
        return currentTaskNameSection!!.getString ("time") ?: "0s"
    }


    /**
     *  如果是 empty 就直接返回 命令可以一条没有
     *  如果是 " " 就删掉
     */
    fun getCommands (): List<String>
    {
        val result = currentTaskNameSection!!.getStringList("commands")
        if (result.isEmpty())
            return result
        result.forEach {
            if (!it.isNotNullCharacter())
                result.remove(it)
        }

        return result
    }

    /**
     *  如果是 empty 就直接报错 这个不能一条没有
     *  如果是 " " 就删掉
     */
    fun getDoneQuicklyPercentage (): List<String>
    {
        val result = currentTaskNameSection!!.getStringList("doneQuicklyPercentage")
        if (result.isEmpty())
            throw  RuntimeException ("在 [${sourceFile.name}] 配置文件中 [${currentTaskNameSection!!.name}] 任务的 doneQuicklyPercentage 必须有一行配置...")

        result.forEach {
            if (!it.isNotNullCharacter())
                result.remove(it)
        }

        return result
    }

    fun getStartConsume (consumeType: String): Double
    {
        val startCoin: Double?
        val startCoinStringType: String = currentTaskNameSection!!.getString(consumeType) ?: return 0.0
        try {
            startCoin = startCoinStringType.notExistAddDot().toDouble()

        }catch (e: NumberFormatException)
        {
            throw RuntimeException ("在获取 开始前消耗金币数量时 您的${if (consumeType == "startCoin") "startCoin" else "startPoint" }数量不符合要求 => $startCoinStringType")
        }
        return startCoin
    }

    fun getModelData (): Int
    {
        return currentTaskNameSection!!.getInt ("modelData")
    }

    fun getDefaultModelData (): Int
    {
        return currentTaskNameSection!!.getInt ("defaultModelData")
    }

    fun getLore (): List<String>
    {
        val result = currentTaskNameSection!!.getStringList("lore")
        if (result.isEmpty())
            return result
        result.forEach {
            if (!it.isNotNullCharacter())
                result.remove(it)
        }

        return result
    }

    fun getButtonName (): String
    {
        return currentTaskNameSection!!.getString("buttonName") ?: throw RuntimeException ("请配置 ${configFile.name} 中的 buttonName...")
    }

    fun getDefaultButtonName (): String
    {
        return currentTaskNameSection!!.getString("defaultButtonName") ?: throw RuntimeException ("请配置 ${configFile.name} 中的 defaultButtonName...")
    }

    fun loadAll (): List<CraftTask>
    {
        val allCraftTask = arrayListOf<CraftTask>()

        getRoot()?.let {
            val keys = it.getKeys(false)
            for (key in keys)
            {
                getTaskNameSection(key)

                if (currentTaskNameSection != null)
                {
                    allCraftTask.add (CraftTask (getStartConsume("startPoint"),
                        getStartConsume("startCoin"),
                        currentTaskNameSection!!.name,
                        getTime(),
                        getCommands(),
                        getDoneQuicklyPercentage(),
                        getButtonName(),
                        getModelData(),
                        getLore()))

                }else
                    continue
            }
        }

        return allCraftTask.toList()
    }

}