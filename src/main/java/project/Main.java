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

    private static int TIME_RANGE = 10;
    private static int TIME_DELTA = 1;

    public Main() {
        setContentPane(rootPanel);
        setVisible(true);

        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        init();
    }

    private void init() {
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
                int intensityTreatFlow = Integer.parseInt(intensityTreatFlowString);
                int intensityParry = Integer.parseInt(intensityParryString);
                double parryProbability = Double.parseDouble(parryProbabilityString);

                if (parryProbability < 0.0 || parryProbability > 1.0) {
                    throw new NumberFormatException();
                }

                Executor executor = new Executor(intensityTreatFlow, intensityParry, parryProbability);
                executor.init();

                System.out.printf("%f %f %f %f %n", executor.getProbabilityS2(1), executor.getProbabilityS2(2), executor.getProbabilityS2(4), executor.getProbabilityS2(6));

                //prepareTables(countThreatInt, countStepsInt);

            } catch (NumberFormatException exception) {
                exception.printStackTrace();
            }
        });

        mCountProbabilityButton.addActionListener(e -> {
            double[][] A = new double[][]{{1, 1}};//= ((RowColumnTableModel) table1.getModel()).getValues();

            int rows = mParryTable.getModel().getRowCount();
            int cols = mParryTable.getModel().getColumnCount() - 1;
            double[][] B = new double[rows][cols];

            for (int i = 0; i < rows; i++) {
                if (i == 0) {
                    for (int j = 0; j < cols - 2; j++) {
                        B[i][j] = A[0][j];
                    }
                    B[i][cols - 2] = 1;
                    B[i][cols - 1] = 0;
                } else {
                    for (int j = 0; j < cols - 2; j++) {
                        double ver = 0;
                        if (j < cols - 4) {
                            for (int q = 0; q < cols - 3; q++) {
                                ver += B[i - 1][q] * A[q][j];
                            }
                        } else {
                            for (int q = 0; q < cols - 2; q++) {
                                ver += B[i - 1][q] * A[q][j];
                            }
                        }
                        B[i][j] = ver;
                    }
                    B[i][cols - 2] = 1 - B[i][cols - 3];
                    B[i][cols - 1] = B[i][cols - 3];
                }
            }

            ((RowColumnTableModel) mParryTable.getModel()).setValues(B);
        });

        buildChartsButton.addActionListener(e -> {
            int threatCount = mParryTable.getModel().getRowCount();
            double[][] values = ((RowColumnTableModel) mParryTable.getModel()).getValues();
            double[] probability = new double[threatCount];
            for (int i = 0; i < probability.length; i++) {
                probability[i] = values[i][values[i].length - 2];
            }

            initChart(probability);
        });

    }

    private void prepareTables(int treats, int steps) {

        RowColumnTableModel model1 = new RowColumnTableModel(treats + 2, treats + 2, (column, size) -> "P" + column, (column, size) -> "P" + column);
        if (treats == 3) {
            model1.setValues(
                    new double[][]{
                            {0.7, 0.15, 0.05, 0.1, 0},
                            {0.7, 0, 0.04, 0.06, 0.2},
                            {0.6, 0.05, 0, 0.1, 0.25},
                            {0.65, 0.03, 0.02, 0, 0.3},
                            {0, 0, 0, 0, 1}}
            );
        }
/*
        table1.setModel(model1);
        table1.updateUI();*/

        RowColumnTableModel model2 = new RowColumnTableModel(steps, treats + 4,
                (column, size) -> {
                    if (column == size) {
                        return "Qби(i)";
                    }
                    if (column == size - 1) {
                        return "Pби(i)";
                    }
                    return "P" + column + "(i)";
                },
                (column, size) -> "i=" + (column + 1));
        mParryTable.setModel(model2);
        mParryTable.updateUI();
    }

    private void initChart(double[] propability) {
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                "Результат моделирования",
                "K, кол-во шагов",
                "Pби",
                createDataSet(propability),
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel chartPanel = new ChartPanel(xylineChart);

        mChartPanel.removeAll();
        mChartPanel.add(chartPanel, BorderLayout.CENTER);
        rootPanel.validate();
    }

    private XYDataset createDataSet(double[] propability) {
        final XYSeries series1 = new XYSeries("");
        for (int i = 0; i < propability.length; i++) {
            series1.add(i + 1, propability[i]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);

        return dataset;
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
        rootPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 5, new Insets(10, 10, 10, 10), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Интенсивность потока угроз (λ):");
        rootPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mIntensityTreatFlowTextField = new JTextField();
        mIntensityTreatFlowTextField.setText("");
        rootPanel.add(mIntensityTreatFlowTextField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 24), null, 0, false));
        mEnterButton = new JButton();
        mEnterButton.setText("Ввод");
        rootPanel.add(mEnterButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mCountProbabilityButton = new JButton();
        mCountProbabilityButton.setText("Рассчитать вероятности");
        rootPanel.add(mCountProbabilityButton, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        rootPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buildChartsButton = new JButton();
        buildChartsButton.setText("Построить график зависимости");
        rootPanel.add(buildChartsButton, new com.intellij.uiDesigner.core.GridConstraints(5, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("График зависимости вероятности благополочного исхода Рби от количества шагов моделирования");
        rootPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mChartPanel = new JPanel();
        mChartPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.add(mChartPanel, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, new Dimension(-1, 200), null, new Dimension(-1, 400), 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Интенсивность парирования (μ):");
        rootPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mIntensityParryTextField = new JTextField();
        rootPanel.add(mIntensityParryTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 24), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Вероятность парирования (R):");
        rootPanel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mParryProbabilityTextField = new JTextField();
        rootPanel.add(mParryProbabilityTextField, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mParryScrollPane = new JScrollPane();
        rootPanel.add(mParryScrollPane, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
