import java.util.*;

/**
 * Soluções corrigidas para os 5 exercícios de Lista de Prioridade (PriorityQueue) em Java.
 */
public class ListaPrioridadeExercicios {

    // ==========================================================================
    // EXERCÍCIO 1: Encontrar os K Maiores Elementos (O(N log K))
    // ==========================================================================

    /**
     * Usa uma Min-Heap de tamanho K. A raiz (menor elemento) é removida se o
     * novo elemento for maior e a Heap estiver cheia.
     */
    public static List<Integer> findKthLargest(int[] arr, int k) {
        if (arr == null || k <= 0 || arr.length == 0) {
            return Collections.emptyList();
        }

        // Min-Heap padrão. O menor elemento está no topo.
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

        for (int num : arr) {
            // 1. Adiciona o elemento.
            minHeap.offer(num);

            // 2. Se o tamanho for maior que K, remove o menor (a raiz).
            // Assim, a Heap sempre mantém os K maiores.
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return new ArrayList<>(minHeap);
    }

    // ==========================================================================
    // EXERCÍCIO 2: Mesclagem de K Listas Ordenadas (O(N log K))
    // ==========================================================================

    /**
     * Classe auxiliar para rastrear o elemento, de qual lista veio, e seu índice naquela lista.
     */
    private static class Node implements Comparable<Node> {
        final int value;
        final int listIndex;
        final int elementIndex;

        public Node(int value, int listIndex, int elementIndex) {
            this.value = value;
            this.listIndex = listIndex;
            this.elementIndex = elementIndex;
        }

        // Min-Heap será baseada no menor 'value'.
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.value, other.value);
        }
    }

    /**
     * Usa uma Min-Heap de Nodes para sempre pegar o menor elemento global entre
     * as cabeças de todas as listas.
     */
    public static List<Integer> mergeKSortedLists(List<List<Integer>> lists) {
        PriorityQueue<Node> minHeap = new PriorityQueue<>();
        List<Integer> result = new ArrayList<>();

        // Inicializa a Heap com o primeiro elemento de cada lista não vazia.
        for (int i = 0; i < lists.size(); i++) {
            if (!lists.get(i).isEmpty()) {
                minHeap.offer(new Node(lists.get(i).get(0), i, 0));
            }
        }

        while (!minHeap.isEmpty()) {
            Node current = minHeap.poll();
            result.add(current.value);

            int nextElementIndex = current.elementIndex + 1;
            List<Integer> currentList = lists.get(current.listIndex);

            // Se a lista de origem tiver mais elementos, adiciona o próximo na Heap.
            if (nextElementIndex < currentList.size()) {
                minHeap.offer(new Node(
                        currentList.get(nextElementIndex),
                        current.listIndex,
                        nextElementIndex
                ));
            }
        }

        return result;
    }

    // ==========================================================================
    // EXERCÍCIO 3: Implementar uma Lista de Prioridade Dupla (O(log N))
    // ==========================================================================

    /**
     * Simula um Heap duplo usando Max-Heap, Min-Heap e um mapa de frequência para
     * lidar com elementos que foram "removidos" (marcados para remoção) pela Heap oposta.
     */
    public static class DualPriorityQueue {
        // Max-Heap: para retornar/remover o maior valor.
        private final PriorityQueue<Integer> maxHeap;
        // Min-Heap: para retornar/remover o menor valor.
        private final PriorityQueue<Integer> minHeap;
        // Rastreia elementos removidos (chave: valor, valor: contagem).
        private final Map<Integer, Integer> removedElements;

        public DualPriorityQueue() {
            maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
            minHeap = new PriorityQueue<>();
            removedElements = new HashMap<>();
        }

        public void insert(int value) {
            maxHeap.offer(value);
            minHeap.offer(value);
        }

        public Integer getMax() {
            cleanHeap(maxHeap, removedElements);
            return maxHeap.peek();
        }

        public Integer getMin() {
            cleanHeap(minHeap, removedElements);
            return minHeap.peek();
        }

        public Integer removeMax() {
            cleanHeap(maxHeap, removedElements);
            Integer max = maxHeap.poll();
            if (max != null) {
                // Marca o elemento removido, incrementando a contagem no mapa.
                removedElements.put(max, removedElements.getOrDefault(max, 0) + 1);
            }
            return max;
        }

        public Integer removeMin() {
            cleanHeap(minHeap, removedElements);
            Integer min = minHeap.poll();
            if (min != null) {
                // Marca o elemento removido, incrementando a contagem no mapa.
                removedElements.put(min, removedElements.getOrDefault(min, 0) + 1);
            }
            return min;
        }

        /**
         * Remove elementos da raiz da Heap que já foram removidos/marcados pela Heap oposta.
         */
        private void cleanHeap(PriorityQueue<Integer> heap, Map<Integer, Integer> removedMap) {
            while (!heap.isEmpty()) {
                Integer root = heap.peek();
                int count = removedMap.getOrDefault(root, 0);

                if (count > 0) {
                    heap.poll();
                    removedMap.put(root, count - 1); // Decrementa a contagem de remoção.
                } else {
                    break; // O elemento no topo é válido.
                }
            }
        }
    }

    // ==========================================================================
    // EXERCÍCIO 4: Verificação de Propriedade de Min-Heap (O(N))
    // ==========================================================================

    /**
     * Verifica se um array satisfaz a propriedade de Min-Heap: Pai <= Filho.
     * A verificação só precisa ocorrer nos nós não-folha.
     */
    public static boolean isMinHeap(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return true;
        }

        int n = arr.length;
        // O último nó não-folha (pai) é encontrado em floor(n/2) - 1.
        int lastNonLeafIndex = (n / 2) - 1;

        for (int i = 0; i <= lastNonLeafIndex; i++) {
            int parentValue = arr[i];
            int leftChildIndex = 2 * i + 1;
            int rightChildIndex = 2 * i + 2;

            // Verifica o filho esquerdo (deve sempre existir se i <= lastNonLeafIndex).
            if (parentValue > arr[leftChildIndex]) {
                return false; // Violação: Pai é maior que o filho esquerdo.
            }

            // Verifica o filho direito, se ele existir.
            if (rightChildIndex < n) {
                if (parentValue > arr[rightChildIndex]) {
                    return false; // Violação: Pai é maior que o filho direito.
                }
            }
        }

        return true;
    }

    // ==========================================================================
    // EXERCÍCIO 5: Encontrar a Mediana em um Fluxo de Dados
    // ==========================================================================

    /**
     * Usa um Max-Heap (metade inferior) e um Min-Heap (metade superior) para
     * manter a mediana em tempo O(log N) por adição e O(1) para busca.
     *
     */
    public static class MedianFinder {
        // Max-Heap: Guarda a metade inferior (o maior da inferior está no topo).
        private final PriorityQueue<Integer> lowerHalf;
        // Min-Heap: Guarda a metade superior (o menor da superior está no topo).
        private final PriorityQueue<Integer> upperHalf;

        public MedianFinder() {
            lowerHalf = new PriorityQueue<>(Comparator.reverseOrder());
            upperHalf = new PriorityQueue<>();
        }

        /**
         * Adiciona um novo inteiro ao fluxo. O(log N).
         */
        public void addNum(int num) {
            // 1. Distribuição inicial.
            if (lowerHalf.isEmpty() || num <= lowerHalf.peek()) {
                lowerHalf.offer(num);
            } else {
                upperHalf.offer(num);
            }

            // 2. Rebalanceamento: Garante que a diferença de tamanho seja <= 1,
            // e que lowerHalf seja a maior (ou igual) em tamanho.

            // Caso 1: lowerHalf ficou muito maior (size > upperHalf.size + 1).
            if (lowerHalf.size() > upperHalf.size() + 1) {
                // Move o maior elemento de lowerHalf para upperHalf.
                upperHalf.offer(lowerHalf.poll());
            }
            // Caso 2: upperHalf ficou maior que lowerHalf.
            else if (upperHalf.size() > lowerHalf.size()) {
                // Move o menor elemento de upperHalf para lowerHalf.
                lowerHalf.offer(upperHalf.poll());
            }
        }

        /**
         * Retorna a mediana atual. O(1).
         */
        public double findMedian() {
            if (lowerHalf.isEmpty()) {
                return 0.0;
            }

            // Ímpar: A mediana é o topo da Heap maior (lowerHalf).
            if (lowerHalf.size() > upperHalf.size()) {
                return (double) lowerHalf.peek();
            }
            // Par: A mediana é a média dos topos das duas Heaps.
            else {
                return (lowerHalf.peek() + upperHalf.peek()) / 2.0;
            }
        }
    }

    // ==========================================================================
    // MÉTODO MAIN PARA TESTES RÁPIDOS
    // ==========================================================================

    public static void main(String[] args) {
        // Testes do Exercício 1
        System.out.println("--- Exercício 1: K Maiores Elementos ---");
        int[] arr1 = {3, 2, 1, 5, 6, 4};
        int k1 = 2;
        System.out.println("Array: " + Arrays.toString(arr1) + ", K=" + k1 + " -> " + findKthLargest(arr1, k1));

        // Testes do Exercício 2
        System.out.println("\n--- Exercício 2: Mesclagem de K Listas Ordenadas ---");
        List<List<Integer>> lists2 = Arrays.asList(
                Arrays.asList(1, 4, 5),
                Arrays.asList(1, 3, 4),
                Arrays.asList(2, 6)
        );
        System.out.println("Resultado da mesclagem: " + mergeKSortedLists(lists2));

        // Testes do Exercício 3
        System.out.println("\n--- Exercício 3: Lista de Prioridade Dupla ---");
        DualPriorityQueue dpq = new DualPriorityQueue();
        dpq.insert(5); dpq.insert(1); dpq.insert(8); dpq.insert(2);
        System.out.println("Max (8): " + dpq.getMax() + ", Min (1): " + dpq.getMin());
        System.out.println("Remove Max (8): " + dpq.removeMax());
        System.out.println("Remove Min (1): " + dpq.removeMin());
        System.out.println("Novo Max (5): " + dpq.getMax() + ", Novo Min (2): " + dpq.getMin());
        dpq.insert(10);
        dpq.removeMax(); // Remove 10 (marca 10)
        dpq.removeMin(); // Remove 2 (marca 2)
        System.out.println("Depois de mais inserções/remoções. Max: " + dpq.getMax() + ", Min: " + dpq.getMin()); // Max deve ser 5, Min deve ser null (se estiver vazio ou 5, se tiver apenas ele)

        // Testes do Exercício 4
        System.out.println("\n--- Exercício 4: Verificação de Min-Heap ---");
        int[] arr4_min = {1, 3, 6, 5, 9, 8};
        int[] arr4_not = {10, 5, 8, 3, 1};
        System.out.println("Min-Heap {1, 3, 6, 5, 9, 8}? " + isMinHeap(arr4_min)); // true
        System.out.println("Min-Heap {10, 5, 8, 3, 1}? " + isMinHeap(arr4_not)); // false (10 > 5 e 10 > 8)

        // Testes do Exercício 5
        System.out.println("\n--- Exercício 5: Mediana em um Fluxo de Dados ---");
        MedianFinder mf = new MedianFinder();
        mf.addNum(1); // [1]
        System.out.println("Mediana (1.0): " + mf.findMedian());
        mf.addNum(2); // [1, 2]
        System.out.println("Mediana (1.5): " + mf.findMedian());
        mf.addNum(3); // [1, 2, 3]
        System.out.println("Mediana (2.0): " + mf.findMedian());
        mf.addNum(4); // [1, 2, 3, 4]
        System.out.println("Mediana (2.5): " + mf.findMedian());
    }
}