package project;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Formatter;

/**
 * Created by pilipenko on 27.10.2017.
 */
public class Main extends JFrame {
    private JPanel rootPanel;
    private JTextField mIntensityTreatFlowTextField;
    private JTextField mIntensityParryTextField;
    private JButton mEnterButton;
    private JButton mCountProbabilityButton;
    private JTable mParryTable;
    private JButton buildChartsButton;
    private JPanel mChartPanel;
    private JScrollPane mParryScrollPane;
    private JTextField mParryProbabilityTextField;
    private JButton mClearButton;

    private static int TIME_RANGE = 50;
    private static int TIME_DELTA = 1;

    private XYSeriesCollection mXySeriesCollection;

    public Main() {
        setContentPane(rootPanel);
        setVisible(true);

        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        init();
    }

    private void init() {
        mXySeriesCollection = new XYSeriesCollection();

        createTables();
        initButtons();
        //initChart();
    }

    private void createTables() {
        mParryTable = new JTable() {
            @Override
            public Component prepareRenderer(
                    TableCellRenderer renderer, int row, int col) {
                if (col == 0) {
                    return this.getTableHeader().getDefaultRenderer()
                            .getTableCellRendererComponent(this, this.getValueAt(
                                    row, col), false, false, row, col);
                } else {
                    return super.prepareRenderer(renderer, row, col);
                }
            }
        };

        final JTableHeader header1 = mParryTable.getTableHeader();
        header1.setDefaultRenderer(new HeaderRenderer(mParryTable));
        mParryScrollPane.getViewport().add(mParryTable);
    }

    private void initButtons() {
        mEnterButton.addActionListener(e -> {
            String intensityTreatFlowString = mIntensityTreatFlowTextField.getText();
            String intensityParryString = mIntensityParryTextField.getText();
            String parryProbabilityString = mParryProbabilityTextField.getText();

            try {
                double intensityTreatFlow = Double.parseDouble(intensityTreatFlowString);
                double intensityParry = Double.parseDouble(intensityParryString);
                double parryProbability = Double.parseDouble(parryProbabilityString);

                if (parryProbability < 0.0 || parryProbability > 1.0) {
                    throw new NumberFormatException();
                }

                Executor executor = new Executor(intensityTreatFlow, intensityParry, parryProbability);
                executor.init();

                System.out.printf("%f %f %f %f %n", executor.getProbabilityS2(1), executor.getProbabilityS2(2), executor.getProbabilityS2(4), executor.getProbabilityS2(6));

                double[][] B = prepareTables(executor);

                double[] probability = new double[B.length];
                for (int i = 0; i < B.length; i++) {
                    probability[i] = B[i][3];
                }

                addSeries(probability, intensityTreatFlow, intensityParry, parryProbability);
                initChart();
            } catch (NumberFormatException exception) {
                exception.printStackTrace();
            }
        });

        mClearButton.addActionListener(e -> {
            clearSeriesCollection();
        });
    }

    private double[][] prepareTables(Executor executor) {
        int rows = TIME_RANGE / TIME_DELTA;
        int columns = 5;

        double[][] B = new double[rows][columns];
        for (int i = 0; i < rows; i += TIME_DELTA) {
            B[i][0] = executor.getProbabilityS0(i);
            B[i][1] = executor.getProbabilityS1(i);
            B[i][2] = executor.getProbabilityS2(i);

            //System.out.printf("S0: %f =? %f %n", executor.getProbabilityS0(i), executor.getProbability2S0(i));
            //System.out.printf("S1: %f =? %f %n", executor.getProbabilityS1(i), executor.getProbability2S1(i));
            //System.out.printf("S2: %f =? %f %n", executor.getProbabilityS2(i), executor.getProbability2S2(i));

            B[i][3] = /*B[i][0];*/ +B[i][2];
            B[i][4] = B[i][2];
        }

        RowColumnTableModel model2 = new RowColumnTableModel(rows, columns,
                (column, size) -> {
                    if (column == size) {
                        return "Qби(t)";
                    }
                    if (column == size - 1) {
                        return "Pби(t)";
                    }
                    return "P" + column + "(t)";
                },
                (column, size) -> "t=" + (column + 1));
        mParryTable.setModel(model2);
        mParryTable.updateUI();

        ((RowColumnTableModel) mParryTable.getModel()).setValues(B);

        return B;
    }

    private void initChart() {
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                "Результат моделирования",
                "t",
                "Pби(t)",
                mXySeriesCollection,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel chartPanel = new ChartPanel(xylineChart);

        mChartPanel.removeAll();
        mChartPanel.add(chartPanel, BorderLayout.CENTER);
        rootPanel.validate();
    }

    private void addSeries(double[] propability, double intensityTreatFlow, double intensityParry, double parryProbability) {
        final XYSeries series1 = new XYSeries(new Formatter().format("λ = %f; μ = %f; R = %f", intensityTreatFlow, intensityParry, parryProbability).toString());
        for (int i = 0; i < propability.length; i++) {
            series1.add(i + 1, propability[i]);
        }

        mXySeriesCollection.addSeries(series1);
    }

    private void clearSeriesCollection() {
        mXySeriesCollection = new XYSeriesCollection();
        initChart();
    }

    public static void main(String[] args) {
        new Main();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 4, new Insets(10, 10, 10, 10), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Интенсивность потока угроз (λ):");
        rootPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mIntensityTreatFlowTextField = new JTextField();
        mIntensityTreatFlowTextField.setText("");
        rootPanel.add(mIntensityTreatFlowTextField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 24), null, 0, false));
        mEnterButton = new JButton();
        mEnterButton.setText("Ввод");
        rootPanel.add(mEnterButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        rootPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        mChartPanel = new JPanel();
        mChartPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.add(mChartPanel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, new Dimension(-1, 200), null, new Dimension(-1, 400), 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Интенсивность парирования (μ):");
        rootPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mIntensityParryTextField = new JTextField();
        rootPanel.add(mIntensityParryTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 24), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Вероятность парирования (R):");
        rootPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mParryProbabilityTextField = new JTextField();
        rootPanel.add(mParryProbabilityTextField, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mParryScrollPane = new JScrollPane();
        rootPanel.add(mParryScrollPane, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mClearButton = new JButton();
        mClearButton.setText("Очистить");
        rootPanel.add(mClearButton, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
