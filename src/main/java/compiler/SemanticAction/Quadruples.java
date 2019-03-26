package compiler.SemanticAction;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

public class Quadruples {
    private Vector<String[]> Quadruple;
    private int nextQuad;

    public Quadruples() {
        Quadruple = new Vector<>();
        nextQuad = 0;
        String[] dummy_quadruple = new String[4];
        dummy_quadruple[0] = dummy_quadruple[1] = dummy_quadruple[2] = dummy_quadruple[3] = null;
        Quadruple.add(nextQuad, dummy_quadruple);
        nextQuad++;
    }

    public String getField(int quadIndex, int field) {
        return Quadruple.elementAt(quadIndex)[field];
    }

    public void setField(int quadIndex, int index, String field) {
        Quadruple.elementAt(quadIndex)[index] = field;
    }

    public int getNextQuad() {
        return nextQuad;
    }

    public void incrementNextQuad() {
        nextQuad++;
    }

    public String[] getQuad(int index) {
        return Quadruple.elementAt(index);
    }

    public void addQuad(String[] quad) {
        System.out.println("Adding quad " + Arrays.toString(quad));
        Quadruple.add(nextQuad, quad);
        nextQuad++;
    }

    public String getInterCode() {
        int quadLabel = 1;
        StringBuilder builder = new StringBuilder("CODE\n");


        Enumeration<String[]> e = this.Quadruple.elements();
        e.nextElement();

        while (e.hasMoreElements()) {
            String[] quad = e.nextElement();
            builder.append(quadLabel).append(":  ").append(quad[0]);

            if (quad.length > 1)
                builder.append(" ").append(quad[1]);

            for(int i = 2; i < quad.length; i++)
                builder.append(", ").append(quad[2]);

            builder.append("\n");
            quadLabel++;
        }

        return builder.toString();
    }
}

