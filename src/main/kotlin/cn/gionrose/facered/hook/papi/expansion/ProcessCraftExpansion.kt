package cn.gionrose.facered.hook.papi.expansion

import cn.gionrose.facered.configure.ConfigProcessor
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


    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        player ?: return null

        val holder = TaskHolderManager.getHolderByPlayer(player)

        //此玩家正在运行任务的数量
        if ("runningtasksize" == params.lowercase())
            return holder.taskSize().toString()

        //目前是processcraft_modelData_index
        //     processcraft_lore_loreIndex_index
        //     processcraft_needTime_year_index
        //     processcraft_isEnoughCoinForStart_taskName
        // 不包括 processcraft
        val args = params.split("_")

        val type = args[0].lowercase()

        /**
         * 第一种情况 processcraft_modelData_index
          */
        args.takeIf { it.size == 2 }?.let {args->
            // 转成全小写


            val index: Int
            val taskName: String
            var result = "null"
            try {


                //此时的下标 1 的参数是任务列表的下标 或者是 taskName
                index = args[1].toInt ()
            }catch (e: NumberFormatException)
            {

                /**
                 *  情况 1 的情况 1
                 *  如果下标 1 的参数转 int 类型出现异常
                 *  则代表他是通过 taskName 获取目标
                 */
                taskName = args[1]

                val task = CraftTaskManager.getTaskById(taskName)

                task?.let {
                    result = when (type) {
                        "isenoughcoinforstart" -> task.isEnoughCoinForStartTask(player)
                        "isenoughpointforstart" -> task.isEnoughPointForStartTask(player)
                        "startneedcoin" -> task.startTaskNeedCoins
                        "startneedpoint" -> task.startTaskNeedPoint
                        else -> "null"
                    }.toString()
                }
                //基本只有这几个需要用 taskName 来获取任务的变量
                //所以如果 result 下来后还是 null
                //说明不需要再往下走了
                //直接返回 null
                result.let { r->
                    r.takeIf { r != "null" }?.let {
                        return r
                    }
                } ?: return null
            }

            /**
             * 情况 1 的情况 2
             * 代表是 通过运行任务列表index 来指定目标
             */
            for (i in 0 until holder.taskSize())
            {
                i.takeIf { it == index }?.let {

                    val task = holder.getTask(index)

                    result =  when (type) {
                        "isfinish" -> task.updateIsFinish()
                        "isenoughpointforfastfinish" -> task.isEnoughPointForFastFinish(player)
                        "progress" -> task.updateProgress()
                        "fastfinishneedpoint" -> task.fastFinishNeedPoints()
                        "taskname" -> task.id
                        "loresize" -> task.lore.size
                        "buttonname" -> task.buttonName
                        "modeldata" -> task.modelData.toString()
                        else -> "null"
                    }.toString()

                    //
                    result.let { r->
                        r.takeIf { r != "null" }?.let {
                            return r
                        }
                    }
                }

            }

            /**
             * 情况 1 的情况 3
             * 如果不在运行任务列表时 获取的默认参数
             */
            try {
                //如果报下标越界异常 获取的默认参数
                holder.getTask(index)

            }catch (e: IndexOutOfBoundsException)
            {

                result = when (type) {
                    "buttonname" -> ConfigProcessor.getDefaultButtonName()
                    "modeldata" -> ConfigProcessor.getDefaultModelData().toString()
                    else -> "null"
                }
            }

            result.let { r->
                r.takeIf { r != "null" }?.let {
                    return r
                } ?: return  null
            }
        }

        /**
         * 情况2
         * 当 processcraft_needTime_year_index 情况时
         */
        args.takeIf { it.size == 3 }?.let {args->
            //此时的下标 1 的参数是 时间类型
            val timeType = args[1]
            //运行任务列表的下标
            val index = try {
                args[2].toInt ()
            }catch (e: NumberFormatException)
            {
                //若不是数字 直接返回 null
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
                        "lore" -> if (task.lore.size > timeType.toInt())
                            task.lore[timeType.toInt()] else "null"
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