package com.eschava.ht2000.gui;

import com.eschava.ht2000.usb.HT2000State;
import com.eschava.ht2000.usb.HT2000UsbConnection;
import com.eschava.ht2000.usb.UsbException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class HT2000Window extends JDialog implements ActionListener {
    private static final DateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private JPanel contentPane;
    private JButton buttonClose;
    private JButton connectButton;
    private JButton disconnectButton;
    private JTextField dateTimeField;
    private JTextField temperatureField;
    private JTextField humidityField;
    private JTextField co2Field;

    private HT2000UsbConnection connection;
    private Timer timer;

    public HT2000Window() {
        super((Dialog) null);
        setTitle("HT-2000");
        setContentPane(contentPane);
        setModal(true);

        buttonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClose();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onConnect();
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDisconnect();
            }
        });
    }

    public static void main(String[] args) {
        HT2000Window dialog = new HT2000Window();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        System.exit(0);
    }

    private void onConnect() {
        try {
            if (connection == null)
                connection = new HT2000UsbConnection();
            connection.open();

            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);

            read();

            timer = new Timer(5000, this);
            timer.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actionPerformed(ActionEvent e) {
        read();
    }

    private void read() {
        try {
            HT2000State state = connection.readState();
            dateTimeField.setText(DATE_TIME_FORMATTER.format(state.getTime()));
            temperatureField.setText(String.valueOf(state.getTemperature()));
            humidityField.setText(String.valueOf(state.getHumidity()));
            co2Field.setText(String.valueOf(state.getCo2()));
        } catch (UsbException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDisconnect() {
        connection.close();
        timer.stop();

        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
    }

    private void onClose() {
// add your code here if necessary
        dispose();

        if (connection != null) {
            timer.stop();
            connection.close();
            HT2000UsbConnection.shutdown();
        }
    }
}
