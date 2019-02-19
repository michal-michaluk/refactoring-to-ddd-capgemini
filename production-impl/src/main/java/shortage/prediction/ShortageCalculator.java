package shortage.prediction;

import entities.ShortageEntity;
import enums.DeliverySchema;
import external.CurrentStock;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class ShortageCalculator {
    private List<LocalDate> dates;

    private CurrentStock stock;
    private ProductionOutput outputs;
    private CurrentDemands demandsPerDay;

    public ShortageCalculator(CurrentStock stock,
                              List<LocalDate> dates,
                              ProductionOutput outputs,
                              CurrentDemands demandsPerDay) {
        this.stock = stock;
        this.dates = dates;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
    }

    public List<ShortageEntity> findShortages() {
        long level = stock.getLevel();

        List<ShortageEntity> gap = new LinkedList<>();
        for (LocalDate day : dates) {
            CurrentDemands.DailyDemand demand = demandsPerDay.get(day);
            if (demand == null) {
                level += outputs.getOutput(day);
                continue;
            }
            long produced = outputs.getOutput(day);

            long levelOnDelivery;
            if (demand.hasDeliverySchema(DeliverySchema.atDayStart)) {
                levelOnDelivery = level - demand.getAmount();
            } else if (demand.hasDeliverySchema(DeliverySchema.tillEndOfDay)) {
                levelOnDelivery = level - demand.getAmount() + produced;
            } else if (demand.hasDeliverySchema(DeliverySchema.every3hours)) {
                // TODO WTF ?? we need to rewrite that app :/
                throw new NotImplementedException();
            } else {
                // TODO implement other variants
                throw new NotImplementedException();
            }

            if (!(levelOnDelivery >= 0)) {
                ShortageEntity entity = new ShortageEntity();
                entity.setRefNo(outputs.getRefNo());
                entity.setFound(LocalDate.now());
                entity.setMissing(levelOnDelivery * -1L);
                entity.setAtDay(day);
                gap.add(entity);
            }
            long endOfDayLevel = level + produced - demand.getAmount();
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return gap;
    }
}
