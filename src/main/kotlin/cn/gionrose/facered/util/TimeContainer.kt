package cn.gionrose.facered.util

import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year

class TimeContainer (var year: Int = 0, var month: Int = 0, var day: Int = 0, var hour: Int = 0, var minute: Int = 0, var second: Int = 0): Cloneable
{
    //秒到分钟的跨度
    val SECONDTOMINUTE =  60
    //分钟到小时的跨度
    val MINUTETOHOUR = 60
    //小时到天的跨度
    val HOURTODAY = 24

    /**
     * 天到月的跨度 默认 30
     * 但有 28 和 29 的区别
     * @see adaptiveDaysInMonth
     */
    var DAYTOMONTH = 30
    //月到年的跨度
    val MONTHTOYEAR = 12


    /**
     * 匹配月份所应该有的天数
     */
    private fun adaptiveDaysInMonth (year: Int, month: Int)
    {
        when (month)
        {
            1 -> DAYTOMONTH = 31
            2 -> DAYTOMONTH = if (isLeap(year)) 29 else 28
            3 -> DAYTOMONTH = 31
            4 -> DAYTOMONTH = 30
            5 -> DAYTOMONTH = 31
            6 -> DAYTOMONTH = 30
            7 -> DAYTOMONTH = 31
            8 -> DAYTOMONTH = 31
            9 -> DAYTOMONTH = 30
            10 -> DAYTOMONTH = 31
            11 -> DAYTOMONTH = 30
            12 -> DAYTOMONTH = 31
        }

    }

    /**
     * 前者加后者
     */
    infix fun add (container: TimeContainer): TimeContainer
    {
        val clone = this.clone()

        clone.day += container.day
        clone.second += container.second
        clone.minute += container.minute
        clone.month += container.month
        clone.year += container.year
        clone.hour += container.hour

        clone.fixes()
        return clone
    }

    /**
     * 前者减后者
     * 前者必须大于后者
     */
    infix fun subtract (container: TimeContainer): TimeContainer
    {
        if (this compareTo container < 0)
            throw  RuntimeException ("在使用 subtract 方法时 前者必须大于后者")

        val clone = this.clone()

        clone.day -= container.day
        clone.second -= container.second
        clone.month -= container.month
        clone.year -= container.year
        clone.hour -= container.hour
        clone.minute -= container.minute

        clone.fixes()
        return clone
    }

    /**
     * 克隆一模一样的
     */
    public override fun clone (): TimeContainer
    {
       return super.clone() as TimeContainer
    }

    /**
     * 将 timeContainer 对象转成 Long 类型的秒数
     */
    fun toSecond (): Long
    {
        var yearToDay: Long = 0
        for (i in 1..year)
        {
            if (isLeap(LocalDate.now().year + i))
                yearToDay += 366
            else yearToDay += 365
        }
        var monthToDay: Long = 0
        for (i in 1..month)
        {
            adaptiveDaysInMonth(year, i)
            monthToDay += DAYTOMONTH

        }

        val total= this.second.toLong()
        val total1 =this.minute.toLong() * 60
        val total2 =this.hour.toLong() * 3600
        val total3 = this.day.toLong() * 3600 * 24
        val total4 = yearToDay * 3600 * 24
        val total5 = monthToDay * 3600 * 24

        return total + total1 + total2 + total3 + total4 + total5
    }

    fun get (unit: String): Int
    {
        return when (unit)
        {
            "s", "second" -> second
            "m", "minute"-> minute
            "h", "hour" -> hour
            "d", "day" -> day
            "M", "month" -> month
            "y", "year","Y"  -> year
            else  -> throw RuntimeException ("$unit 不是一个合法的时间单位 请用如下单位 => s m h d M y Y")
        }
    }

    /**
     * 前者必须小于后者
     * 才可以有意义
     * 前者占后者的比例
     */
    infix  fun percentageOf (timeContainer: TimeContainer): Double
    {
        if (this compareTo timeContainer > 0)
            throw  RuntimeException ("在使用 percentageOf 方法时 前者必须小于后者")
        if (this compareTo timeContainer == 0)
            return 1.0


        val moleculeSecond = this.toSecond().toDouble()
        val denominatorSecond = timeContainer.toSecond()

        return DecimalFormat ("#.###").format(moleculeSecond / denominatorSecond).toDouble()

    }

    /**
     * 前一个时间与后一个时间进行比较
     * 前者大返回 true
     */
    infix fun compareTo (other: TimeContainer): Int
    {
        if (this.year != other.year) return this.year - other.year
        if (this.month != other.month) return this.month - other.month
        if (this.day != other.day) return this.day - other.day
        if (this.hour != other.hour) return this.hour - other.hour
        if (this.minute != other.minute) return this.minute - other.minute
        return this.second - other.second
    }

