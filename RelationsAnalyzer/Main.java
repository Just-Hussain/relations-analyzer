package RelationsAnalyzer;

public class Main {
    public static void main(String[] args) {

        //Provide path to file here.
        Relation relation = new Relation("C:\\Users\\just-\\Desktop\\samples\\sample9.txt");

        System.out.println(relation);
        System.out.println("reflexivity: " + relation.reflexivity);
        System.out.println("symmetry: " + relation.symmetry);
        System.out.println("transitivity: " + relation.isTransitive);
        System.out.println("equivalence relation: " + relation.isEquivalence);
        System.out.println("equivalence classes>\n" + relation.equivalenceClasses);
        System.out.println("partial order: " + relation.isPartialOrder);
        System.out.println("hasse>\n" + relation.hasseDiagram);
        System.out.println("maximals: " + relation.maximals.toString());
        System.out.println("minimals: " + relation.minimals.toString());
        System.out.println("greatest: " + relation.greatest);
        System.out.println("least: " + relation.least);

    }
}
