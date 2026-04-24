import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class Main {

    static Random random = new Random();

    public static void main(String[] args) {

        int[] tamanhos = {1000, 5000, 10000};
        String[] tipos = {"Aleatorio", "Ordenado", "Reverso"};
        int[] threadsLista = {1, 2, 4, 8};

        int amostras = 5;

        try {
            PrintWriter writer = new PrintWriter(new FileWriter("resultados.csv"));

            writer.println("Algoritmo,Versao,Threads,Tamanho,Tipo,Amostra,Tempo_ms");

            for (int tamanho : tamanhos) {
                for (String tipo : tipos) {
                    for (int amostra = 1; amostra <= amostras; amostra++) {

                        int[] base = gerarVetor(tamanho, tipo);

                        for (String algoritmo : new String[]{
                                "Bubble", "Selection", "Insertion", "Merge"}) {

                            testar(writer, algoritmo, "Serial", 1, base, amostra);

                            for (int th : threadsLista) {
                                testar(writer, algoritmo, "Paralelo", th, base, amostra);
                            }
                        }
                    }
                }
            }

            writer.close();

            System.out.println("CSV gerado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void testar(PrintWriter writer, String algoritmo, String versao,
                       int threads, int[] base, int amostra) {

        int[] vetor = Arrays.copyOf(base, base.length);

        long inicio = System.nanoTime();

        try {

            if (versao.equals("Serial")) {

                switch (algoritmo) {
                    case "Bubble":
                        bubbleSort(vetor);
                        break;
                    case "Selection":
                        selectionSort(vetor);
                        break;
                    case "Insertion":
                        insertionSort(vetor);
                        break;
                    case "Merge":
                        mergeSort(vetor, 0, vetor.length - 1);
                        break;
                }

            } else {

                paralelo(vetor, algoritmo, threads);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        long fim = System.nanoTime();

        double tempo = (fim - inicio) / 1000000.0;

        writer.println(algoritmo + "," + versao + "," + threads + "," +
                vetor.length + "," + tipoVetor(base) + "," +
                amostra + "," + tempo);

        System.out.println(algoritmo + " | " + versao +
                " | Threads " + threads +
                " | " + tempo + " ms");
    }

    static void paralelo(int[] vetor, String algoritmo, int threads) throws Exception {

        Thread[] lista = new Thread[threads];
        int parte = vetor.length / threads;

        for (int t = 0; t < threads; t++) {

            int inicio = t * parte;
            int fim = (t == threads - 1) ? vetor.length : inicio + parte;

            lista[t] = new Thread(() -> {

                int[] pedaço = Arrays.copyOfRange(vetor, inicio, fim);

                switch (algoritmo) {
                    case "Bubble":
                        bubbleSort(pedaço);
                        break;
                    case "Selection":
                        selectionSort(pedaço);
                        break;
                    case "Insertion":
                        insertionSort(pedaço);
                        break;
                    case "Merge":
                        mergeSort(pedaço, 0, pedaço.length - 1);
                        break;
                }

                for (int i = inicio, j = 0; i < fim; i++, j++) {
                    vetor[i] = pedaço[j];
                }

            });

            lista[t].start();
        }

        for (Thread t : lista)
            t.join();

        Arrays.sort(vetor); // junta final simples
    }

    static int[] gerarVetor(int tamanho, String tipo) {

        int[] v = new int[tamanho];

        if (tipo.equals("Aleatorio")) {
            for (int i = 0; i < tamanho; i++)
                v[i] = random.nextInt(100000);
        }

        if (tipo.equals("Ordenado")) {
            for (int i = 0; i < tamanho; i++)
                v[i] = i;
        }

        if (tipo.equals("Reverso")) {
            for (int i = 0; i < tamanho; i++)
                v[i] = tamanho - i;
        }

        return v;
    }

    static String tipoVetor(int[] v) {

        if (v[0] == 0)
            return "Ordenado";

        if (v[0] == v.length)
            return "Reverso";

        return "Aleatorio";
    }

    static void bubbleSort(int[] v) {
        for (int i = 0; i < v.length - 1; i++)
            for (int j = 0; j < v.length - i - 1; j++)
                if (v[j] > v[j + 1]) {
                    int temp = v[j];
                    v[j] = v[j + 1];
                    v[j + 1] = temp;
                }
    }

    static void selectionSort(int[] v) {
        for (int i = 0; i < v.length - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < v.length; j++)
                if (v[j] < v[menor])
                    menor = j;

            int temp = v[i];
            v[i] = v[menor];
            v[menor] = temp;
        }
    }

    static void insertionSort(int[] v) {
        for (int i = 1; i < v.length; i++) {
            int chave = v[i];
            int j = i - 1;

            while (j >= 0 && v[j] > chave) {
                v[j + 1] = v[j];
                j--;
            }

            v[j + 1] = chave;
        }
    }

    static void mergeSort(int[] v, int ini, int fim) {

        if (ini < fim) {

            int meio = (ini + fim) / 2;

            mergeSort(v, ini, meio);
            mergeSort(v, meio + 1, fim);

            merge(v, ini, meio, fim);
        }
    }

    static void merge(int[] v, int ini, int meio, int fim) {

        int[] temp = new int[fim - ini + 1];

        int i = ini;
        int j = meio + 1;
        int k = 0;

        while (i <= meio && j <= fim)
            temp[k++] = (v[i] < v[j]) ? v[i++] : v[j++];

        while (i <= meio)
            temp[k++] = v[i++];

        while (j <= fim)
            temp[k++] = v[j++];

        for (i = ini, k = 0; i <= fim; i++, k++)
            v[i] = temp[k];
    }
}