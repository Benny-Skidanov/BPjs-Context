package il.ac.bgu.cs.bp.bpjs.context.examples.chess.effectFunction;

import il.ac.bgu.cs.bp.bpjs.context.ContextService;
import il.ac.bgu.cs.bp.bpjs.context.examples.chess.schema.Cell;
import il.ac.bgu.cs.bp.bpjs.context.examples.chess.schema.Piece;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;

public class EnPassant extends ContextService.EffectFunction
{
    public EnPassant() {
        super(bEvent -> bEvent.name.equals("EnPassant"));
    }

    @Override
    protected void innerExecution(EntityManager em, BEvent e) {
        Cell cell = (Cell) e.maybeData;

        Query q1 = em.createNamedQuery("UpdateCell");
        ContextService.setParameters(q1, new HashMap<>() {{
            put("piece", null);
            put("cell", cell);
        }});
        q1.executeUpdate();

        Query q2 = em.createNamedQuery("RemovePiece");
        ContextService.setParameters(q2, new HashMap<>() {{
            put("piece", cell.piece);
        }});
        q2.executeUpdate();
    }
}
