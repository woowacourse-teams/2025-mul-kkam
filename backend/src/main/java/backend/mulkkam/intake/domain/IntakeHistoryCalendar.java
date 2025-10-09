package backend.mulkkam.intake.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IntakeHistoryCalendar {

    private final Map<LocalDate, IntakeHistory> historyByDates;
    private final Map<IntakeHistory, List<IntakeHistoryDetail>> detailsByHistory;

    public IntakeHistoryCalendar(List<IntakeHistory> intakeHistories, List<IntakeHistoryDetail> details) {
        this.historyByDates = getInitHistories(intakeHistories);
        this.detailsByHistory = getInitDetails(details);
    }

    private Map<LocalDate, IntakeHistory> getInitHistories(List<IntakeHistory> intakeHistories) {
        Map<LocalDate, IntakeHistory> map = new HashMap<>();
        for (IntakeHistory history : intakeHistories) {
            map.put(history.getHistoryDate(), history);
        }
        return map;
    }

    private Map<IntakeHistory, List<IntakeHistoryDetail>> getInitDetails(List<IntakeHistoryDetail> details) {
        Map<IntakeHistory, List<IntakeHistoryDetail>> result = new HashMap<>();
        for (IntakeHistoryDetail detail : details) {
            IntakeHistory intakeHistory = detail.getIntakeHistory();
            List<IntakeHistoryDetail> intakeHistoryDetails = result.computeIfAbsent(intakeHistory,
                    history -> new ArrayList<>());
            intakeHistoryDetails.add(detail);
        }
        result.values().forEach(
                intakeHistoryDetails -> intakeHistoryDetails.sort(
                        Comparator.comparing(
                                IntakeHistoryDetail::getIntakeTime, Comparator.reverseOrder()
                        )
                )
        );
        return Collections.unmodifiableMap(result);
    }

    public boolean isExistHistoryOf(LocalDate date) {
        return historyByDates.containsKey(date);
    }

    public IntakeHistory getHistoryOf(LocalDate date) {
        return historyByDates.get(date);
    }

    public List<IntakeHistoryDetail> getHistoryDetails(IntakeHistory intakeHistory) {
        return Collections.unmodifiableList(detailsByHistory.getOrDefault((intakeHistory), List.of()));
    }

    public Optional<IntakeHistory> findHistoryOf(LocalDate date) {
        return Optional.of(historyByDates.get(date));
    }
}
