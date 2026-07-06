package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.RegistrationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.SchedulingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tournaments", schema = "tournamentmgmt")
@Getter
@Setter
public class TournamentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizer_id", nullable = false)
    private long organizerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "locale")
    private String locale;

    @Column(name = "schedule_start")
    private Instant scheduleStart;

    @Column(name = "schedule_end")
    private Instant scheduleEnd;

    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "venue_street")
    private String venueStreet;

    @Column(name = "venue_postal_code")
    private String venuePostalCode;

    @Column(name = "venue_city")
    private String venueCity;

    @Column(name = "venue_capacity_amount")
    private Integer venueCapacityAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "venue_capacity_unit")
    private Capacity.Unit venueCapacityUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "format")
    private TournamentFormat format;

    @Column(name = "capacity_max_participants")
    private Integer capacityMaxParticipants;

    @Enumerated(EnumType.STRING)
    @Column(name = "team_size")
    private TeamSize teamSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_policy")
    private RegistrationPolicy registrationPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "scheduling_policy")
    private SchedulingPolicy schedulingPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "court_allocation_policy")
    private CourtAllocationPolicy courtAllocationPolicy;

    @Column(name = "scoring_points_per_game")
    private Integer scoringPointsPerGame;

    @Column(name = "scoring_games_per_match")
    private Integer scoringGamesPerMatch;

    @Column(name = "scoring_win_by_two")
    private Boolean scoringWinByTwo;

    @Column(name = "scoring_cap_points")
    private Integer scoringCapPoints;

    @Column(name = "tie_break_use_set_difference")
    private Boolean tieBreakUseSetDifference;

    @Column(name = "tie_break_use_points_ratio")
    private Boolean tieBreakUsePointsRatio;

    @Column(name = "tie_break_use_buchholz")
    private Boolean tieBreakUseBuchholz;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_duration_policy")
    private MatchDurationPolicy matchDurationPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "seeding_policy")
    private SeedingPolicy seedingPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TournamentStatus status;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by_user_id", nullable = false)
    private long createdByUserId;

    @Column(name = "last_modified_at", nullable = false)
    private Instant lastModifiedAt;

    @Column(name = "last_modified_by_user_id", nullable = false)
    private long lastModifiedByUserId;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistrationWindowEntity> registrationWindows = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourtEntity> courts = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisciplineEntity> disciplines = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantEntity> participants = new ArrayList<>();

    public TournamentStatus getStatus() {
        return status;
    }

    public Long getVersion() {
        return version;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public void setLastModifiedByUserId(long lastModifiedByUserId) {
        this.lastModifiedByUserId = lastModifiedByUserId;
    }
}
