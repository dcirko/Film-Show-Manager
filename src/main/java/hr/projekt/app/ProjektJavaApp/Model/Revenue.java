package hr.projekt.app.ProjektJavaApp.Model;

import java.math.BigDecimal;

public record Revenue(BigDecimal moneyRevenue) {
    @Override
    public String toString() {
        return "" + moneyRevenue;
    }
}
