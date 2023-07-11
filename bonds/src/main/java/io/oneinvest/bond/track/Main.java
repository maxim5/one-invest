package io.oneinvest.bond.track;

import java.util.List;

public class Main {
    // More:
    // https://www.moex.com/ru/issue.aspx?board=TQCB&code=RU000A105YQ9
    // https://fapvdo.ru/news-ru000a105yq9/
    // https://cbonds.ru/bonds/1405840/

    public static void main(String[] args) {
//        fetchOne();
//        analyze();
        analyzePortfolio();
    }

    private static void analyzePortfolio() {
        BondAnalyzer analyzer = new BondAnalyzer();
        List<Position> positions = IsinSource.fromPosTxt();
        analyzer.analyzePortfolio(positions);
    }

    private static void analyze() {
        BondAnalyzer analyzer = new BondAnalyzer();
        List<Isin> isins = IsinSource.fromIsinTxt();
        analyzer.analyze(isins);
    }

    private static void fetchOne() {
        // XS0971721450
        // RU000A106987
        Isin isin = Isin.of("RU000A105YQ9");
        BondDataAggregator aggregator = new BondDataAggregator();
        BondData bondData = aggregator.fetchAllData(isin);
        System.out.println(bondData.isin());
        System.out.println(bondData.dohodData());
        System.out.println(bondData.blackTerminalData());
        System.out.println(bondData.finPlanData());
        System.out.println(bondData.moexData());
        System.out.println(bondData.smartlabData());
    }
}
