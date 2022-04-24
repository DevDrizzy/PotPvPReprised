# PotPvP Reprised

A fork of PotPvP, porting it to 1.8 and changing mSpigot's dependency to [CarbonSpigot](https://polymart.org/resource/1-8-carbonspigot.1341).

## Information
This fork has also changed all APIs utilized by the plugin like Scoreboard and NameTagAPI.
Meanwhile, I have removed TablistAPI because I don't have an API that I can give out for free.
Although in the future, I might add an open source TablistAPI in this later but right now I don't have the time to do that.

## Changes

- Added UN-TESTED holograms w/config (Might not work and could be buggy af)
- Removed Redis and its usages
- Removed PlayerMoveEvent usage to hold player at spawn point and utilized PlayerUtil#denyMovement
- Removed Token based Stats resetting and made it admin only
- Completely changed all permissions
- New CommandAPI
- Ported NameTagAPI (Still not working for some reason?)
- Removed a lot of junk from kotlin qLib
- Removed unused dependencies
- Cleaned up general code
- Changed from Fanciful API to Clickable Util
- Removed Tablist and its API (Reason: Incompatibility and time shortage)

## Compiling
You are required to compile this with Java 8+ and must use maven.
Secondly, this also requires Refine's fork of Drink CommandAPI.
You can download it here,

[Download Here](https://cdn.discordapp.com/attachments/826102925805092885/966575292460179536/CommandAPI-1.1-SNAPSHOT.jar)

After downloading, run this maven command with cmd or your IDE.
```
mvn install:install-file -Dfile=<DOWNLOADED JAR FILE LOCATION.jar> -DgroupId=xyz.refinedev.api -DartifactId=CommandAPI -Dversion=1.1-SNAPSHOT -Dpackaging=jar
```

## Note
I don't claim ANY ownership on this code, I have simply ported this to 1.8 with a few additions as my code.
All ownership goes to Hylist/FrozenOrb. I made this open source because PotPvP forks are going all around the community
being sold left to right, no point in having it privately saved.

**Credit goes to FrozenOrb/HylistGames**

Using this on your servers or forking it (and selling that fork) is allowed as far as I can give authority. All I ask is you keep the original developers' credits in there along with mine in there as "DevDrizzy". Not having these credits will be considered as you skidding this.

## Dependency
As many developers know that PotPvP requires qLib and mSpigot to function. While, qLib was removed and converted to kotlin in PotPvP-SI, mSpigot was never changed and remained as is for years. I changed this by porting PotPvP to 1.8 and replacing mSpigot with my own, [CarbonSpigot](https://polymart.org/resource/1-8-carbonspigot.1341), which supports the "Chunk Snapshots" required by PotPvP to handle arenas properly.

Now, you may try to replicate it, but I highly doubt you will succeed in getting the same results as Carbon. So due to this, You are required to purchase [CarbonSpigot](https://polymart.org/resource/1-8-carbonspigot.1341) in order to use this. But, by all means, using your own fork is not forbidden in any way.