    /**
     * 修复数值
     * 1). 修复溢出数值
     * 2). 修复缺少数值
     */
    fun fixes ()
    {

        this.second.takeIf { it >= SECONDTOMINUTE }?.let {
            this.minute += it / SECONDTOMINUTE
            this.second = it % SECONDTOMINUTE

        }

        this.second.takeIf { it < 0 }?.let {
            this.minute --
            this.second = SECONDTOMINUTE + it
        }

        this.minute.takeIf { it >= MINUTETOHOUR }?.let {
            this.hour += it / MINUTETOHOUR
            this.minute = it % MINUTETOHOUR

        }

        this.minute.takeIf { it < 0 }?.let {
            this.hour --
            this.minute = MINUTETOHOUR + it

        }

        this.hour.takeIf { it >= HOURTODAY }?.let {
            this.day += it / HOURTODAY
            this.hour = it % HOURTODAY

        }

        this.hour.takeIf { it < 0 }?.let {
            this.day --
            this.hour = HOURTODAY + it

        }
        //日到月转换就要顾及到闰年，平月大小月

        //是否修复完成
        var monthFinishFixes: Boolean
        var dayFinishFixes: Boolean
        var ignoreYear = 0

        //算取当前月份
        //如果当前月份是 12月 取模后是 0 所以直接给12
        //如果不是就单纯正常月份
        var autoTuneMonth = LocalDate.now().month.value
        //自动根据
        var autoTuneYear = LocalDate.now().year

        while (true)
        {
            //如果大于 10 年就先忽略 临时保存在这 ignoreYear
            if (this.year > 10)
            {
                ignoreYear = this.year
                this.year = 0
            }

            //判断是否是闰年 返回这个月有几天
            adaptiveDaysInMonth(autoTuneYear, autoTuneMonth)

            dayFinishFixes = day - DAYTOMONTH < 0

            if (dayFinishFixes)
            {
                if (this.day < 0)
                {
                    this.month --
                    this.day = DAYTOMONTH + this.day
                }
            } else
            {
                this.day -=  DAYTOMONTH
                this.month ++
            }

            autoTuneMonth = if(((LocalDate.now().month.value + this.month) % 12) == 0)
                12
            else
                (LocalDate.now().month.value + this.month) % 12

            adaptiveDaysInMonth(autoTuneYear, autoTuneMonth)

            monthFinishFixes = month - MONTHTOYEAR < 0

            if (monthFinishFixes)
            {
                if (this.month < 0)
                {
                    this.year --
                    this.month = MONTHTOYEAR + this.month
                }
            }
            else
            {
                this.month -= MONTHTOYEAR
                this.year++
            }

            autoTuneYear = LocalDate.now().year + this.year -1

            //如果都修复完成就结束死循环
            if (monthFinishFixes && dayFinishFixes)
            {
                this.year +=  ignoreYear
                break
            }
        }
    }

    override fun toString (): String
    {
        return "${year}y${month}M${day}d${hour}h${minute}m${second}s"
    }

     infix fun same (other: TimeContainer): Boolean {

        return year == other.year
                && month == other.month
                && day == other.day
                && hour == other.hour
                && minute == other.minute
                && second == other.second
    }

    companion object
    {
        /**
         * 获取当前时间
         */
        fun getCurrentTime (): TimeContainer
        {
            val timeContainer = TimeContainer(
                LocalDateTime.now().year,
                LocalDateTime.now().monthValue,
                LocalDateTime.now().dayOfMonth,
                LocalDateTime.now().hour,
                LocalDateTime.now().minute,
                LocalDateTime.now().second
            )
            timeContainer.fixes()
            return timeContainer
        }

        /**
         * 是否是闰年
         */
        fun isLeap (year: Int): Boolean
        {
            return Year.isLeap(year.toLong())
        }

        /**
         * 传入 2002y05M27d02m25s 字符串返回 TimeContainer 对象
         */
        fun parse (time: String): TimeContainer
        {
            val timeContainer = TimeContainer()

            //规定格式 必须是 多个数字和一个字母为一组的开头和结尾
            val prescribePattern = Regex ("^(\\d+[YyMmhds])+$")
            if (!prescribePattern.matches(time))
                throw RuntimeException ("字符串转时间的格式有误 => $time" )

            //匹配格式 匹配 多个数字和一个字母为一组的字符串
            val matchPattern = Regex ("\\d+[YyMmhds]")

            val matches = matchPattern.findAll(time)

            val allUnit: ArrayList<Char> = arrayListOf()

            matches.forEach {matchResult ->
                //匹配到的子字符串
                val result = matchResult.value
                //获取它的单位
                val unit = result[result.length - 1]
                //先判断所有单位是否已经存在过 如果存在过就说明 写重了就报错让他修改
                for (c in allUnit)
                {
                    if (unit == 'y' || unit == 'Y')
                    {
                        if (c == 'y' || c == 'Y')
                            throw RuntimeException("你有重复字符 -> $c 来自于 => $time")
                    } else if (c == unit)
                        throw RuntimeException("你有重复字符 -> $c 来自于 => $time")
                }

                //将当前单位存储
                allUnit.add(unit)

                //获取它的数值
                val number: Int
                try {
                    number = Integer.parseInt(result.substring(0, result.length-1))
                }catch (e: NumberFormatException)
                {
                    throw RuntimeException ("$time 不是一个合法的时间 例如: 2002y05M27d02m25s")
                }
                when (unit)
                {
                    's' -> timeContainer.second = number
                    'm' -> timeContainer.minute = number
                    'h' -> timeContainer.hour = number
                    'd' -> timeContainer.day = number
                    'M' -> timeContainer.month = number
                    'y', 'Y' -> timeContainer.year = number
                    else  -> throw RuntimeException ("$time 不是一个合法的时间 例如: 2002y12M40d02m25s")
                }
            }
            timeContainer.fixes()
            return timeContainer
        }
    }
}