package net.frozenorb.potpvp.kt.redis

import net.frozenorb.potpvp.PotPvPSI
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.io.Closeable

/**
 * A class used for managing Jedis connections and executing Jedis commands.
 */
class Redis : Closeable {

    var localJedisPool: JedisPool? = null
    var backboneJedisPool: JedisPool? = null

    fun load(localCredentials: RedisCredentials, backboneCredentials: RedisCredentials) {
        try {
            val password = if (localCredentials.shouldAuthenticate()) {
                localCredentials.password!!
            } else {
                null
            }

            localJedisPool = JedisPool(JedisPoolConfig(), localCredentials.host, localCredentials.port, TIMEOUT, password, localCredentials.dbId)
        } catch (e: Exception) {
            PotPvPSI.getInstance().logger.warning("Couldn't connect to a Redis instance at ${localCredentials.host}:${localCredentials.port}")
            e.printStackTrace()
        }

        try {
            val password = if (backboneCredentials.shouldAuthenticate()) {
                backboneCredentials.password!!
            } else {
                null
            }

            backboneJedisPool = JedisPool(JedisPoolConfig(), backboneCredentials.host, backboneCredentials.port, TIMEOUT, password, backboneCredentials.dbId)
        } catch (e: Exception) {
            PotPvPSI.getInstance().logger.warning("Couldn't connect to a Backbone Redis instance at ${backboneCredentials.host}:${backboneCredentials.port}")
            e.printStackTrace()
        }
    }

    /**
     * Close any open Jedis connections.
     */
    override fun close() {
        if (localJedisPool != null && !localJedisPool!!.isClosed) {
            localJedisPool!!.close()
        }

        if (backboneJedisPool != null && !backboneJedisPool!!.isClosed) {
            backboneJedisPool!!.close()
        }
    }

    /**
     * A functional method for using a pooled [Jedis] resource and returning data.
     *
     * @param lambda the function
     */
    fun <T> runRedisCommand(lambda: (Jedis) -> T): T {
        if (localJedisPool == null || localJedisPool!!.isClosed) {
            throw IllegalStateException("Local jedis pool couldn't connect or is closed")
        }

        try {
            localJedisPool!!.resource.use { jedis -> return lambda(jedis) }
        } catch (e: Exception) {
            throw RuntimeException("Could not use resource and return", e)
        }
    }

    /**
     * A functional method for using a pooled [Jedis] resource and returning data.
     *
     * @param lambda the function
     */
    fun <T> runBackboneRedisCommand(lambda: (Jedis) -> T): T {
        if (backboneJedisPool == null || backboneJedisPool!!.isClosed) {
            throw IllegalStateException("Backbone jedis pool couldn't connect or is closed")
        }

        try {
            backboneJedisPool!!.resource.use { jedis -> return lambda(jedis) }
        } catch (e: Exception) {
            throw RuntimeException("Could not use resource and return", e)
        }
    }

    companion object {
        private const val TIMEOUT = 5000
    }

}