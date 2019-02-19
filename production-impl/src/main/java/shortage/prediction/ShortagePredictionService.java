package shortage.prediction;

import dao.DemandDao;
import dao.ProductionDao;
import dao.ShortageDao;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import external.JiraService;
import external.NotificationsService;
import external.StockService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {

    private ShortageDao shortageDao;
    private ProductionDao productionDao;
    private StockService stockService;
    private DemandDao demandDao;
    private ShortagePredictionService shortagesPrediction;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;

    public void processProductionPlanChanged(List<ProductionEntity> products) {
        LocalDate today = LocalDate.now(clock);

        for (ProductionEntity production : products) {
            CurrentStock currentStock = stockService.getCurrentStock(production.getForm().getRefNo());
            List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                    today, confShortagePredictionDaysAhead,
                    currentStock,
                    productionDao.findFromTime(production.getForm().getRefNo(), today.atStartOfDay()),
                    demandDao.findFrom(today.atStartOfDay(), production.getForm().getRefNo())
            );
            List<ShortageEntity> previous = shortageDao.getForProduct(production.getForm().getRefNo());
            if (!shortages.isEmpty() && !shortages.equals(previous)) { // ShortagePredicted
                notificationService.markOnPlan(shortages);
                if (currentStock.getLocked() > 0 &&
                        shortages.get(0).getAtDay()
                                .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                    jiraService.increasePriorityFor(production.getForm().getRefNo());
                }
                shortageDao.save(shortages);
            }
            if (shortages.isEmpty() && !previous.isEmpty()) { // ShortageSolved
                shortageDao.delete(production.getForm().getRefNo());
            }
        }
    }

    public void processDemandChanged(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                today, confShortagePredictionDaysAhead,
                stock,
                productionDao.findFromTime(productRefNo, today.atStartOfDay()),
                demandDao.findFrom(today.atStartOfDay(), productRefNo)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            if (stock.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortageDao.save(shortages);
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processLocking(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                today, confShortagePredictionDaysAhead,
                currentStock,
                productionDao.findFromTime(productRefNo, today.atStartOfDay()),
                demandDao.findFrom(today.atStartOfDay(), productRefNo)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.softNotifyPlanner(shortages);
            if (currentStock.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processStockChanged(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                today, confShortagePredictionDaysAhead,
                currentStock,
                productionDao.findFromTime(productRefNo, today.atStartOfDay()),
                demandDao.findFrom(today.atStartOfDay(), productRefNo)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (shortages != null && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            if (currentStock.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }
}
