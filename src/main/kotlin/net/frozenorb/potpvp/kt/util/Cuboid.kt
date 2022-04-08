package net.frozenorb.potpvp.kt.util

import net.frozenorb.potpvp.PotPvPRP
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import java.util.ArrayList
import java.util.HashMap
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Entity

class Cuboid : Iterable<Block>, Cloneable, ConfigurationSerializable {

    protected val worldName: String
    val lowerX: Int
    val lowerY: Int
    val lowerZ: Int
    val upperX: Int
    val upperY: Int
    val upperZ: Int

    val lowerNE: Location
        get() = Location(this.world, this.lowerX.toDouble(), this.lowerY.toDouble(), this.lowerZ.toDouble())

    val upperSW: Location
        get() = Location(this.world, this.upperX.toDouble(), this.upperY.toDouble(), this.upperZ.toDouble())

    val blocks: List<Block>
        get() {
            val blockI = this.iterator()
            val copy = ArrayList<Block>()
            while (blockI.hasNext()) {
                copy.add(blockI.next())
            }
            return copy
        }

    val center: Location
        get() {
            val x1 = this.upperX + 1
            val y1 = this.upperY + 1
            val z1 = this.upperZ + 1
            return Location(this.world, this.lowerX + (x1 - this.lowerX) / 2.0, this.lowerY + (y1 - this.lowerY) / 2.0, this.lowerZ + (z1 - this.lowerZ) / 2.0)
        }

    val world: World
        get() =
            PotPvPRP.getInstance().server.getWorld(worldName)
                    ?: throw IllegalStateException("World '$worldName' is not loaded")

    val sizeX: Int
        get() = this.upperX - this.lowerX + 1

    val sizeY: Int
        get() = this.upperY - this.lowerY + 1

    val sizeZ: Int
        get() = this.upperZ - this.lowerZ + 1

    val volume: Int
        get() = this.sizeX * this.sizeY * this.sizeZ

    val averageLightLevel: Byte
        get() {
            var total = 0L
            var n = 0
            for (b in this) {
                if (b.isEmpty()) {
                    total += b.getLightLevel()
                    ++n
                }
            }
            return (if (n > 0) (total / n).toByte() else 0).toByte()
        }

    val chunks: List<Chunk>
        get() {
            val res = ArrayList<Chunk>()
            val w = this.world
            val x1 = this.lowerX and -0x10
            val x2 = this.upperX and -0x10
            val z1 = this.lowerZ and -0x10
            val z2 = this.upperZ and -0x10
            var x3 = x1
            while (x3 <= x2) {
                var z3 = z1
                while (z3 <= z2) {
                    res.add(w.getChunkAt(x3 shr 4, z3 shr 4))
                    z3 += 16
                }
                x3 += 16
            }
            return res
        }

