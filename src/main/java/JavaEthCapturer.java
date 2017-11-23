package javaethcapturer;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class JavaEthCapturer {

    /*// Will be filled with NICs
    List alldevs = new ArrayList();
    // For any error msgs 
    StringBuilder errbuf = new StringBuilder();
    //Getting a list of devices 
    int r = Pcap.findAllDevs(alldevs, errbuf);
    int adp;
    int nbpct;
    boolean pc = false;
    // массив строк адаптеров  
    String [] nbAdapterStrings = {"0", "1"};
    String [] nbAdpStrings = {"0", "1"};
    int bufrdCount;
    // Capture all packets, no trucation 
    int snaplen = 64 * 1024;
    // capture all packets 
    int flags = Pcap.MODE_PROMISCUOUS;
    // пакеты только на выбранный адаптер  
//    static int flags = Pcap.MODE_NON_PROMISCUOUS;  
    // Timeout mc 
//    static int timeout = 10000;            
    int timeout = 100;
//---------------------------------------------------------------------- 
// создание объектов 

    private JButton stop = new JButton("Stop");
    private JButton start = new JButton("Start");
    private static  JTextArea input = new JTextArea("input");
    private JScrollPane scrollPaneInput = new JScrollPane(input);
    private JLabel label1 = new JLabel("Select an adapter");
    private JLabel label2 = new JLabel("Test1");
    private JLabel label3 = new JLabel("Test2");
    private JComboBox adapterList = new JComboBox();
    static private Pcap pcap = new Pcap();

//---------------------------------------------------------------------- 
// Десктопная форма  

    public class Form extends JFrame{

        String dv = "";

        // конструктор класса Form (должен иметь тоже имя Form) 
        public Form() {

            System.out.println(r);
            System.out.println(r);
            if (r != Pcap.OK) {
                System.err.printf("Can't read list of devices, error is %s", errbuf
                        .toString());
                return;
            }

            System.out.println("Network devices found:");
            int i = 0;
            for (Iterator it = alldevs.iterator(); it.hasNext();) {
                PcapIf device = (PcapIf) it.next();
                String description =
                        (device.getDescription() != null) ? device.getDescription()
                                : "No description available";
                // записать название адаптера в строку 
                nbAdapterStrings[i] = description + "\n";
                nbAdpStrings[i] = description;
                dv = dv + nbAdapterStrings[i];
                // список адаптеров в adapterList 
                adapterList.addItem(nbAdapterStrings[i]);
                i++;
            }
            // список адаптеров в input 
            input.setText(dv);
            // инициализация компонентов 
            initComponents();
        }

        // метод инициализации компонентов формы 
        private void initComponents(){
            // положение на экране 
            setBounds(15,30,800,600);
            // размер формы 
            setSize(830, 600);
            // Закрытие формы  
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // Контейнер для размещения компонентов формы 
            Container container = getContentPane();
            // установить разметку 
            container.setLayout(null);
            container.setBounds(5,5,800,600);

//---------------------------------------------------------------------- 
// JTextArea 

            // Добавление JTextArea input 
            input.setLineWrap(true);
            input.setColumns(20);
            input.setRows(5);
            input.setBounds(10,220,790,300);
            container.add(input);
            // Добавление скрола 
            scrollPaneInput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPaneInput.setBounds(10,220,800,300);
            container.add(scrollPaneInput);
            scrollPaneInput.setViewportView(input);

//---------------------------------------------------------------------- 
// JComboBox 

            adapterList.setBounds(10,25,300,20);
            // зарегистрировать экземпляр класса обработчика события  
            adapterList.addActionListener(new ethListEventListener());
            adapterList.setEnabled(true);
            container.add(adapterList);

//---------------------------------------------------------------------- 
// JLabel 

            // добавление метки состояния COM порта 
            label1.setBounds(10,5,300,20);
            // прозрачный фон 
            label1.setOpaque(true);
            label1.setForeground(Color.red);
            container.add(label1);
            //  
            label2.setBounds(10,190,300,20);
            label2.setText("Lenght of packet");
            container.add(label2);
            // 
            label3.setBounds(20,530,250,20);
            label3.setText("Frame number");
            container.add(label3);

//---------------------------------------------------------------------- 
// JButton 

            // зарегистрировать экземпляр класса обработчика события start 
            start.addActionListener(new startEventListener());
            // добавить кнопку и ее положение 
            start.setBounds(440,155,80,25);
            start.setEnabled(false);
            container.add(start);

            // зарегистрировать экземпляр класса обработчика события stop 
            stop.addActionListener(new stopEventListener());
            stop.setBounds(730,155,80,25);
            stop.setEnabled(false);
            container.add(stop);
        }

        // клас имплементации события нажатия start 
        class startEventListener implements ActionListener {

            @Override
            // обработка события нажатия на button start 
            public void actionPerformed(ActionEvent e) {
                // дезактивировать выбор адаптеров 
                adapterList.setEnabled(false);
                // Флаг открытия pcap 
                pc = true;
                stop.setEnabled(true);
            }
        }

        // клас имплементации события нажатия stop 
        class stopEventListener implements ActionListener {
            @Override
            // обработка события нажатия на button stop 
            public void actionPerformed(ActionEvent e) {
                // Флаг закрытия pcap 
                pc = false;
                System.out.println(adp + " device close");
                label1.setOpaque(true);
                label1.setForeground(Color.red);
                label1.setText("Select an adapter");
                //Close the pcap 
                pcap.close();
                start.setEnabled(false);
                adapterList.setEnabled(true);
                stop.setEnabled(false);
            }
        }

        // обработка события изменения JComboBox comList 
        class ethListEventListener implements ActionListener {

            String op = "Opened";

            @Override
            public void actionPerformed(ActionEvent e) {
                // comName - выбранная строка в JComboBox comList   
                JComboBox cb = (JComboBox)e.getSource();
                // получить номер выбранного адаптера 
                adp = cb.getSelectedIndex();
                // выбор адаптера 
                PcapIf device = (PcapIf) alldevs.get(adp);
                // отрыть выбранный адаптер 
                pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
                if (pcap == null) {
                    input.setText("Error while opening device for capture: "
                            + errbuf.toString());
                }
                // выбранный адаптер в окне input  
                input.setText(nbAdpStrings[adp] + "   " + op);
                // прозрачность label1 с послед. уст. цвета 
                label1.setOpaque(true);
                label1.setForeground(Color.MAGENTA);
                label1.setText(nbAdapterStrings[adp]);
                start.setEnabled(true);
            }
        }
    }

//---------------------------------------------------------------------- 
// Хендлер метод  

    PcapPacketHandler jpacketHandler = new PcapPacketHandler() {

        // строка данных 
        String readData;
        // буфер данных 
        byte[] bufrd = new byte [2000];

        @Override
        public void nextPacket(PcapPacket packet, String user) {
            // данные фрейма data 
            byte[] data = packet.getByteArray(0, packet.size());
            // номер фрейма 
            nbpct = (int) packet.getFrameNumber();
            input.setText("");
            readData = "";
            // количество байт фрейма 
            label2.setText(String.format("Lenght of packet %d bytes", data.length));
            label3.setText(String.format("Frame number %d ", packet.getFrameNumber()));
            // перенос данных фрейма в форматированную строку 
            for (int i = 0; i < data.length; i++){
                bufrd[i] = data[i];
                readData = readData + String.format("%02X ", bufrd[i]);
            }
            // данные фрейма в окно input 
            input.setText(readData);
        }
    };

    // Поток с pcap.loop 
    public class PcapLoopThread extends Thread {

        // переопределение метода run 
        @Override
        public void run() {
            while(true)
            {
                try{
                    //Приостанавливает поток 1мс 
                    sleep(1);
                    if(pc) {
                        // отлов одного пакета если был Start 
                        pcap.loop(1, jpacketHandler, "jnetpcap rocks!");
                    }
                }catch(InterruptedException e){}
            }
        }
    }

    //----------------------------------------------------------------------
// Main     
    public static void main(String[] args) {

        // создание объектов 
        JavaEthCapturer javaEthTest = new JavaEthCapturer();
        JavaEthCapturer.Form form = javaEthTest.new Form();
        JavaEthCapturer.PcapLoopThread pcapLoopThread = javaEthTest.new PcapLoopThread();
        // по зарытию формы 
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Запуск формы 
        form.setVisible(true);
        // Запуск потока 
        pcapLoopThread.start();
    }*/
} 