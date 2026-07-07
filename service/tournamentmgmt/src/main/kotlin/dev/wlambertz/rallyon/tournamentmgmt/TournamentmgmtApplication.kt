package dev.wlambertz.rallyon.tournamentmgmt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.modulith.Modulith

@SpringBootApplication
@Modulith
class TournamentmgmtApplication

fun main(args: Array<String>) {
    runApplication<TournamentmgmtApplication>(*args)
}
