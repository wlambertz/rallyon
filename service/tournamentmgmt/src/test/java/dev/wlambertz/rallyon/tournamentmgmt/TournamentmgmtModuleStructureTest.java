package dev.wlambertz.rallyon.tournamentmgmt;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ConfigurationService;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.ConfigurationServiceImpl;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository.TournamentRepository;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.CreateDraftUseCase;
import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.PhasesService;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.RuleService;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class TournamentmgmtModuleStructureTest {

  @Test
  void verifiesNestedSetupModulesAndExposedInterfaces() {
    ApplicationModules modules = ApplicationModules.of(TournamentmgmtApplication.class);

    ApplicationModule configurationModule = modules.getModuleByName("setup.configuration").orElseThrow();
    ApplicationModule rulesModule = modules.getModuleByName("setup.rules").orElseThrow();
    ApplicationModule phasesModule = modules.getModuleByName("setup.phases").orElseThrow();

    assertThat(modules.getModuleByType(ConfigurationService.class)).contains(configurationModule);
    assertThat(modules.getModuleByType(RuleService.class)).contains(rulesModule);
    assertThat(modules.getModuleByType(PhasesService.class)).contains(phasesModule);

    assertThatCode(modules::verify).doesNotThrowAnyException();

    assertThat(configurationModule.isExposed(ConfigurationService.class)).isTrue();
    assertThat(rulesModule.isExposed(RuleService.class)).isTrue();
    assertThat(phasesModule.isExposed(PhasesService.class)).isTrue();

    assertThat(configurationModule.isExposed(ConfigurationServiceImpl.class)).isFalse();
    assertThat(configurationModule.isExposed(CreateDraftUseCase.class)).isFalse();
    assertThat(configurationModule.isExposed(TournamentRepository.class)).isFalse();
  }
}
