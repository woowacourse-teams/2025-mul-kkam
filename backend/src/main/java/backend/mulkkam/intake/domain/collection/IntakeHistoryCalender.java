package backend.mulkkam.intake.domain.collection;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntakeHistoryCalender {

    private final Map<LocalDate, IntakeHistory> historiesByDate;
    private final Map<IntakeHistory, List<IntakeHistoryDetail>> detailsByHistory;

    public IntakeHistoryCalender(List<IntakeHistoryDetail> details) {
        this.historiesByDate = new HashMap<>();
        this.detailsByHistory = new HashMap<>();
        init(details);
    }

    private void init(List<IntakeHistoryDetail> details) {
        for (IntakeHistoryDetail detail : details) {
            IntakeHistory intakeHistory = detail.getIntakeHistory();
            LocalDate historyDate = intakeHistory.getHistoryDate();
            historiesByDate.put(historyDate, intakeHistory);

            List<IntakeHistoryDetail> saved = detailsByHistory.getOrDefault(intakeHistory, new ArrayList<>());
            saved.add(detail);
            detailsByHistory.put(intakeHistory, saved);
        }
        detailsByHistory.values().forEach(d -> d.sort(Comparator.comparing(IntakeHistoryDetail::getIntakeTime, Comparator.reverseOrder())));
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
