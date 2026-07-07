package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@Constraint(validatedBy = [TimeWindowRangeValidator::class])
@Target(CLASS, TYPE)
@Retention(RUNTIME)
annotation class TimeWindowRange(
    val message: String = "TimeWindow end must not be before start",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
