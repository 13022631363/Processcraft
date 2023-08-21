package cn.gionrose.facered.util

import cn.gionrose.facered.hook.papi.PapiHook
import com.google.gson.GsonBuilder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * 2.0 就不会处理
 * 2 就会变成 2.0
 * 202 => 202.0
 */
fun String.notExistAddDot (): String
{
    if (contains("."))
        return this.trim()
   return "${this.trim()}.0"

}

/**
 * 此字符串不能是 "" " " "  "
 */
fun String.isNotNullCharacter (): Boolean
{
    return this.trim() != ""
}

/**
 * set time 1000 by op
 * 例如 传入 by op 就会把 by op 截掉返回
 */
fun String.onlyKeepCommand (notKeep: String): String
{
    val index = this.indexOf(notKeep)

    return this.substring(0, index)
}

/**
 * 通过 papi 字符转换
 */
fun String.papiParse (player: Player): String
{
    return PapiHook.parse(player, this)
}

fun String.sendCommand (sender: CommandSender)
{
    Bukkit.dispatchCommand(sender, this)
}

fun String.getPlayer (): Player?
{
    return Bukkit.getPlayerExact(this)
}

fun <T> T.toJson (): String
{
    return GsonBuilder ().create().toJson(this)
}

fun String.fromJson (): Map<String, Any>
{
    return GsonBuilder ().create().fromJson(this, Map::class.java) as Map<String, Any>
}