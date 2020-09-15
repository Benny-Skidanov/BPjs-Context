package il.ac.bgu.cs.bp.bpjs.context.examples.chess.schema;

import il.ac.bgu.cs.bp.bpjs.context.ContextService;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Entity
@NamedQueries(value = {
        @NamedQuery(name = "Cell", query = "SELECT c FROM Cell c"), // Cell.All
        @NamedQuery(name = "EmptyCell", query = "SELECT c FROM Cell c WHERE c.piece IS NULL"), //Cell.Empty
        @NamedQuery(name = "OccupiedByWhitePiece", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.color = 'White'"), // Cell.WithWhitePiece
        @NamedQuery(name = "OccupiedByBlackPiece", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.color = 'Black'"), // Cell.WithBlackPiece
        @NamedQuery(name = "NotEmptyCell", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL"), // Cell.Nonempty
        @NamedQuery(name = "SpecificCell", query = "SELECT c FROM Cell c WHERE c.row=:row AND c.col=:col"), // Cell.
        @NamedQuery(name = "SpecificRow", query = "SELECT c FROM Cell c WHERE c.row=:row"), // Cell.
        @NamedQuery(name = "SpecificColumn", query = "SELECT c FROM Cell c WHERE c.col=:col"), // Cell.
        @NamedQuery(name = "SpecificDiagonalP", query = "SELECT c FROM Cell c WHERE c.row =   c.col - :col + :row"), // Cell.
        @NamedQuery(name = "SpecificDiagonalN", query = "SELECT c FROM Cell c WHERE c.row = - c.col + :col + :row"),
        @NamedQuery(name = "SpecificEmptyCell", query = "SELECT c FROM Cell c WHERE c.row=:row AND c.col=:col AND c.piece IS NULL"), // Cell.
        @NamedQuery(name = "PieceCell", query = "SELECT c FROM Cell c WHERE c.piece = :piece"), // Cell.
        @NamedQuery(name = "CellsWithPawn", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Pawn'"), // Cell.
        @NamedQuery(name = "CellsWithRook", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Rook'"), // Cell.
        @NamedQuery(name = "CellsWithBlackRook", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Rook' AND c.piece.color = 'Black'"), // Cell.
        @NamedQuery(name = "CellsWithWhiteRook", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Rook' AND c.piece.color = 'White'"), // Cell.
        @NamedQuery(name = "CellsWithBlackBishop", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Bishop' AND c.piece.color = 'Black'"), // Cell.
        @NamedQuery(name = "CellsWithWhiteBishop", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Bishop' AND c.piece.color = 'White'"), // Cell.
        @NamedQuery(name = "CellsWithBlackPawn", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Pawn' AND c.piece.color = 'Black'"), // Cell.
        @NamedQuery(name = "CellsWithWhitePawn", query = "SELECT c FROM Cell c WHERE c.piece IS NOT NULL AND c.piece.type = 'Pawn' AND c.piece.color = 'White'"), // Cell.
        @NamedQuery(name = "PromotionCellsWithBlackPawn", query = "SELECT c FROM Cell c WHERE c.row=0 AND c.piece IS NOT NULL AND c.piece.type = 'Pawn' AND c.piece.color = 'Black'"), // Cell.
        @NamedQuery(name = "PromotionCellsWithWhitePawn", query = "SELECT c FROM Cell c WHERE c.row=7 AND c.piece IS NOT NULL AND c.piece.type = 'Pawn' AND c.piece.color = 'White'"), // Cell.
        @NamedQuery(name = "EnPassantCells", query = "SELECT eater, eaten FROM Cell eater, Cell eaten WHERE " +
                "((eater.row=3 AND eater.piece IS NOT NULL AND eater.piece.type = 'Pawn' AND eater.piece.color = 'Black') AND " +
                "(eaten.row=1 AND eaten.piece IS NOT NULL AND eaten.piece.type = 'Pawn' AND eaten.piece.color = 'White' AND (eaten.col=eater.col+1 OR eaten.col=eater.col-1)))" +
                " OR " +
                "((eater.row=4 AND eater.piece IS NOT NULL AND eater.piece.type = 'Pawn' AND eater.piece.color = 'White') AND " +
                "(eaten.row=6 AND eaten.piece IS NOT NULL AND eaten.piece.type = 'Pawn' AND eaten.piece.color = 'Black' AND (eaten.col=eater.col+1 OR eaten.col=eater.col-1)))"), // Cell.
        //-------------------------
        @NamedQuery(name = "UpdateCell", query = "Update Cell c set c.piece=:piece where c=:cell"), // Cell.UpdatePiece
})
public class Cell extends BasicEntity implements Comparable<Cell> {
    @Column
    public final int row;
    @Column
    public final int col;
    @OneToOne
    public Piece piece;

    public Cell() {
        super();
        row = -1;
        col = -1;
        this.piece = null;
    }

    public Cell(int row, int col) {
        super("Cell[" + row + "," + col + "]");
        this.row = row;
        this.col = col;
        this.piece = null;
    }

    @Override
    public String toString() {
        return "Cell[" + row + "," + col + "," + piece + "]";
    }

    public Cell shift(int i, int j) {
        return (Cell) ContextService.getContextInstances("Cell[" + (row + i) + "," + (col + j) + "]").get(0);
    }


    @Override
    public int compareTo(@NotNull Cell o) {
        int i = Integer.compare(row, o.row);
        return i == 0 ? Integer.compare(col, o.col) : i;
    }
}
