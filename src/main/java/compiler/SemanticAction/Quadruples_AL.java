package compiler.SemanticAction;

import java.util.ArrayList;
import java.util.Enumeration;

public class Quadruples_AL {
    private ArrayList<String[]> Quadruple;

    public Quadruples_AL() {
        Quadruple = new ArrayList<>();
        String[] dummy_quadruple = new String[4];
        dummy_quadruple[0] = dummy_quadruple[1] = dummy_quadruple[2] = dummy_quadruple[3] = null;
        Quadruple.add(dummy_quadruple);
    }

    public String getField(int quadIndex, int field) {
        return Quadruple.get(quadIndex)[field];
    }

    public void setField(int quadIndex, int index, String field) {
        Quadruple.get(quadIndex)[index] = field;
    }

    public String[] getQuad(int index) {
        return Quadruple.get(index);
    }

    public void addQuad(String[] quad) {
        Quadruple.add(quad);
    }

    public void print() {
        StringBuilder out = new StringBuilder("CODE");
        int quadLabel = 1;

        for (String[] quad : Quadruple) {
            out.append(quadLabel).append(":  ").append(quad[0]);

            if (quad[1] != null)
                out.append(" ").append(quad[1]);

            if (quad[2] != null)
                out.append(", ").append(quad[2]);

            if (quad[3] != null)
                out.append(", ").append(quad[3]);

            out.append("\n");
            quadLabel++;
        }
        System.out.println(out.toString());
    }
}

