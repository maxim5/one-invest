package io.oneinvest.bond.track;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class BlackTerminalParsedYieldTest {
    @Test
    public void parseYield_rub() {
        String yield = """
        
            <b>Bond</b>: BUSINESS ALLIANCE 001P-01<br/>
            <b>Face value</b>: RUB 1,000<br/>
            <b>Price</b>: 101.22% of face value =
            RUB 1,012.2<br/>
            <b>Broker commission</b>,
            by default    0.057%:
            RUB 1,012.2 *
            0.057% =
            RUB 0.58    <br/>
        
            <b>Accrued interest</b>: RUB 8.95    <br/>
        
            <b>You will pay</b>:
            RUB 1,012.2 +
            RUB 0.58    +
            RUB 8.95 =
            RUB 1,021.73    for 1 pcs.<br/>
            By maturity date    <b>12.03.2026</b>
            (in 980 days)    you will receive coupons (inclusive of taxes 13%)    RUB 354.28,
            as well as the body of the bond net of tax from the redemption of the bond    RUB 1,000    total:
            RUB 1,354.28    <br/>
            <b>Your profit</b>*
            for all time will be:
            RUB 1,354.28    -
            RUB 1,021.73 =
            RUB 332.55    or    12.12%    per annum.<br/>
            * Provided that the last known coupon remains unchanged<br/>
        """;
        BlackTerminalParsedYield parsedYield = BlackTerminalParsedYield.parseOrNull(yield);
        assertThat(parsedYield).isEqualTo(new BlackTerminalParsedYield(1000.0, 101.22, 0.057, 0.58, 8.95, 1021.73, 1354.28, 12.12));
    }

    @Test
    public void parseYield_usd() {
        String yield = """
        
            <b>Bond</b>: RUS-23<br/>
            <b>Face value</b>: $200,000<br/>
            <b>Price</b>: 99.00% of face value =
            $198,000<br/>
            <b>Broker commission</b>,
            by default    0.057%:
            $198,000 *
            0.057% =
            $112.86    <br/>
        
            <b>Accrued interest</b>: $271,578.61    <br/>
        
            <b>You will pay</b>:
            $198,000 +
            $112.86    +
            $271,578.61 =
            $469,691.47    for 1 pcs.<br/>
            By maturity date    <b>16.09.2023</b>
            (in 72 days)    you will receive coupons (inclusive of taxes 13%)    $4,241.25,
            as well as the body of the bond net of tax from the redemption of the bond    $199,740    total:
            $203,981.25    <br/>
            <b>Your profit</b>*
            for all time will be:
            $203,981.25    -
            $469,691.47 =
            -$265,710.22    or    -286.78%    per annum.<br/>
            * Provided that the last known coupon remains unchanged<br/>
        """;
        BlackTerminalParsedYield parsedYield = BlackTerminalParsedYield.parseOrNull(yield);
        assertThat(parsedYield).isEqualTo(new BlackTerminalParsedYield(200000.0, 99.0, 0.057, 112.86, 271578.61, 469691.47, 203981.25, -286.78));
    }
}
