package xyz.thewhitedog9487.WebAPI.Miscellaneous

import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.runBlocking

val Message.link get(): String {
    return runBlocking(VirtualThreadCoroutineDispatcher) {
        data.guildId.value?.let { guildId ->
            return@runBlocking "https://discord.com/channels/$guildId/$channelId/$id" }
        val channel = kord.defaultSupplier.getChannelOfOrNull<GuildChannel>(channelId)
        val guild = channel?.guildId?.toString() ?: "@me"
        return@runBlocking "https://discord.com/channels/$guild/$channelId/$id" } }