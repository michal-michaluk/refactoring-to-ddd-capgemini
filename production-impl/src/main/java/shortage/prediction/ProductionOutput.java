package shortage.prediction;

import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionOutput {
    private Map<LocalDate, List<ProductionEntity>> outputs = new HashMap<>();
    private String productRefNo = null;

    public ProductionOutput(List<ProductionEntity> productions) {
        for (ProductionEntity production : productions) {
            outputs.computeIfAbsent(production.getStart().toLocalDate(), localDate -> new ArrayList<>())
                    .add(production);
            productRefNo = production.getForm().getRefNo();
        }
    }

    public long getOutput(LocalDate day) {
        return outputs.get(day).stream()
                .mapToLong(ProductionEntity::getOutput)
                .sum();
    }

    public String getRefNo() {
        return productRefNo;
    }
}
