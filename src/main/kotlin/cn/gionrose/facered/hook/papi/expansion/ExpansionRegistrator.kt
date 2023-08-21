package cn.gionrose.facered.hook.papi.expansion

import me.clip.placeholderapi.expansion.PlaceholderExpansion

object ExpansionRegistrator
{
    val allExpansion = arrayListOf<PlaceholderExpansion>()

    fun registerAll ()
    {
        for (expansion in allExpansion) {
            expansion.register()
        }
    }

    fun register (vararg expansions: PlaceholderExpansion)
    {
        expansions.forEach {
            if (!allExpansion.contains(it))
                allExpansion.add(it)
        }

    }
}