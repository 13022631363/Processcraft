package cn.gionrose.facered.hook.papi.expansion

import cn.gionrose.facered.task.CraftTaskManager
import cn.gionrose.facered.task.TaskHolderManager
import cn.gionrose.facered.util.TimeContainer
import me.clip.placeholderapi.expansion.PlaceholderExpansion

import org.bukkit.entity.Player


object ProcessCraftExpansion: PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "processCraft"
    }

    override fun getAuthor(): String {
        return "facered"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun getName(): String {
        return "processCraft"
    }
    /**
     * processcraft_isfinish_index =>这个玩家正在运行的任务的第 index 个是否已经完成
     *
     * processcraft_isEnoughCoinForStart_index
     * processcraft_isEnoughPointForStart_index
     * processcraft_isEnoughPointForFastFinish_index
     *
     * processcraft_progress_index
     *
     * processcraft_endTime_year_index
     * processcraft_endTime_month_index
     * processcraft_endTime_day_index
     * processcraft_endTime_hour_index
     * processcraft_endTime_minute_index
     * processcraft_endTime_second_index
     *
     * processcraft_remainTime_year_index
     * processcraft_remainTime_month_index
     * processcraft_remainTime_day_index
     * processcraft_remainTime_hour_index
     * processcraft_remainTime_minute_index
     * processcraft_remainTime_second_index
     *
     * processcraft_needTime_year_index
     * processcraft_needTime_month_index
     * processcraft_needTime_day_index
     * processcraft_needTime_hour_index
     * processcraft_needTime_minute_index
     * processcraft_needTime_second_index
     *
     * processcraft_startNeedCoin_index
     * processcraft_startNeedPoint_index
     * processcraft_fastFinishNeedPoint_index
     * processcraft_taskName_index
     *
     * processcraft_runningTaskSize
     */
    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        player ?: return null

        val holder = TaskHolderManager.getHolderByPlayer(player)

        //此玩家正在运行任务的数量
        if ("runningtasksize" == params.lowercase())
            return holder.taskSize().toString()

        val typeAndIndex = params.split("_")
        typeAndIndex.takeIf { it.size == 2 }?.let {array->
            val type = array[0].lowercase()

            var index: Int = -1
            var taskName: String = ""
             try {
                 index = array[1].toInt ()
            }catch (e: NumberFormatException)
            {
                taskName = array[1]
                val task = CraftTaskManager.getTaskById(taskName)
                var result = task?.let {
                    when (type) {
                        "isenoughcoinforstart" -> task.isEnoughCoinForStartTask(player)
                        "isenoughpointforstart" -> task.isEnoughPointForStartTask(player)
                        "startneedcoin" -> task.startTaskNeedCoins
                        "startneedpoint" -> task.startTaskNeedPoint
                        else -> "null"
                    }.toString()
                }
                result?.let {r->
                    r.takeIf { r != "null" }?.let {
                        return r
                    }
                } ?: return null

            }

            for (i in 0 until holder.taskSize())
            {

                val result = i.takeIf { it == index }?.let {
                    val task = holder.getTask(index)
                    when (type)
                    {
                        "isfinish" -> task.updateIsFinish()
                        "isenoughpointforfastfinish" -> task.isEnoughPointForFastFinish(player)
                        "progress" -> task.updateProgress()
                        "fastfinishneedpoint" -> task.fastFinishNeedPoints()
                        "taskname" -> task.id
                        else -> "null"
                    }.toString()
                }
                result?.let {r->
                    r.takeIf { r != "null" }?.let {
                        return r
                    } ?: return  null
                }

            }
        }
        typeAndIndex.takeIf { it.size == 3 }?.let {array->
            val type = array[0].lowercase()
            val timeType = array[1]
            val index = try {
                array[2].toInt ()
            }catch (e: NumberFormatException)
            {
                return  null
            }

            for (i in 0 until holder.taskSize())
            {
                val result = i.takeIf { it == index }?.let {
                    val task = holder.getTask(index)
                    when (type)
                    {
                        "endtime" -> task.endTime.get(timeType)
                        "remaintime"-> task.updateRemainTime ().get(timeType)
                        "needtime" -> TimeContainer.parse(task.needTime).get(timeType)
                        else -> "null"
                    }.toString()
                }
                result?.let {r->
                    r.takeIf { r != "null" }?.let {
                        return r
                    } ?: return  null
                }
            }
        }


        return null
    }
}