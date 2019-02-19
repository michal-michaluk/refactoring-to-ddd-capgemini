package services.impl;

import api.QualityService;
import api.StorageUnit;
import shortage.prediction.ShortagePredictionService;

public class QualityServiceImpl implements QualityService {

    //Inject all
    private ShortagePredictionService shortagePrediction;

    /**
     * <pre>
     *  Lock all parts from storage unit on stock.
     *  parts from storage unit are locked on stock
     *  If locking parts can lead to insufficient stock for next deliveries,
     *    parts recovery should have high priority.
     *  If there is a potential shortage in particular days,
     *    we need to rise an soft notification to planner.
     *  </pre>
     */
    //Transactional
    @Override
    public void lock(StorageUnit unit) {
        shortagePrediction.processLocking(unit.getProductRefNo());
    }

    /**
     * <pre>
     *  Unlock storage unit, recover X parts, Y parts was scrapped.
     *  stock.unlock(storageUnit, recovered, scrapped)
     *  Recovered parts are back on stock.
     *  Scrapped parts are removed from stock.
     *  If demand is not fulfilled by current product stock and production forecast
     *    there is a shortage in particular days and we need to rise an alert.
     * </pre>
     */
    //Transactional
    @Override
    public void unlock(StorageUnit unit, long recovered, long scrapped) {
        shortagePrediction.processLocking(unit.getProductRefNo());
    }

}
