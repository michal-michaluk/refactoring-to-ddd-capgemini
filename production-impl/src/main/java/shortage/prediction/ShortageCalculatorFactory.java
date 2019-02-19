package shortage.prediction;

import external.CurrentStock;
import external.StockService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortageCalculatorFactory {
    private int daysAhead;
    private StockService stockService;
    private ProductionOutputProvider productions;
    private CurrentDemandProvider demands;
    private Clock clock;

    public ShortageCalculator get(String refNo) {
        LocalDate today = LocalDate.now(clock);

        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        CurrentStock stock = stockService.getCurrentStock(refNo);
        ProductionOutput outputs = productions.createOutputs(refNo, today.atStartOfDay());
        CurrentDemands demandsPerDay = demands.createDemands(refNo, today.atStartOfDay());
        return new ShortageCalculator(stock, dates, outputs, demandsPerDay);
    }
}
