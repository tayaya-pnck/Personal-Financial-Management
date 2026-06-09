package com.finance.tracker.report;

import com.finance.tracker.common.CurrentUser;
import com.finance.tracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(@CurrentUser User user) {
        return ResponseEntity.ok(reportService.getDashboard(user.getId()));
    }

    @GetMapping("/spending-by-category")
    public ResponseEntity<List<CategorySpending>> getSpendingByCategory(
            @CurrentUser User user,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        var start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().withDayOfMonth(1);
        var end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        return ResponseEntity.ok(reportService.getSpendingByCategory(user.getId(), start, end));
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<List<MonthlySummary>> getMonthlySummary(
            @CurrentUser User user,
            @RequestParam(defaultValue = "12") int months) {
        return ResponseEntity.ok(reportService.getMonthlySummary(user.getId(), months));
    }
}
