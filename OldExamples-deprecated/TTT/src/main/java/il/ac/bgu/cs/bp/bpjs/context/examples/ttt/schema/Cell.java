package il.ac.bgu.cs.bp.bpjs.context.examples.ttt.schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries(value = {
        @NamedQuery(name = "Cell", query = "SELECT c FROM Cell c"),
        @NamedQuery(name = "CornerCell", query = "SELECT c FROM Cell c WHERE (c.i = 0 AND c.j = 0) OR (c.i = 0 AND c.j = 2) OR (c.i = 2 AND c.j = 0) OR (c.i = 2 AND c.j = 2)"),
        @NamedQuery(name = "SpecificCell", query = "SELECT c FROM Cell c WHERE c.i=:i AND c.j=:j"),
        @NamedQuery(name = "EmptyCell", query = "SELECT c FROM Cell c WHERE c.value = ''"),
        @NamedQuery(name = "NonEmptyCell", query = "SELECT c FROM Cell c WHERE not(c.value = '')"),
        @NamedQuery(name = "UpdateCell", query = "Update Cell C set C.value=:val where C=:cell"),
        @NamedQuery(name = "MarkCellWithX", query = "Update Cell C set C.value='X' where C=:cell"),
})
public class Cell extends BasicEntity {
    @Column
    public final int game;
    @Column
    public final int i;
    @Column
    public final int j;
    @Column
    public String value="";

    protected Cell() {
        super();
        i=0;
        j=0;
        game = 0;
    }

    public Cell(int i, int j, int game) {
        super("cell("+game+": "+i+","+j+")");
        this.i = i;
        this.j = j;
        this.game = game;
    }
}
