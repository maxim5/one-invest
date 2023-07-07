package io.oneinvest.bond.track;

public class Main {
    // More:
    // https://www.moex.com/ru/issue.aspx?board=TQCB&code=RU000A105YQ9
    // https://fapvdo.ru/news-ru000a105yq9/
    // https://cbonds.ru/bonds/1405840/

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
        System.out.println(bondData.smartlabData());
    }
}
