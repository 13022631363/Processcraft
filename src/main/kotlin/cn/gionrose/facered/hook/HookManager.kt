package cn.gionrose.facered.hook

object HookManager
{
    val allHook = arrayListOf<Hook>()

    fun load ()
    {
        allHook.forEach {
            it.load()
        }
    }

    fun register (vararg hooks: Hook)
    {
        hooks.forEach {
            if (!allHook.contains(it))
                allHook.add(it)
        }
    }
}