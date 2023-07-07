package io.oneinvest.bond.track;

public class Main {
    public static void main(String[] args) {
        // XS0971721450
        // RU000A106987
        Isin isin = Isin.of("RU000A105YQ9");
        BondDataAggregator aggregator = new BondDataAggregator();
        BondData bondData = aggregator.fetchAllData(isin);
        System.out.println(bondData.isin());
        System.out.println(bondData.dohodData());
        System.out.println(bondData.blackTerminalData());
        System.out.println(bondData.finPlanData());
    }
}
