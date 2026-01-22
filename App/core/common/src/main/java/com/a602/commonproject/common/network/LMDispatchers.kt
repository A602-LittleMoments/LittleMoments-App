package com.a602.commonproject.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val lnDispatcher : LMDispatchers)

enum class LMDispatchers{
    Default,
    IO,
}
