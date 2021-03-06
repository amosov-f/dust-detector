package ru.spbu.astro.dust.algo.classify;

import ru.spbu.astro.commons.StarFilter;
import ru.spbu.astro.commons.Stars;
import ru.spbu.astro.commons.spect.LuminosityClass;

/**
 * User: amosov-f
 * Date: 28.03.15
 * Time: 18:57
 */
public enum LuminosityClassifiers {
    ;

    public static final LuminosityClassifier SVM = new SVMLuminosityClassifier(StarFilter.of(Stars.ALL).mainLumin().stars());
    public static final LuminosityClassifier LEFT_III = new ConstLuminosityClassifier.Left(LuminosityClass.III);
    public static final LuminosityClassifier LEFT_V = new ConstLuminosityClassifier.Left(LuminosityClass.V);
    public static final LuminosityClassifier COMBINING = new CombiningLuminosityClassifier(SVM);
}
