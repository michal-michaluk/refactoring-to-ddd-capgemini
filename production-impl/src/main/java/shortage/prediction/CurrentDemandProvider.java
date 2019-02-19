package shortage.prediction;

import dao.DemandDao;
import entities.DemandEntity;
import shortage.prediction.CurrentDemands.DailyDemand;
import tools.Util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CurrentDemandProvider {
    private DemandDao demandDao;

    public CurrentDemandProvider(DemandDao demandDao) {
        this.demandDao = demandDao;
    }

    public CurrentDemands createDemands(String refNo, LocalDateTime fromTime) {
        List<DemandEntity> demands = demandDao.findFrom(fromTime, refNo);

        Map<LocalDate, DailyDemand> map = Collections.unmodifiableMap(
                demands.stream().collect(Collectors.toMap(
                        DemandEntity::getDay,
                        demand -> new DailyDemand(Util.getDeliverySchema(demand), Util.getLevel(demand)))
                ));
        return new CurrentDemands(map);
    }
}
