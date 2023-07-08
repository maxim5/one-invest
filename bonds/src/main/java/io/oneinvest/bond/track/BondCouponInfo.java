package io.oneinvest.bond.track;

public interface BondCouponInfo {
    double notional();

    double couponAbs();

    double couponYield();

    double couponAbsAnnual();
}
