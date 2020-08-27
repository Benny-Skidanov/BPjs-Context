package il.ac.bgu.cs.bp.bpjs.context.examples.chess.schema;

import il.ac.bgu.cs.bp.bpjs.context.ContextService;

import javax.persistence.*;

@Entity
@NamedQueries(value = {
        @NamedQuery(name = "Cell", query = "SELECT c FROM Cell c"), // Cell.All
        @NamedQuery(name = "EmptyCell", query = "SELECT c FROM Cell c WHERE c.piece IS NULL"), //Cell.Empty
        @NamedQuery(name = "OccupiedByWhitePiece", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.color = 'White'"), // Cell.WithWhitePiece
        @NamedQuery(name = "OccupiedByBlackPiece", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.color = 'Black'"), // Cell.WithBlackPiece
        @NamedQuery(name = "NotEmptyCell", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL"), // Cell.Nonempty
        @NamedQuery(name = "SpecificCell", query = "SELECT c FROM Cell c WHERE c.row=:row AND c.col=:col"), // Cell.
        @NamedQuery(name = "SpecificEmptyCell", query = "SELECT c FROM Cell c WHERE c.row=:row AND c.col=:col AND c.piece IS NULL"), // Cell.
        @NamedQuery(name = "PieceCell", query = "SELECT c FROM Cell c WHERE c.piece = :piece"), // Cell.
        @NamedQuery(name = "CellsWithPawn", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Pawn'"), // Cell.
        @NamedQuery(name = "CellsWithBlackPawn", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Pawn' AND c.piece.color = 'Black'"), // Cell.
        @NamedQuery(name = "CellsWithWhitePawn", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Pawn' AND c.piece.color = 'White'"), // Cell.
        @NamedQuery(name = "PromotionCellsWithBlackPawn", query = "SELECT c FROM Cell c WHERE c.row=0 AND c.piece IS NOT NULL AND c.piece.type = 'Pawn' AND c.piece.color = 'Black'"), // Cell.
        @NamedQuery(name = "PromotionCellsWithWhitePawn", query = "SELECT c FROM Cell c WHERE c.row=7 AND c.piece IS NOT NULL AND c.piece.type = 'Pawn' AND c.piece.color = 'White'"), // Cell.
        @NamedQuery(name = "EnPassantCellsWithBlackPawn", query = "SELECT c FROM Cell c WHERE c.row=3 AND c.piece IS NOT NULL AND c.piece.type = 'Pawn' AND c.piece.color = 'Black'"), // Cell.
        @NamedQuery(name = "EnPassantCellsWithWhitePawn", query = "SELECT c FROM Cell c WHERE c.row=4 AND c.piece IS NOT NULL AND c.piece.type = 'Pawn' AND c.piece.color = 'White'"), // Cell.
        //-------------------------
        @NamedQuery(name = "UpdateCell", query = "Update Cell c set c.piece=:piece where c=:cell"), // Cell.UpdatePiece
})
public class Cell extends BasicEntity
{
    @Column
    public final int row;
    @Column
    public final int col;
    @OneToOne
    public Piece piece;

    public Cell()
    {
        super();
        row = -1;
        col = -1;
        this.piece = null;
    }

    public Cell(int row, int col)
    {
        super("Cell[" + row + "," + col + "]");
        this.row = row;
        this.col = col;
        this.piece = null;
    }

    @Override
    public String toString() {
        return "Cell[" + row + "," + col + ","+piece+"]";
    }

    public Cell shift(int i, int j) {
        return (Cell) ContextService.getContextInstances("Cell["+(row+i)+","+(col+j)+"]").get(0);
    }
}
