package il.ac.bgu.cs.bp.bpjs.context.examples.chess.effectFunction;

import il.ac.bgu.cs.bp.bpjs.context.ContextService;
import il.ac.bgu.cs.bp.bpjs.context.examples.chess.schema.Cell;
import il.ac.bgu.cs.bp.bpjs.context.examples.chess.schema.Piece;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By: Assaf, On 26/02/2020
 * Description:
 */
public class Promotion extends ContextService.EffectFunction
{
    public Promotion() {
        super(bEvent -> bEvent.name.equals("Promotion"));
    }

    @Override
    protected void innerExecution(EntityManager em, BEvent e) {
        Map<String, Cell> data = (Map<String, Cell>) e.maybeData;
        Cell source = data.get("source");
        Piece changeToPiece =  new Piece(Piece.Type.Rook,source.piece.color);

        
        Query q3 = em.createNamedQuery("RemovePiece");
        ContextService.setParameters(q3, new HashMap<>() {{
            put("piece", source.piece);
        }});
        q3.executeUpdate();
        
        Query q1 = em.createNamedQuery("UpdateCell");
        ContextService.setParameters(q1, new HashMap<>() {{
            put("piece", changeToPiece);
            put("cell", source);
        }});
        q1.executeUpdate();
    }
}
