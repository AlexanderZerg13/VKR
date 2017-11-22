package project;

/**
 * Created by pilipenko on 22.11.2017.
 */
public class Executor {

    private int mIntensityTreatFlow;
    private int mIntensityParry;
    private double mParryProbability;

    private double s1;
    private double s2;

    private double A0, B0;
    private double A1, B1;
    private double A2, B2, C2;

    public Executor(int intensityTreatFlow, int intensityParry, double parryProbability) {
        mIntensityTreatFlow = intensityTreatFlow;
        mIntensityParry = intensityParry;
        mParryProbability = parryProbability;
    }

    public void init() {
        double D = mIntensityParry * mIntensityParry + mIntensityTreatFlow * mIntensityTreatFlow + 2 * mIntensityParry * mIntensityTreatFlow * (2 * mParryProbability - 1);
        s1 = -((mIntensityParry + mIntensityTreatFlow + Math.sqrt(D)) / 2.0);
        s2 = -((mIntensityParry + mIntensityTreatFlow - Math.sqrt(D)) / 2.0);

        System.out.printf("D = %f s1 = %f s2 = %f%n", D, s1, s2);

        B0 = (mIntensityParry + s2) / (s2 - s1);
        A0 = 1 - B0;

        System.out.printf("A0 = %f B0 = %f%n", A0, B0);

        B1 = mIntensityTreatFlow / (s2 - s1);
        A1 = -B1;

        System.out.printf("A1 = %f B1 = %f%n", A1, B1);

        A2 = (mIntensityTreatFlow * mIntensityParry * (1 - mParryProbability)) / (s1 * s2);
        C2 = - (s1 * A2) / (s1 - s2);
        B2 = - (A2 + C2);

        System.out.printf("A2 = %f B2 = %f C2 = %f%n", A2, B2, C2);
    }

    public double getProbabilityS0(double t) {
        return A0 * Math.exp(s1 * t) + B0 * Math.exp(s2 * t);
    }

    public double getProbabilityS1(double t) {
        return A1 * Math.exp(s1 * t) + B1 * Math.exp(s2 * t);
    }

    public double getProbabilityS2(double t) {
        return A2 + B2 * Math.exp(s1 * t) + C2 * Math.exp(s2 * t);
    }
}
