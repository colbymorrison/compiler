package compiler.SemanticAction;

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
        Quadruple.add(nextQuad, quad);
        nextQuad++;
    }

    public String getInterCode() {
        int quadLabel = 1;
        String separator;
        StringBuilder builder = new StringBuilder("CODE");


        Enumeration<String[]> e = this.Quadruple.elements();
        e.nextElement();

        while (e.hasMoreElements()) {
            String[] quad = e.nextElement();
            builder.append(quadLabel).append(":  ").append(quad[0]).append("\n");

            if (quad[1] != null)
                builder.append(" ").append(quad[1]).append("\n");

            if (quad[2] != null)
                builder.append(", ").append(quad[2]).append("\n");

            if (quad[3] != null)
                builder.append(", ").append(quad[3]).append("\n");

            builder.append("\n");
            quadLabel++;
        }

        return builder.toString();
    }
}

