package shortage.prediction;

import dao.ProductionDao;
import entities.ProductionEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProductionOutputProvider {

    private ProductionDao productionDao;

    public ProductionOutputProvider(ProductionDao productionDao) {
        this.productionDao = productionDao;
    }

    public ProductionOutput createOutputs(String refNo, LocalDateTime fromTime) {
        List<ProductionEntity> productions = productionDao.findFromTime(refNo, fromTime);

        return new ProductionOutput(
                productions.stream()
                        .map(production -> production.getForm().getRefNo())
                        .findFirst()
                        .orElse(null),
                productions.stream().collect(Collectors.groupingBy(
                        production -> production.getStart().toLocalDate(),
                        Collectors.summingLong(ProductionEntity::getOutput)
                ))
        );
    }
}
