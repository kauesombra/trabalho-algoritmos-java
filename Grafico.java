import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class Grafico extends JFrame {

    Map<String, Double> medias = new LinkedHashMap<>();

    public Grafico() {

        setTitle("Comparação Serial x Paralelo");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        carregarCSV();

        add(new Painel());

        setVisible(true);
    }

    void carregarCSV() {

        try {
            BufferedReader br = new BufferedReader(new FileReader("resultados.csv"));

            br.readLine();

            Map<String, ArrayList<Double>> mapa = new LinkedHashMap<>();

            String linha;

            while ((linha = br.readLine()) != null) {

                String[] p = linha.split(",");

                String chave = p[0] + "-" + p[1]; // Bubble-Serial

                double tempo = Double.parseDouble(p[6]);

                mapa.putIfAbsent(chave, new ArrayList<>());
                mapa.get(chave).add(tempo);
            }

            br.close();

            for (String chave : mapa.keySet()) {

                ArrayList<Double> lista = mapa.get(chave);

                double soma = 0;

                for (double v : lista)
                    soma += v;

                medias.put(chave, soma / lista.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Painel extends JPanel {

        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Comparação de Desempenho", 280, 30);

            double max = Collections.max(medias.values());

            String[] algoritmos = {"Bubble", "Selection", "Insertion", "Merge"};

            int y = 80;

            for (String alg : algoritmos) {

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString(alg, 40, y);

                // SERIAL
                desenharBarra(g2, alg + "-Serial", y + 20, max, Color.BLUE);

                // PARALELO
                desenharBarra(g2, alg + "-Paralelo", y + 55, max, Color.GREEN);

                y += 130;
            }
        }

        void desenharBarra(Graphics2D g2, String chave, int y, double max, Color cor) {

            if (!medias.containsKey(chave))
                return;

            double valor = medias.get(chave);

            int largura = (int)((valor / max) * 500);

            g2.setColor(Color.BLACK);

            if (chave.contains("Serial"))
                g2.drawString("Serial", 60, y + 20);
            else
                g2.drawString("Paralelo", 60, y + 20);

            g2.setColor(cor);
            g2.fillRect(150, y, largura, 30);

            g2.setColor(Color.BLACK);
            g2.drawRect(150, y, largura, 30);

            g2.drawString(String.format("%.2f ms", valor), 670, y + 20);
        }
    }

    public static void main(String[] args) {
        new Grafico();
    }
}