package shortage.prediction;

import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionOutput {
    private Map<LocalDate, ProductionEntity> outputs = new HashMap<>();
    private String productRefNo = null;

    public ProductionOutput(List<ProductionEntity> productions) {
        for (ProductionEntity production : productions) {
            outputs.put(production.getStart().toLocalDate(), production);
            productRefNo = production.getForm().getRefNo();
        }
    }

    public long getOutput(LocalDate day) {
        ProductionEntity production = outputs.get(day);
        if (production != null) {
            return production.getOutput();
        } else {
            return 0;
        }
    }

    public String getRefNo() {
        return productRefNo;
    }
}
