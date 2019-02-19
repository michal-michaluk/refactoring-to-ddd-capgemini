package shortage.prediction;

import entities.DemandEntity;
import shortage.prediction.CurrentDemands.DailyDemand;
import tools.Util;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CurrentDemandProvider {
    private List<DemandEntity> demands;

    public CurrentDemandProvider(List<DemandEntity> demands) {
        this.demands = demands;
    }

    public CurrentDemands createDemands() {
        Map<LocalDate, DailyDemand> map = Collections.unmodifiableMap(
                demands.stream().collect(Collectors.toMap(
                        DemandEntity::getDay,
                        demand -> new DailyDemand(Util.getDeliverySchema(demand), Util.getLevel(demand)))
                ));
        return new CurrentDemands(map);
    }
}
