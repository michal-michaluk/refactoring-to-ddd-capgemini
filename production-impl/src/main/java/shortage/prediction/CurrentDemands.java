package shortage.prediction;

import entities.DemandEntity;
import enums.DeliverySchema;
import tools.Util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentDemands {
    private Map<LocalDate, DemandEntity> demandsPerDay = new HashMap<>();

    public CurrentDemands(List<DemandEntity> demands) {
        for (DemandEntity demand1 : demands) {
            demandsPerDay.put(demand1.getDay(), demand1);
        }
    }

    public DailyDemand get(LocalDate day) {
        DemandEntity demand = demandsPerDay.get(day);
        if (demand != null) {
            return new DailyDemand(Util.getDeliverySchema(demand), Util.getLevel(demand));
        }
        return null;
    }

    public static class DailyDemand {
        private DeliverySchema deliverySchema;
        private long level;

        public DailyDemand(DeliverySchema deliverySchema, long level) {
            this.deliverySchema = deliverySchema;
            this.level = level;
        }

        public boolean hasDeliverySchema(DeliverySchema schema) {
            return deliverySchema == schema;
        }

        public long getAmount() {
            return level;
        }
    }
}
