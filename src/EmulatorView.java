/**
 * Created by user on 24.03.14.
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

public class EmulatorView extends JFrame implements Observer {
    private static final int COLS_NUMBER = 2;
    private static final String WINDOW_TITLE = "Virtual Memory Emulator";
    private static final String LABEL_TEXT_PATTERN = "<html><font color='red'>%d</font></html>";

    private VirtualMemoryBlock[] virtMemory;
    private CommandProcessor commandProcessor;
    private JTextPane blockInfo;
    private JPanel buttonsPanel;
    private JLabel[] buttonLabels;
    private JLabel currCommandLabel;
    private JButton stepButton;
    private JButton runButton;
    private JSpinner stepPauseSpinner;

    private void buildInterface(MemoryUnit memUnit) {
    }

    private String getBlockInfo(VirtualMemoryBlock block) {
        final String pattern =  "Physical Memory Address: %d\n" +
                                "Reads: %d\n" +
                                "Writes: %d\n" +
                                "Last Access: %d\n" +
                                "In memory time: %d\n" +
                                "Usage: %32s\n";

        String usage = Integer.toBinaryString(block.getUsage());
        String zeros32 = "00000000000000000000000000000000";
        String usageStringForm = (zeros32 + usage).substring(usage.length());

        String info = String.format(pattern,
                                    block.getPhysMemoryBlock(),
                                    block.getReadCounter(),
                                    block.getWriteCounter(),
                                    block.getLastAccessTime(),
                                    block.getInMemoryTime(),
                                    usageStringForm);

        return info;
    }

    private void onPageButtonPressed(int index) {
        blockInfo.setText(getBlockInfo(virtMemory[index]));
    }

    private void nextStep() {
        if (commandProcessor.hasNextCommand()) {
            commandProcessor.proceedNextCommand();

            if (commandProcessor.hasNextCommand()) {
                Command command = commandProcessor.getNextCommand();
                currCommandLabel.setText(String.format("%s %d", command.command, command.address));
            }
            else {
                currCommandLabel.setText("There is no commands more.");
                stepButton.setEnabled(false);
            }
        }
    }

    private void runAllCommands() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                stepButton.setEnabled(false);
                runButton.setEnabled(false);

                final int pause = (int)stepPauseSpinner.getValue();

                while (commandProcessor.hasNextCommand()) {
                    nextStep();

                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    private JPanel buildControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));

        controlPanel.add(Box.createHorizontalStrut(50));

        currCommandLabel = new JLabel();
        if (commandProcessor.hasNextCommand()) {
            Command command = commandProcessor.getNextCommand();
            currCommandLabel.setText(String.format("%s %d", command.command, command.address));
        }
        else {
            currCommandLabel.setText("There is no commands more.");
            stepButton.setEnabled(false);
        }
        controlPanel.add(currCommandLabel);

        stepButton = new JButton("Next Step");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextStep();
            }
        });
        controlPanel.add(stepButton);

        runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runAllCommands();
            }
        });
        controlPanel.add(runButton);

        controlPanel.add(Box.createHorizontalStrut(100));

        controlPanel.add(new JLabel("Step Pause (ms):"));
        controlPanel.add(stepPauseSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 2000, 100)));

        return controlPanel;
    }

    public EmulatorView(CommandProcessor commandProcessor) {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.commandProcessor = commandProcessor;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(0, COLS_NUMBER * 2));

        subPanel.add(buttonsPanel);
        subPanel.add(blockInfo = new JTextPane());

        mainPanel.add(buildControlPanel());
        mainPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        mainPanel.add(subPanel);

        add(mainPanel);

        pack();
    }

    public void setMemoryUnit(MemoryUnit memUnit) {
        buildInterface(memUnit);
        memUnit.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        VirtualMemoryBlock[] virtMemory = (VirtualMemoryBlock[])arg;

        final int ROWS = (int)Math.ceil((double)virtMemory.length / COLS_NUMBER);

        if (virtMemory != this.virtMemory) {
            this.virtMemory = virtMemory;

            buttonLabels = new JLabel[virtMemory.length];
            buttonsPanel.removeAll();
            blockInfo.setText("");

            for (int i = 0; i < virtMemory.length; ++i) {
                final int index = i / COLS_NUMBER + (i % COLS_NUMBER) * ROWS;

                String label = virtMemory[index].isMapped() ?
                               String.format(LABEL_TEXT_PATTERN, virtMemory[index].getPhysMemoryBlock()) : "";

                buttonLabels[i] = new JLabel(label);
                buttonLabels[i].setHorizontalAlignment(JLabel.CENTER);

                JButton button = new JButton(String.format("Page %d", index));
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onPageButtonPressed(index);
                    }
                });

                buttonsPanel.add(button);
                buttonsPanel.add(buttonLabels[i]);
            }
        }

        for (int i = 0; i < virtMemory.length; ++i) {
            final int index = i / COLS_NUMBER + (i % COLS_NUMBER) * ROWS;

            String label = virtMemory[index].isMapped() ?
                           String.format(LABEL_TEXT_PATTERN, virtMemory[index].getPhysMemoryBlock()) : "";

            buttonLabels[i].setText(label);
        }

        pack();
    }
}
