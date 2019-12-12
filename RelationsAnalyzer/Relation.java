package RelationsAnalyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * <h1>RelationsAnalyzer.Relation</h1>
 *
 * @author Hussain Al-Bin Hajji
 */
public class Relation {
    private final String REFLIXIVE = "Reflexive";
    private final String IRREFLIXIVE = "Irreflexive";
    private final String SYMMETRIC = "Symmetric";
    private final String ANTISYMMETRIC = "Antisymmetric";
    private final String ASYMMETRIC = "Asymmetric";
    private final String NONE = "NONE";
    private boolean isReflexive;

    int size;
    String list[];
    int matrix[][];

    String reflexivity;
    String symmetry;
    boolean isTransitive;
    boolean isEquivalence;
    boolean isPartialOrder;

    String equivalenceClasses;
    String hasseDiagram;
    ArrayList<String> maximals, minimals;
    String greatest, least;


    public Relation(String path)
    {
        read(path);
        checkReflexivity();
        checkSymmetry();
        checkTransitivity();
        checkEquivalence();
        checkPartialOrder();
    }


    private void read(String path)
    {
        Scanner in = null;
        try
        {
            in = new Scanner(new FileInputStream(path));
        } catch (FileNotFoundException e)
        {
            System.out.println("File Not Found, Exiting.");
        }

        this.size = in.nextInt();
        list = new String[size];
        matrix = new int[size][size];

        for (int i = 0; i < size; i++)
        {
            this.list[i] = in.next();
        }
        in.nextLine();

        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                this.matrix[i][j] = in.nextInt();
            }
        }
    }


    /* 1`s diagonal -> reflexive
    *  0`s diagonal -> irr-reflexive
    *  else -> none
    * */
    private void checkReflexivity()
    {
        isReflexive = true;
        boolean ref = false, irr = false;
        for (int i = 0; i < size; i++)
        {
            if (matrix[i][i] == 0)
                isReflexive = false;

            if (matrix[i][i] == 1)
                ref = true;
            else
                irr = true;
            if (ref & irr)
                break;
        }

        if (ref && !irr)
            reflexivity = REFLIXIVE;
        if (irr && !ref)
            reflexivity = IRREFLIXIVE;
        if (ref && irr)
            reflexivity = NONE;
    }


    /*Compares (i, j),(j, i)
     * if they are equal for ALL -> symmetric,
     * if they are NOT equal for ALL -> antisymmetric
     * if antisymmetric & irrreflexive: asymmetric
     * else none
     * */
    private void checkSymmetry()
    {
        boolean anti = false;
        boolean symm = false;

        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                if (((i != j) && matrix[i][j] == matrix[j][i]))
                {
                    if (matrix[i][j] == 0)
                        continue;
                    symm = true;
                }
                else if (((i != j) && matrix[i][j] != matrix[j][i]))
                    anti = true;
                if (symm && anti)
                    break;
            }
            if (symm && anti)
                break;
        }

        if (symm && !anti)
            symmetry = SYMMETRIC;
        if (anti && !symm)
            symmetry = ANTISYMMETRIC;
        if (reflexivity.equalsIgnoreCase(IRREFLIXIVE) && anti)
            symmetry = ASYMMETRIC;
        if (symm && anti)
            symmetry = NONE;
    }


    /*Squares the matrix and compares it with the original
     * if in place of a 0 in the original,
     * the squared has a 1 -> it is not transitive
     * Proof:
     *   a(i, j) belongs to {0, 1}. the (i, j) element of the squared matrix is:
     *       sum at k (a(i, k) * a(k, j)) which is non-zero iff a(i, k) * a(k, j) == 1
     *       for some k, i.e., iff (i, k) and (k, j) are both in the relation.
     *                   - Harald at math stack exchange.
     * */
    private void checkTransitivity()
    {
        isTransitive = true;

        int[][] squared = squaredMatrix();

        //if m[i][j] == 0 go check s[i][j]
        //if s[i][j] == 1 -> not trans.
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (matrix[i][j] == 0 && squared[i][j] == 1)
                    isTransitive = false;
    }


    // Helper for checkTransitivity();
    private int[][] squaredMatrix()
    {
        int[][] squared = new int[size][size];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
            {
                int sum = 0;
                for (int k = 0; k < size; k++) sum += this.matrix[i][k] * this.matrix[k][j];
                squared[i][j] = sum;
            }

        return squared;
    }


    /*For Equivalence Classes:
     *   Compares the rows of the matrix, similar rows are grouped in one class.
     *   the copy is used to mark already compared rows by a <-1> as the first element.
     *   if a row is marked <-1>, it is skipped.
     * */
    private void checkEquivalence()
    {
        if (isReflexive && isTransitive && symmetry.equalsIgnoreCase(SYMMETRIC))
        {
            isEquivalence = true;

            int[][] copy = new int[size][size];
            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < size; j++)
                {
                    copy[i][j] = matrix[i][j];
                }
            }

            this.equivalenceClasses = "";
            int count = 1;
            for (int i = 0; i < size; i++)
            {
                if (copy[i][0] == -1)
                    continue; //Skip already compared row.
                if (i == size - 1)
                {
                    equivalenceClasses += "EQ" + count + ": " + list[i] + ", ";
                    break; //Handles last row/class.
                }
                equivalenceClasses += "EQ" + count + ": " + list[i] + ", ";

                for (int j = i + 1; j < size; j++)
                {
                    boolean equal = true; //checks row similarity.
                    for (int k = 0; k < size; k++)
                    {
                        if (copy[i][k] != copy[j][k])
                        {
                            equal = false;
                            break;
                        }
                    }
                    if (equal)
                    {
                        equivalenceClasses += list[j] + ", ";
                        copy[j][0] = -1; //marks the row as compared.
                    }
                }
                count++;
                equivalenceClasses += "\n";
            }
        }
        else
            equivalenceClasses = NONE;
    }


    //All logic explained in HasseDiagram methods.
    private void checkPartialOrder()
    {
        if (isReflexive && isTransitive && symmetry.equalsIgnoreCase(ANTISYMMETRIC))
        {
            isPartialOrder = true;
            HasseDiagram hasseDiagram = new HasseDiagram();
            this.hasseDiagram = hasseDiagram.representableToString();

            maximals = hasseDiagram.getMaximals();
            minimals = hasseDiagram.getMinimals();
            greatest = hasseDiagram.getGreatest();
            least = hasseDiagram.getLest();
        }
        else
        {
            hasseDiagram = greatest = least = NONE;
            maximals = new ArrayList<>();
            minimals = new ArrayList<>();
            maximals.add(NONE);
            minimals.add(NONE);
        }
    }

    private class HasseDiagram {

        HasseNode[] diagram;

        public HasseDiagram()
        {
            generate();
        }


        /*Step 1:
         *   generates basic Hasse diagram without loops, but with transitive edges.
         *   achieved by storing to what does the element point to
         *           moon --> sun  (sun stored as next)
         *   and what points to the element
         *           sun <-- moon  (moon stored as prev)
         *   diagram represented in a 1D array* containing all elements as HasseNode/s.
         *           *: underlying data structure is an array.
         * `
         * Step 2:
         *   removes transitive edges using <removeTransitiveEdges()>.
         * */
        private void generate()
        {
            this.diagram = new HasseNode[size];
            for (int i = 0; i < size; i++)
            { //initial step, stores plain nodes.
                this.diagram[i] = new HasseNode(list[i]);
            }

            for (int i = 0; i < size; i++)
            { //creates the references without loops.
                for (int j = 0; j < size; j++)
                {
                    if ((i != j) && (matrix[i][j] == 1))
                    {
                        diagram[i].nexts.add(diagram[j]);
                        diagram[j].prevs.add(diagram[i]);
                    }
                }
            }
            removeTransitiveEdges();
            printHasseNode();
        }


        /*To remove the transitive edges, this does the following:
         *   It computes the map with costs and paths using <calcPathsCosts()> for the current node.
         *   It stores the costs in an array, finds the minimum cost (hence, the desired edge),
         *   gets the node related to this cost and removes the reference from the current node to it,
         *  from both the map and diagram (specifically from <nexts>).
         *  And by that, the transitive edge is removed, it repeats the process for all nodes,
         *  and until there is a single path in the map for every node.
         * */
        private void removeTransitiveEdges()
        {
            for (int i = 0; i < size; i++)
            {
                if (!diagram[i].nexts.isEmpty())
                {
                    HashMap<Integer, HasseNode> map = calcPathsCosts(i);

                    while (map.size() > 1)
                    {
                        int min = Integer.MAX_VALUE;

                        Object[] keys = map.keySet().toArray();
                        for (int k = 0; k < keys.length; k++)
                        {
                            if ((Integer) keys[k] < min)
                                min = (Integer) keys[k];
                        }

                        diagram[i].nexts.remove(map.get(min));
                        map.get(min).prevs.remove(diagram[i]);
                        map.remove(min);
                    }

                }
            }
        }


        /*Inorder to help at finding transitive edges this does the following:
         *   Take the current node, traverse from it to the last possible node
         *           using the <nexts> list
         *   count how many steps it took to reach <cost>.
         *   store the node that began the path and the cost in a map.
         *   do the same for other nodes that the current node can go to.
         *   the map would look something like this:
         *          map: {2=earth, 1=sun} <cost/int, path/HasseNode>
         * */
        private HashMap<Integer, HasseNode> calcPathsCosts(int current)
        {
            HashMap<Integer, HasseNode> map = new HashMap<>();
            for (int j = 0; j < diagram[current].nexts.size(); j++)
            {
                int cost = 1;

                for (HasseNode tmp = diagram[current].nexts.get(j);
                     !tmp.nexts.isEmpty();
                     tmp = tmp.nexts.get(0), cost++)
                    ;

                map.put(cost, diagram[current].nexts.get(j));
            }
            return map;
        }


        /*First gets the representable map using <getRepresentable()>, then:
         *   Stores keys <HasseNode/s> and values <ArrayList<Integer `label`> in separate arrays.
         *   Since the insertion order is maintained, the two arrays would still be consistent.
         *   builds up the string in this style:
         *       P#: (- or P#..., {element})     P -> label.
         * */
        private String representableToString()
        {
            LinkedHashMap<HasseNode, ArrayList<Integer>> map = getRepresentable();
            Object[] keys = map.keySet().toArray();
            Object[] vals = map.values().toArray();

            String str = "";
            for (int i = 0; i < size; i++)
            {
                str += "P" + i + ": ";
                ArrayList<String> list = (ArrayList<String>) vals[i];
                str += "(";
                if (list.size() == 1)
                {
                    str += "-, {";
                }
                else
                {
                    for (int j = 1; j < list.size(); j++)
                    {
                        str += "P" + String.valueOf(list.get(j)) + " ";
                    }
                    str += ", {";
                }
                str += ((HasseNode) keys[i]).data + "})\n";
            }
            return str;
        }


        /*To be able to represent the diagram in the desired style, this does the following:
         *   The map stores a HasseNode and an ArrayList of preceding elements,
         *   the first item in the ArrayList is the label of the element itself,
         *   followed by preceding labels.
         *   This is achieved by first getting the nodes with no preceding elements (empty prev)
         *   then adding the rest from lower size of prev to higher.
         *   in case of nodes having same <prev> size and one coming before the other,
         *   it is added to <skipped> for later processing if its prev is not in the map.
         *   produces a map that looks like:
         *       ________________________________________________
         *       | HasseNode | [its label, preceding labels...] |
         *       ------------------------------------------------
         * */
        private LinkedHashMap<HasseNode, ArrayList<Integer>> getRepresentable()
        {
            LinkedHashMap<HasseNode, ArrayList<Integer>> map = new LinkedHashMap<>();
            int label = 0;
            for (int i = 0; i < size; i++)
            {
                if (diagram[i].prevs.isEmpty())
                {
                    ArrayList<Integer> list = new ArrayList<>();
                    list.add(label);
                    map.put(diagram[i], list);
                    label++;
                }
            }
            ArrayList<HasseNode> skipped = new ArrayList<>();
            for (int i = 0, lastPrevSize = 0; i < size; i++)
            {
                int minPrevSize = Integer.MAX_VALUE;

                HasseNode node = null;
                for (int z = 0; z < size; z++)
                {
                    if (diagram[z].prevs.size() >= lastPrevSize && diagram[z].prevs.size() < minPrevSize && !map.containsKey(diagram[z]))
                    {
                        if (diagram[z].prevs.size() > 0)
                        {
                            minPrevSize = diagram[z].prevs.size();
                            node = diagram[z];
                        }
                    }
                }
                if (node != null)
                {
                    ArrayList<Integer> list = new ArrayList<>();
                    list.add(label);
                    for (int k = 0; k < node.prevs.size(); k++)
                    {
                        try
                        {
                            //list.add(map.get(node.prevs.get(k)).get(0));
                            HasseNode n0 = node.prevs.get(k);
                            ArrayList<Integer> arr = map.get(n0);
                            list.add(arr.get(0));
                        }
                        catch (NullPointerException e)
                        {
                            skipped.add(node);
                            break;
                        }
                    }
                    map.put(node, list);
                    lastPrevSize = node.prevs.size();
                    label++;
                }

            }
            for (int j = 0; j < skipped.size(); j++)
            {
                ArrayList<Integer> list = map.get(skipped.get(j));
                for (int k = 0; k < skipped.get(j).prevs.size(); k++)
                {
                    list.add(map.get(skipped.get(j).prevs.get(k)).get(0));

                }

                map.put(skipped.get(j), list);
            }

            return map;
        }


        /* Maximal: nodes with size 0 of <nexts>
         *  Minimal: nodes with size 0 of <prevs>
         *  Greatest: if 1 maximal -> itself. else -> none
         *  Lest: if 1 minimal -> itself. else -> none
         *
         *  These just loop an comperes the size of <prevs, nexts>.
         *  */
        private ArrayList<String> getMaximals()
        {
            ArrayList<String> maximals = new ArrayList<>();

            for (int i = 0; i < size; i++)
            {
                if (diagram[i].nexts.size() == 0)
                    maximals.add(diagram[i].data);
            }
            return maximals;
        }

        private ArrayList<String> getMinimals()
        {
            ArrayList<String> minimals = new ArrayList<>();

            for (int i = 0; i < size; i++)
            {
                if (diagram[i].prevs.size() == 0)
                    minimals.add(diagram[i].data);
            }
            return minimals;
        }

        private String getGreatest()
        {
            if (getMaximals().size() == 1)
                return getMaximals().get(0);
            else
                return NONE;
        }

        private String getLest()
        {
            if (getMinimals().size() == 1)
                return getMinimals().get(0);
            else
                return NONE;
        }


        private class HasseNode {
            String data;
            ArrayList<HasseNode> nexts;
            ArrayList<HasseNode> prevs;

            public HasseNode(String data)
            {
                this.data = data;
                this.nexts = new ArrayList<>();
                this.prevs = new ArrayList<>();
            }

            @Override
            public String toString()
            {
                return data;
            }
        }


        private void printHasseNode()
        {
            for (int i = 0; i < size; i++)
            {
                System.out.println("data: " + diagram[i].data);
                System.out.println("nexts: " + diagram[i].nexts);
                System.out.println("prevs: " + diagram[i].prevs);
                System.out.println("------------------------------");
            }
        }
    }


    public String toString()
    {
        String str = size + "\n" + Arrays.toString(list) + "\n";

        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[i].length; j++)
                str += matrix[i][j] + " ";
            str += "\n";
        }
        return (str);
    }
}
