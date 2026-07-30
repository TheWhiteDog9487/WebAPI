package xyz.thewhitedog9487.WebAPI.Exception

import dev.kord.common.entity.Snowflake

class NoSuchDiscordChannelException(val TargetChannelId: Snowflake,
                                    message: String? = null,
                                    cause: Throwable? = null): Exception(message ?: "ID为${TargetChannelId}的频道不存在", cause)