package mdt.timeseries;

import de.fraunhofer.iosb.ilt.faaast.service.persistence.PersistenceConfig;

/**
 *
 * @author Kang-Woo Lee (ETRI)
 */
public class PersistenceTimeSeriesHolderConfig extends PersistenceConfig<TimeSeriesHoldingPersistence> {
    private String target;

    public static Builder builder() {
        return new Builder();
    }
    
    public void target(String target) {
        this.target = target;
    }

    private abstract static class AbstractBuilder<T extends PersistenceTimeSeriesHolderConfig,
    												B extends AbstractBuilder<T, B>>
            extends PersistenceConfig.AbstractBuilder<TimeSeriesHoldingPersistence, T, B> {

        public B target(String value) {
            getBuildingInstance().target(value);
            return getSelf();
        }
    }

    public static class Builder extends AbstractBuilder<PersistenceTimeSeriesHolderConfig, Builder> {
        @Override
        protected Builder getSelf() {
            return this;
        }



        @Override
        protected PersistenceTimeSeriesHolderConfig newBuildingInstance() {
            return new PersistenceTimeSeriesHolderConfig();
        }
    }
}
