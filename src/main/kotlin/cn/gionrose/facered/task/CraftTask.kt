package cn.gionrose.facered.task

import cn.gionrose.facered.hook.PlayerPointsHook
import cn.gionrose.facered.hook.VaultHook
import cn.gionrose.facered.util.TimeContainer
import cn.gionrose.facered.util.notExistAddDot
import cn.gionrose.facered.util.removeDot
import org.bukkit.entity.Player


class CraftTask (val startTaskNeedPoint: Double, val startTaskNeedCoins: Double,
                 val id: String,var needTime: String, var commands: List<String>,
                 var doneQuicklyPercentage: List<String>, val buttonName: String,
                 val modelData: Int, val lore: List<String>)
{
    /**
     * 是否完成
     */
    private var isFinish = false



    /**
     * 进度
     * 需要调用 update 方法来更新
     */
    private var progress: Double = 0.0

    /**
     * 以下是结束和剩余时间的年月日时分秒
     * 需要调用 updateXXX方法来更新
     */
    lateinit var endTime: TimeContainer

    lateinit var remainTime: TimeContainer



    /**
     * 命令执行器
     */
    private var commandExecutor: CommandExecutor = CommandExecutor(commands)

    /**
     * 更新是否完成
     * 首先更新剩余时间
     * 在判断是否是 0s
     * 是否代表完成
     */
    fun updateIsFinish ():Boolean
    {
                    //更新剩余时间
        isFinish = updateRemainTime() same TimeContainer.parse("0s")
        return isFinish
    }

    /**
     * 到期时间 - 当前时间
     * 如果当前时间已经超过到期时间 说明已经完成 给一个 0s的
     * 捕获异常是因为前者小于后者会报错
     * 如果没超过就是剩余时间
     */
    fun updateRemainTime (): TimeContainer
    {
        remainTime = try {
            endTime subtract TimeContainer.getCurrentTime()
        }catch (e: RuntimeException) {
            TimeContainer.parse("0s")
        }
        return remainTime
    }

    /**
     * 获取到期时间
     * 用当前时间 + 配置文件中设置的需要时间
     * 得出到期时间点
     */
    fun updateEndTime (): TimeContainer
    {
        endTime = TimeContainer.getCurrentTime() add TimeContainer.parse(needTime)
        return endTime
    }

    /**
     * 更新并获取当前进度
     * 先更新剩余时间
     * 剩余时间 / 需要时间
     * 获得最多 3 位小数的 0-1的数
     */
    fun updateProgress (): Double
    {
        updateRemainTime()
        val needTime = TimeContainer.parse(needTime)
        progress =  (needTime subtract remainTime) percentageOf needTime
        return progress
    }


    /**
     * 执行命令集合
     */
    fun runCommands (player: Player)
    {
        commandExecutor.execute(player)
    }


    /**
     * 消费点卷
     */
    fun takePoint (player: Player, amount: Double)
    {
        PlayerPointsHook.addOrSubtractPoint(player, -amount.toInt())
    }

    /**
     * 消费金币
     */
    fun takeCoin (player: Player, amount: Double)
    {
        VaultHook.takeMoney(player, amount)
    }

    /**
     * 获取玩家此时的点卷 来判断是否足够扣款来快速完成任务
     */
    fun isEnoughPointForFastFinish (player: Player): Boolean
    {
        val fastFinishNeedPoints = fastFinishNeedPoints()
        return PlayerPointsHook.getPlayerPoint(player) >= fastFinishNeedPoints
    }

    /**
     * 获取玩家此时的点卷 来判断是否足够扣款来开始任务
     */
    fun isEnoughPointForStartTask (player: Player): Boolean
    {
        return PlayerPointsHook.getPlayerPoint(player) >= startTaskNeedPoint
    }

    /**
     * 获取玩家此时的金币 来判断是否足够扣款来开始任务
     */
    fun isEnoughCoinForStartTask (player: Player): Boolean
    {
        return VaultHook.getMoney(player) >= startTaskNeedCoins
    }


    /**
     * 完成检测无误
     * 快速完成任务
     * 根据进度来减少点卷的消耗
     */
    fun fastFinishNeedPoints (): Double
    {
            for (index in doneQuicklyPercentage.indices)
            {
                val s = doneQuicklyPercentage[index]

                val currentPercentageString = s.split(" ")[0]
                val currentReduceCoinString = s.split(" ")[1]

                if (doneQuicklyPercentage.size > index + 1)
                {
                    val nextPercentageString =  doneQuicklyPercentage[index +1].split(" ")[0]
                    try {
                        var currentPercentage = currentPercentageString.notExistAddDot().toDouble()
                        var nextPercentage = nextPercentageString.notExistAddDot().toDouble()

                        if (currentPercentage > 1)
                            currentPercentage /= 100
                        if (nextPercentage > 1)
                            nextPercentage /= 100

                        val updateProgress = updateProgress()

                        if (currentPercentage <= updateProgress && updateProgress < nextPercentage)
                        {
                            return currentReduceCoinString.notExistAddDot().toDouble()
                        }

                    }catch (e: NumberFormatException)
                    {
                        throw RuntimeException ("在 [${id}] 任务中 doneQuicklyPercentage 设置的格式不合格...")
                    }

                }else
                {
                    return s.split(" ")[1].notExistAddDot().toDouble()
                }
            }

        return -1.0
    }

    fun serialize (): Map<String, Any>
    {
        val result = mutableMapOf<String, Any>()
        return result.apply {
            put ("endTime", endTime.toString())
            put ("remainTime", updateRemainTime().toString())
            put ("id", id)
            put ("needTime", needTime)
            put ("startTaskNeedCoins", startTaskNeedCoins)
            put ("startTaskNeedPoint", startTaskNeedPoint)
            put ("commands", commands)
            put ("doneQuicklyPercentage", doneQuicklyPercentage)
            put ("buttonName", buttonName)
            put ("modelData", modelData)
            put ("lore", lore)
        }
    }

    /**
     * 当放入数据库时 最后时间会一直停留
     * 所以需要重新用剩余时间去计算出最后时间并设置
     * 才能达到再次启动任务的效果
     */
    fun reloadEndTime ()
    {
        endTime = TimeContainer.getCurrentTime() add remainTime
    }

    companion object
    {
        fun deserialize (serialize: Map<String, Any>): CraftTask
        {

           serialize.let {
                val task = CraftTask(
                    it["startTaskNeedCoins"] as Double,
                    it["startTaskNeedPoint"] as Double,
                    it["id"].toString(),
                    it["needTime"].toString(),
                    it["commands"] as List<String>,
                    it["doneQuicklyPercentage"] as List<String>,
                    it["buttonName"].toString(),
                    it["modelData"].toString().removeDot().toInt(),
                    it["lore"] as List<String>,
                )
               task.remainTime = TimeContainer.parse(it["remainTime"] as String)
               task.reloadEndTime()

               return task
            }


        }
    }
    fun clone (): CraftTask
    {
        return CraftTask (startTaskNeedPoint, startTaskNeedCoins, id, needTime, commands, doneQuicklyPercentage, buttonName, modelData, lore)
    }
}