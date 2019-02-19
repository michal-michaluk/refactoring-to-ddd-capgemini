package shortage.prediction;

import entities.ProductionEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ProductionOutputProvider {

    private List<ProductionEntity> productions;

    public ProductionOutputProvider(List<ProductionEntity> productions) {
        this.productions = productions;
    }

    public ProductionOutput createOutputs() {
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
