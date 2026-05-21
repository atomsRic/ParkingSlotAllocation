import java.util.*;

public class Graph {
    private List<Map<Integer, Double>> adjList;
    private int vertices;
    
    public Graph(int v) {
        vertices = v;
        adjList = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            adjList.add(new HashMap<>());
        }
    }
    
    public void addEdge(int u, int v, double weight) {
        adjList.get(u).put(v, weight);
        adjList.get(v).put(u, weight); // Undirected
    }
    
    // Dijkstra's Algorithm - O((V+E) log V)
    public String dijkstra(int start, int end) {
        double[] dist = new double[vertices];
        int[] prev = new int[vertices];
        PriorityQueue<Node> pq = new PriorityQueue<>(
            (a, b) -> Double.compare(a.distance, b.distance)
        );
        
        Arrays.fill(dist, Double.MAX_VALUE);
        dist[start] = 0;
        pq.offer(new Node(start, 0));
        
        while (!pq.isEmpty()) {
            Node node = pq.poll();
            int u = node.vertex;
            
            if (u == end) break;
            
            for (Map.Entry<Integer, Double> neighbor : adjList.get(u).entrySet()) {
                int v = neighbor.getKey();
                double weight = neighbor.getValue();
                double newDist = dist[u] + weight;
                
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    prev[v] = u;
                    pq.offer(new Node(v, newDist));
                }
            }
        }
        
        return reconstructPath(prev, start, end);
    }
    
    private String reconstructPath(int[] prev, int start, int end) {
        List<Integer> path = new ArrayList<>();
        for (int at = end; at != -1; at = prev[at]) {
            path.add(at);
            if (at == start) break;
        }
        Collections.reverse(path);
        return path.toString().replace("[", "").replace("]", "").replace(", ", " -> ");
    }
    
    private static class Node {
        int vertex;
        double distance;
        Node(int v, double d) { vertex = v; distance = d; }
    }
}
