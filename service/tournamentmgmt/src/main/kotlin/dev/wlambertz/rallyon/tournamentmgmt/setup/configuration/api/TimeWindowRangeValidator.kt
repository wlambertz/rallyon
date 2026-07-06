package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class TimeWindowRangeValidator : ConstraintValidator<TimeWindowRange, TimeWindow> {

    override fun isValid(value: TimeWindow?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            return true
        }
        val start = value.start
        val end = value.end
        if (start == null || end == null) {
            return true
        }
        return !end.isBefore(start)
    }
}
