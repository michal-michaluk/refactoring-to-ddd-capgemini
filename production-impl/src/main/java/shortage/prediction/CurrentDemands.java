package shortage.prediction;

import enums.DeliverySchema;

import java.time.LocalDate;
import java.util.Map;

public class CurrentDemands {
    private final Map<LocalDate, DailyDemand> demandsPerDay;

    public CurrentDemands(Map<LocalDate, DailyDemand> demandsPerDay) {
        this.demandsPerDay = demandsPerDay;
    }

    public DailyDemand get(LocalDate day) {
        return demandsPerDay.getOrDefault(day, null);
    }

    public static class DailyDemand {
        private final DeliverySchema deliverySchema;
        private final long level;

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
