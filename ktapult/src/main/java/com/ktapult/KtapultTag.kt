package com.ktapult

sealed interface KtapultTag

interface KtapultState : KtapultTag

interface KtapultEvent : KtapultTag