    val walls: List<Block>
        get() {
            val blocks = ArrayList<Block>()
            val min = Location(this.world, this.lowerX.toDouble(), this.lowerY.toDouble(), this.lowerZ.toDouble())
            val max = Location(this.world, this.upperX.toDouble(), this.upperY.toDouble(), this.upperZ.toDouble())
            val minX = min.getBlockX()
            val minY = min.getBlockY()
            val minZ = min.getBlockZ()
            val maxX = max.getBlockX()
            val maxY = max.getBlockY()
            val maxZ = max.getBlockZ()
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    val minLoc = Location(this.world, x.toDouble(), y.toDouble(), minZ.toDouble())
                    val maxLoc = Location(this.world, x.toDouble(), y.toDouble(), maxZ.toDouble())
                    blocks.add(minLoc.getBlock())
                    blocks.add(maxLoc.getBlock())
                }
            }
            for (y2 in minY..maxY) {
                for (z in minZ..maxZ) {
                    val minLoc = Location(this.world, minX.toDouble(), y2.toDouble(), z.toDouble())
                    val maxLoc = Location(this.world, maxX.toDouble(), y2.toDouble(), z.toDouble())
                    blocks.add(minLoc.getBlock())
                    blocks.add(maxLoc.getBlock())
                }
            }
            return blocks
        }

    val faces: List<Block>
        get() {
            val blocks = ArrayList<Block>()
            val min = Location(this.world, this.lowerX.toDouble(), this.lowerY.toDouble(), this.lowerZ.toDouble())
            val max = Location(this.world, this.upperX.toDouble(), this.upperY.toDouble(), this.upperZ.toDouble())
            val minX = min.getBlockX()
            val minY = min.getBlockY()
            val minZ = min.getBlockZ()
            val maxX = max.getBlockX()
            val maxY = max.getBlockY()
            val maxZ = max.getBlockZ()
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    blocks.add(Location(this.world, x.toDouble(), y.toDouble(), minZ.toDouble()).getBlock())
                    blocks.add(Location(this.world, x.toDouble(), y.toDouble(), maxZ.toDouble()).getBlock())
                }
            }
            for (y2 in minY..maxY) {
                for (z in minZ..maxZ) {
                    blocks.add(Location(this.world, minX.toDouble(), y2.toDouble(), z.toDouble()).getBlock())
                    blocks.add(Location(this.world, maxX.toDouble(), y2.toDouble(), z.toDouble()).getBlock())
                }
            }
            for (z2 in minZ..maxZ) {
                for (x2 in minX..maxX) {
                    blocks.add(Location(this.world, x2.toDouble(), minY.toDouble(), z2.toDouble()).getBlock())
                    blocks.add(Location(this.world, x2.toDouble(), maxY.toDouble(), z2.toDouble()).getBlock())
                }
            }
            return blocks
        }

    @JvmOverloads
    constructor(l1: Location, l2: Location = l1) {
        if (!l1.getWorld().equals(l2.getWorld())) {
            throw IllegalArgumentException("Locations must be on the same world")
        }
        this.worldName = l1.getWorld().getName()
        this.lowerX = Math.min(l1.getBlockX(), l2.getBlockX())
        this.lowerY = Math.min(l1.getBlockY(), l2.getBlockY())
        this.lowerZ = Math.min(l1.getBlockZ(), l2.getBlockZ())
        this.upperX = Math.max(l1.getBlockX(), l2.getBlockX())
        this.upperY = Math.max(l1.getBlockY(), l2.getBlockY())
        this.upperZ = Math.max(l1.getBlockZ(), l2.getBlockZ())
    }

    constructor(other: Cuboid) : this(other.world.getName(), other.lowerX, other.lowerY, other.lowerZ, other.upperX, other.upperY, other.upperZ) {}

    constructor(world: World, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {
        this.worldName = world.getName()
        this.lowerX = Math.min(x1, x2)
        this.upperX = Math.max(x1, x2)
        this.lowerY = Math.min(y1, y2)
        this.upperY = Math.max(y1, y2)
        this.lowerZ = Math.min(z1, z2)
        this.upperZ = Math.max(z1, z2)
    }

    private constructor(worldName: String, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {
        this.worldName = worldName
        this.lowerX = Math.min(x1, x2)
        this.upperX = Math.max(x1, x2)
        this.lowerY = Math.min(y1, y2)
        this.upperY = Math.max(y1, y2)
        this.lowerZ = Math.min(z1, z2)
        this.upperZ = Math.max(z1, z2)
    }

    constructor(map: Map<String, Any>) {
        this.worldName = map["worldName"] as String
        this.lowerX = map["x1"] as Int
        this.upperX = map["x2"] as Int
        this.lowerY = map["y1"] as Int
        this.upperY = map["y2"] as Int
        this.lowerZ = map["z1"] as Int
        this.upperZ = map["z2"] as Int
    }

    override fun serialize(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map["worldName"] = this.worldName
        map["x1"] = this.lowerX
        map["y1"] = this.lowerY
        map["z1"] = this.lowerZ
        map["x2"] = this.upperX
        map["y2"] = this.upperY
        map["z2"] = this.upperZ
        return map
    }

    fun corners(): Array<Block> {
        val w = this.world
        return arrayOf(
                w.getBlockAt(this.lowerX, this.lowerY, this.lowerZ),
                w.getBlockAt(this.lowerX, this.lowerY, this.upperZ),
                w.getBlockAt(this.lowerX, this.upperY, this.lowerZ),
                w.getBlockAt(this.lowerX, this.upperY, this.upperZ),
                w.getBlockAt(this.upperX, this.lowerY, this.lowerZ),
                w.getBlockAt(this.upperX, this.lowerY, this.upperZ),
                w.getBlockAt(this.upperX, this.upperY, this.lowerZ),
                w.getBlockAt(this.upperX, this.upperY, this.upperZ)
        )
    }

    fun minCorners(): Array<Block> {
        val w = this.world
        return Array(4) { w.getBlockAt(this.lowerX, this.lowerY, this.lowerZ) }
    }

    fun expand(dir: CuboidDirection, amount: Int): Cuboid {
        when (dir) {
            CuboidDirection.NORTH -> {
                return Cuboid(this.worldName, this.lowerX - amount, this.lowerY, this.lowerZ, this.upperX, this.upperY, this.upperZ)
            }
            CuboidDirection.SOUTH -> {
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.lowerZ, this.upperX + amount, this.upperY, this.upperZ)
            }
            CuboidDirection.EAST -> {
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.lowerZ - amount, this.upperX, this.upperY, this.upperZ)
            }
            CuboidDirection.WEST -> {
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.lowerZ, this.upperX, this.upperY, this.upperZ + amount)
            }
            CuboidDirection.DOWN -> {
                return Cuboid(this.worldName, this.lowerX, this.lowerY - amount, this.lowerZ, this.upperX, this.upperY, this.upperZ)
            }
            CuboidDirection.UP -> {
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.lowerZ, this.upperX, this.upperY + amount, this.upperZ)
            }
            else -> {
                throw IllegalArgumentException("Invalid direction $dir")
            }
        }
    }

    fun shift(dir: CuboidDirection, amount: Int): Cuboid {
        return this.expand(dir, amount).expand(dir.opposite(), -amount)
    }

    fun outset(dir: CuboidDirection, amount: Int): Cuboid? {
        var c: Cuboid? = null
        when (dir) {
            CuboidDirection.HORIZONTAL -> {
                c = this.expand(CuboidDirection.NORTH, amount).expand(CuboidDirection.SOUTH, amount).expand(CuboidDirection.EAST, amount).expand(CuboidDirection.WEST, amount)
            }
            CuboidDirection.VERTICAL -> {
                c = this.expand(CuboidDirection.DOWN, amount).expand(CuboidDirection.UP, amount)
            }
            CuboidDirection.BOTH -> {
                c = this.outset(CuboidDirection.HORIZONTAL, amount)!!.outset(CuboidDirection.VERTICAL, amount)
            }
            else -> {
                throw IllegalArgumentException("Invalid direction $dir")
            }
        }
        return c
    }

    fun inset(dir: CuboidDirection, amount: Int): Cuboid? {
        return this.outset(dir, -amount)
    }

    fun contains(x: Int, y: Int, z: Int): Boolean {
        return x >= this.lowerX && x <= this.upperX && y >= this.lowerY && y <= this.upperY && z >= this.lowerZ && z <= this.upperZ
    }

    operator fun contains(b: Block): Boolean {
        return this.contains(b.getLocation())
    }

    operator fun contains(l: Location): Boolean {
        return this.worldName == l.getWorld().getName() && this.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ())
    }

    operator fun contains(e: Entity): Boolean {
        return this.contains(e.getLocation())
    }

    fun grow(i: Int): Cuboid {
        return this.expand(CuboidDirection.NORTH, i).expand(CuboidDirection.SOUTH, i).expand(CuboidDirection.EAST, i).expand(CuboidDirection.WEST, i)
    }

    fun contract(): Cuboid {
        return this.contract(CuboidDirection.DOWN).contract(CuboidDirection.SOUTH).contract(CuboidDirection.EAST).contract(CuboidDirection.UP).contract(CuboidDirection.NORTH).contract(CuboidDirection.WEST)
    }

    fun contract(dir: CuboidDirection): Cuboid {
        var face = this.getFace(dir.opposite())
        when (dir) {
            CuboidDirection.DOWN -> {
                while (face.containsOnly(0) && face.lowerY > this.lowerY) {
                    face = face.shift(CuboidDirection.DOWN, 1)
                }
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.lowerZ, this.upperX, face.upperY, this.upperZ)
            }
            CuboidDirection.UP -> {
                while (face.containsOnly(0) && face.upperY < this.upperY) {
                    face = face.shift(CuboidDirection.UP, 1)
                }
                return Cuboid(this.worldName, this.lowerX, face.lowerY, this.lowerZ, this.upperX, this.upperY, this.upperZ)
            }
            CuboidDirection.NORTH -> {
                while (face.containsOnly(0) && face.lowerX > this.lowerX) {
                    face = face.shift(CuboidDirection.NORTH, 1)
                }
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.lowerZ, face.upperX, this.upperY, this.upperZ)
            }
            CuboidDirection.SOUTH -> {
                while (face.containsOnly(0) && face.upperX < this.upperX) {
                    face = face.shift(CuboidDirection.SOUTH, 1)
                }
                return Cuboid(this.worldName, face.lowerX, this.lowerY, this.lowerZ, this.upperX, this.upperY, this.upperZ)
            }
            CuboidDirection.EAST -> {
                while (face.containsOnly(0) && face.lowerZ > this.lowerZ) {
                    face = face.shift(CuboidDirection.EAST, 1)
                }
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.lowerZ, this.upperX, this.upperY, face.upperZ)
            }
            CuboidDirection.WEST -> {
                while (face.containsOnly(0) && face.upperZ < this.upperZ) {
                    face = face.shift(CuboidDirection.WEST, 1)
                }
                return Cuboid(this.worldName, this.lowerX, this.lowerY, face.lowerZ, this.upperX, this.upperY, this.upperZ)
            }
            else -> {
                throw IllegalArgumentException("Invalid direction $dir")
            }
        }
    }

    fun getFace(dir: CuboidDirection): Cuboid {
        when (dir) {
            CuboidDirection.DOWN -> {
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.lowerZ, this.upperX, this.lowerY, this.upperZ)
            }
            CuboidDirection.UP -> {
                return Cuboid(this.worldName, this.lowerX, this.upperY, this.lowerZ, this.upperX, this.upperY, this.upperZ)
            }
            CuboidDirection.NORTH -> {
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.lowerZ, this.lowerX, this.upperY, this.upperZ)
            }
            CuboidDirection.SOUTH -> {
                return Cuboid(this.worldName, this.upperX, this.lowerY, this.lowerZ, this.upperX, this.upperY, this.upperZ)
            }
            CuboidDirection.EAST -> {
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.lowerZ, this.upperX, this.upperY, this.lowerZ)
            }
            CuboidDirection.WEST -> {
                return Cuboid(this.worldName, this.lowerX, this.lowerY, this.upperZ, this.upperX, this.upperY, this.upperZ)
            }
            else -> {
                throw IllegalArgumentException("Invalid direction $dir")
            }
        }
    }

    fun containsOnly(blockId: Int): Boolean {
        for (b in this) {
            if (b.getTypeId() !== blockId) {
                return false
            }
        }
        return true
    }

    fun getBoundingCuboid(other: Cuboid?): Cuboid {
        if (other == null) {
            return this
        }
        val xMin = Math.min(this.lowerX, other.lowerX)
        val yMin = Math.min(this.lowerY, other.lowerY)
        val zMin = Math.min(this.lowerZ, other.lowerZ)
        val xMax = Math.max(this.upperX, other.upperX)
        val yMax = Math.max(this.upperY, other.upperY)
        val zMax = Math.max(this.upperZ, other.upperZ)
        return Cuboid(this.worldName, xMin, yMin, zMin, xMax, yMax, zMax)
    }

    fun getRelativeBlock(x: Int, y: Int, z: Int): Block {
        return this.world.getBlockAt(this.lowerX + x, this.lowerY + y, this.lowerZ + z)
    }

    fun getRelativeBlock(w: World, x: Int, y: Int, z: Int): Block {
        return w.getBlockAt(this.lowerX + x, this.lowerY + y, this.lowerZ + z)
    }

    override fun iterator(): Iterator<Block> {
        return CuboidIterator(this.world, this.lowerX, this.lowerY, this.lowerZ, this.upperX, this.upperY, this.upperZ)
    }

    public override fun clone(): Cuboid {
        return Cuboid(this)
    }

    override fun toString(): String {
        return "Cuboid: " + this.worldName + "," + this.lowerX + "," + this.lowerY + "," + this.lowerZ + "=>" + this.upperX + "," + this.upperY + "," + this.upperZ
    }

    enum class CuboidDirection {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        UP,
        DOWN,
        HORIZONTAL,
        VERTICAL,
        BOTH,
        UNKNOWN;

        fun opposite(): CuboidDirection {
            when (this) {
                NORTH -> {
                    return SOUTH
                }
                EAST -> {
                    return WEST
                }
                SOUTH -> {
                    return NORTH
                }
                WEST -> {
                    return EAST
                }
                HORIZONTAL -> {
                    return VERTICAL
                }
                VERTICAL -> {
                    return HORIZONTAL
                }
                UP -> {
                    return DOWN
                }
                DOWN -> {
                    return UP
                }
                BOTH -> {
                    return BOTH
                }
                else -> {
                    return UNKNOWN
                }
            }
        }
    }

    inner class CuboidIterator(private val w: World, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) : Iterator<Block> {
        private val baseX: Int
        private val baseY: Int
        private val baseZ: Int
        private var x: Int = 0
        private var y: Int = 0
        private var z: Int = 0
        private val sizeX: Int
        private val sizeY: Int
        private val sizeZ: Int

        init {
            this.baseX = Math.min(x1, x2)
            this.baseY = Math.min(y1, y2)
            this.baseZ = Math.min(z1, z2)
            this.sizeX = Math.abs(x2 - x1) + 1
            this.sizeY = Math.abs(y2 - y1) + 1
            this.sizeZ = Math.abs(z2 - z1) + 1
            val x3 = false
            this.z = if (x3) 1 else 0
            this.y = if (x3) 1 else 0
            this.x = if (x3) 1 else 0
        }

        override fun hasNext(): Boolean {
            return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ
        }

        override fun next(): Block {
            val b = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z)
            if (++this.x >= this.sizeX) {
                this.x = 0
                if (++this.y >= this.sizeY) {
                    this.y = 0
                    ++this.z
                }
            }
            return b
        }
    }
}