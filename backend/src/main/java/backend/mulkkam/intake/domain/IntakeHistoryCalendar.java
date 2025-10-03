package backend.mulkkam.intake.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IntakeHistoryCalendar {

    private final Map<LocalDate, IntakeHistory> historiesByDate;
    private final Map<IntakeHistory, List<IntakeHistoryDetail>> detailsByHistory;

    public IntakeHistoryCalendar(List<IntakeHistory> intakeHistories, List<IntakeHistoryDetail> details) {
        this.historiesByDate = getInitHistories(intakeHistories);
        this.detailsByHistory = getInitDetails(details);
    }

    private Map<LocalDate, IntakeHistory> getInitHistories(List<IntakeHistory> intakeHistories) {
        return intakeHistories.stream()
                .collect(Collectors.toMap(
                        IntakeHistory::getHistoryDate, 
                        history -> history,
                        (existing, duplicate) -> existing)
                );
    }

    private Map<IntakeHistory, List<IntakeHistoryDetail>> getInitDetails(List<IntakeHistoryDetail> details) {
        Map<IntakeHistory, List<IntakeHistoryDetail>> result = new HashMap<>();
        for (IntakeHistoryDetail detail : details) {
            IntakeHistory intakeHistory = detail.getIntakeHistory();
            List<IntakeHistoryDetail> saved = result.getOrDefault(intakeHistory, new ArrayList<>());
            saved.add(detail);
            result.put(intakeHistory, saved);
        }
        result.values().forEach(d -> d.sort(Comparator.comparing(IntakeHistoryDetail::getIntakeTime, Comparator.reverseOrder())));
        return Collections.unmodifiableMap(result);
    }

    public boolean isExistHistoryOf(LocalDate date) {
        return historiesByDate.containsKey(date);
    }

    public IntakeHistory getIntakeHistory(LocalDate date) {
        return historiesByDate.get(date);
    }

    public List<IntakeHistoryDetail> getIntakeHistoryDetails(IntakeHistory intakeHistory) {
        return Collections.unmodifiableList(detailsByHistory.getOrDefault((intakeHistory), List.of()));
    }
}
