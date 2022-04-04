package net.frozenorb.potpvp.kt.util

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Entity
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

object Reflections {

    val ENTITY_PLAYER_CLASS = getNMSClass("EntityPlayer")!!
    val CRAFT_HUMAN_ENTITY_CLASS = getCBClass("entity.CraftHumanEntity")!!
    val WORLD_CLASS = getNMSClass("World")!!
    val WORLD_SERVER_CLASS = getNMSClass("WorldServer")!!
    val MINECRAFT_SERVER_CLASS = getNMSClass("MinecraftServer")!!
    val PLAYER_INTERACT_MANAGER_CLASS = getNMSClass("PlayerInteractManager")!!
    private val GAME_PROFILE_CLASS_LEGACY: Class<*>? = getClass("com.mojang.authlib.GameProfile")
    private val GAME_PROFILE_CLASS: Class<*>? = getClass("com.mojang.authlib.GameProfile")

    fun cbVer(): String {
        return "org.bukkit.craftbukkit." + ver() + "."
    }

    fun nmsVer(): String {
        return "net.minecraft.server." + ver() + "."
    }

    fun ver(): String {
        val pkg = Bukkit.getServer().javaClass.getPackage().name
        return pkg.substring(pkg.lastIndexOf(".") + 1)
    }

    fun wrapperToPrimitive(clazz: Class<*>): Class<*> {
        if (clazz == Boolean::class.java) return Boolean::class.javaPrimitiveType as Class<*>
        if (clazz == Int::class.java) return Int::class.javaPrimitiveType as Class<*>
        if (clazz == Double::class.java) return Double::class.javaPrimitiveType as Class<*>
        if (clazz == Float::class.java) return Float::class.javaPrimitiveType as Class<*>
        if (clazz == Long::class.java) return Long::class.javaPrimitiveType as Class<*>
        if (clazz == Short::class.java) return Short::class.javaPrimitiveType as Class<*>
        if (clazz == Byte::class.java) return Byte::class.javaPrimitiveType as Class<*>
        if (clazz == Void::class.java) return Void.TYPE
        return if (clazz == Char::class.java) Char::class.javaPrimitiveType as Class<*> else clazz
    }

    fun toParamTypes(vararg params: Any): Array<Class<*>?> {
        val classes = arrayOfNulls<Class<*>>(params.size)
        for (i in params.indices)
            classes[i] = wrapperToPrimitive(params[i].javaClass)
        return classes
    }

    fun getMinecraftServer(): Any? {
        return callMethod(Bukkit.getServer(), "getServer")
    }

    fun getTPS(): Double {
        return (getFieldValue(getMinecraftServer()!!, "recentTps") as DoubleArray)[0]
    }

    fun getHandle(e: Entity): Any? {
        return callMethod(e, "getHandle")
    }

    fun getHandle(w: World): Any? {
        return callMethod(w, "getHandle")
    }

    fun getGameProfileClass(): Class<*> {
        return if (GAME_PROFILE_CLASS_LEGACY != null) {
            GAME_PROFILE_CLASS_LEGACY
        } else {
            GAME_PROFILE_CLASS!!
        }
    }

    fun createGameProfile(uuid: UUID, name: String): Any {
        return callConstructor(getGameProfileClass(), uuid, name)!!
    }

    fun getClass(name: String): Class<*>? {
        try {
            return Class.forName(name)
        } catch (e: Exception) {
            return null
        }
    }

    fun getNMSClass(name: String): Class<*>? {
        return getClass(nmsVer() + name)
    }

    fun getCBClass(name: String): Class<*>? {
        return getClass(cbVer() + name)
    }

    fun getMethod(clazz: Class<*>, method: String, vararg params: Class<*>): Method? {
        try {
            val m = clazz.getMethod(method, *params)
            m.isAccessible = true
            return m
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getDeclaredMethod(clazz: Class<*>, method: String, vararg params: Class<*>): Method? {
        return getDeclaredMethod(false, clazz, method, *params)
    }

    fun getDeclaredMethod(suppressed: Boolean, clazz: Class<*>, method: String, vararg params: Class<*>): Method? {
        try {
            val m = clazz.getDeclaredMethod(method, *params)
            m.isAccessible = true
            return m
        } catch (e: Exception) {
            if (!suppressed) {
                e.printStackTrace()
            }

            return null
        }
    }

    fun callMethod(`object`: Any, method: String, vararg params: Any): Any? {
        try {
            val m = `object`.javaClass.getMethod(method, *toParamTypes(*params))
            m.isAccessible = true
            return m.invoke(`object`, *params)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    fun callConstructor(clazz: Class<*>, vararg params: Any): Any? {
        try {
            val con = clazz.getConstructor(*toParamTypes(*params))
            con.isAccessible = true
            return con.newInstance(*params)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getConstructor(clazz: Class<*>, vararg params: Class<*>): Constructor<*>? {
        try {
            val con = clazz.getConstructor(*params)
            con.isAccessible = true
            return con
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getField(clazz: Class<*>, field: String): Field? {
        try {
            val f = clazz.getField(field)
            f.isAccessible = true
            return f
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getFieldValue(`object`: Any, field: String): Any? {
        try {
            val f = `object`.javaClass.getField(field)
            f.isAccessible = true
            return f.get(`object`)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun setFieldValue(`object`: Any, field: String, value: Any) {
        try {
            val f = `object`.javaClass.getField(field)
            f.isAccessible = true
            f.set(`object`, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setDeclaredField(`object`: Any, field: String, value: Any) {
        try {
            val f = `object`.javaClass.getDeclaredField(field)
            f.isAccessible = true
            f.set(`object`, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDeclaredField(`object`: Any?, field: String): Any? {
        try {
            val f = `object`!!.javaClass.getDeclaredField(field)
            f.isAccessible = true
            return f.get(`object`)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}