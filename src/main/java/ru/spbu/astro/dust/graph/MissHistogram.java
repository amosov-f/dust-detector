package ru.spbu.astro.dust.graph;

import com.google.gwt.thirdparty.guava.common.collect.Iterables;
import com.google.gwt.thirdparty.guava.common.primitives.Ints;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;
import ru.spbu.astro.dust.model.Catalogue;
import ru.spbu.astro.dust.model.SpectralType;
import ru.spbu.astro.dust.model.Star;
import ru.spbu.astro.dust.model.Value;
import ru.spbu.astro.dust.util.StarSelector;
import ru.spbu.astro.dust.util.count.Counter;
import ru.spbu.astro.dust.util.count.DoubleCounter;
import ru.spbu.astro.dust.util.count.LuminosityClassCounter;
import ru.spbu.astro.dust.util.count.SpectralClassCounter;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class MissHistogram {

    private final List<Star> missStars;

    public <T extends Comparable<T>> MissHistogram(@NotNull final List<Star> stars, @NotNull final Counter<T> counter) {
        missStars = new StarSelector(stars).selectByNegativeExtinction().getStars();

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        final Map<T, Integer> missCounts = counter.count(missStars);
        final int missCount = IntStream.of(Ints.toArray(missCounts.values())).sum();
        final Map<T, Integer> counts = counter.count(stars);
        final int count = IntStream.of(Ints.toArray(counts.values())).sum();


        for (final T type : counts.keySet()) {
            if (missCounts.get(type) == null) {
                missCounts.put(type, 0);
            }
            dataset.addValue(1.0 * missCounts.get(type) / counts.get(type), "pizza", type);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                String.format(
                        "Распределение звезд с отрицательным покраснением (%d%% или %d/%d)",
                        100 * missCount / count, missCount, count),
                counter.getName(),
                "Доля",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        final StackedBarRenderer renderer = new StackedBarRenderer(false);
        renderer.setBaseItemLabelGenerator(new CategoryItemLabelGenerator() {
            @Nullable
            @Override
            public String generateRowLabel(CategoryDataset categoryDataset, int i) {
                return null;
            }

            @Nullable
            @Override
            public String generateColumnLabel(CategoryDataset categoryDataset, int i) {
                return null;
            }

            @NotNull
            @Override
            public String generateLabel(CategoryDataset categoryDataset, int i, int i2) {
                final T type = Iterables.get(counts.keySet(), i2);
                return missCounts.get(type) + "/" + counts.get(type);
            }
        });
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        renderer.setBaseItemLabelsVisible(true);

        chart.getCategoryPlot().setRenderer(renderer);

        final NumberAxis rangeAxis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#%"));
        rangeAxis.setRange(0.0, 1.0);


        ChartFrame frame = new ChartFrame("Звезды с отрицательным покраснением", chart);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append("hip\tspect\text\tsigma_ext\n");
        for (final Star star : missStars) {
            final Value ext = star.getExtinction();
            str.append(String.format(
                    "%d\t%s\t%.2f\t%.2f\n",
                    star.getId(), star.getSpectralType().toString(), ext.getValue(), star.getExtinction().getError()
            ));
        }
        return str.toString();
    }

    @NotNull
    public List<Integer> getMissIds() {
        final List<Integer> missIds = new ArrayList<>();
        for (final Star s : missStars) {
            missIds.add(s.getId());
        }
        return missIds;
    }

    public static void main(final String[] args) throws FileNotFoundException {

        /*{
            final PrintWriter fout = new PrintWriter(new FileOutputStream("results/6.txt"));

            Locale.setDefault(Locale.US);
            fout.print(missObserver.toString());
            fout.flush();
        }
        {
            final PrintWriter fout = new PrintWriter(new FileOutputStream("results/10.txt"));
            for (int id : missObserver.getMissIds()) {
                fout.println(id);
            }
            fout.flush();
        }*/
    }

}