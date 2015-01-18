package ru.spbu.astro.dust.algo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ru.spbu.astro.dust.DustCatalogues;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DustTrendCalculatorRegressionTest {
    public static void main(@NotNull String[] args) throws IOException {
        FileUtils.writeStringToFile(
                new File("dust/src/test/resources/dust-trend-calculator.txt"),
                new DustTrendCalculator(DustCatalogues.HIPPARCOS_UPDATED.getStars()).toString()
        );
    }

    @Test
    public void regression() throws Exception {
        assertEquals(
                IOUtils.toString(getClass().getResourceAsStream("/dust-trend-calculator.txt")),
                new DustTrendCalculator(DustCatalogues.HIPPARCOS_UPDATED.getStars()).toString()
        );
    }
}