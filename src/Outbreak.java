import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    static int[] order(int n, int[][] nb, int[] parent) {
        int[] order = new int[n];
        boolean[] seen = new boolean[n];
        int head = 0, tail = 0;
        order[tail++] = 0;
        seen[0] = true;
        parent[0] = -1;
        while (head < tail) {
            int v = order[head++];
            for (int c : nb[v]) {
                if (!seen[c]) { seen[c] = true; parent[c] = v; order[tail++] = c; }
            }
        }
        return order;
    }

    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        int[] parent = new int[n];
        int[] order = order(n, nb, parent);
        long[] in = new long[n];   // v is tested
        long[] out = new long[n];  // v is not tested, so all children must be tested
        for (int idx = n - 1; idx >= 0; idx--) {
            int v = order[idx];
            in[v] += cost[v];
            int p = parent[v];
            if (p != -1) {
                in[p] += Math.min(in[v], out[v]);
                out[p] += in[v];
            }
        }
        boolean[] tested = new boolean[n];
        tested[0] = in[0] <= out[0];
        for (int idx = 1; idx < n; idx++) {
            int v = order[idx];
            if (!tested[parent[v]]) tested[v] = true;
            else tested[v] = in[v] <= out[v];
        }
        return tested;
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        // TODO Mission 5b
        return -1;
    }
}
