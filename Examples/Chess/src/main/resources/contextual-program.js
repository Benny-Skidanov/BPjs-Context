function Move(source, target) {
    return bp.Event("Move", {source: source, target: target});
}
function Promotion(source) {
    return bp.Event("Promotion", {source: source});
}


function AnyMoveFrom(source) {
    return bp.EventSet("AnyMoveFrom " + source, function (e) {
        return e.name.equals("Move") && e.data.source != null && e.data.source.equals(source);
    });
}

function AnyMoveTo(target) {
    return bp.EventSet("AnyMoveTo " + target, function (e) {
        return e.name.equals("Move") && e.data.target != null && e.data.target.equals(target);
    });
}

var moves = bp.EventSet("Moves", function (e) {
    return e.name.equals("Move");
});

var whiteMoves = bp.EventSet("White Moves", function (e) {
    return moves.contains(e) && (e.data.source.piece != null) && (Piece.Color.White.equals(e.data.source.piece.color));
});

var blackMoves = bp.EventSet("black Moves", function (e) {
    return moves.contains(e) && (e.data.source.piece != null) && (Piece.Color.Black.equals(e.data.source.piece.color));
});

var outBoundsMoves = bp.EventSet("", function (e) {
    return moves.contains(e) && (e.data.source.row < 0 || e.data.source.row > 7 || e.data.source.col < 0 || e.data.source.col > 7 || e.data.target.row < 0 || e.data.target.row > 7 || e.data.target.col < 0 || e.data.target.col > 7);
});

var donePopulationEvent = bp.EventSet("Start Event", function (e) {
    return e.name.equals("Done Populate");
});

bp.registerBThread("AfterPopulation", function () {
    bp.sync({waitFor: donePopulationEvent});

// Requirement : Turn Base Game, White Starts
    bp.registerBThread("EnforceTurns", function () {
        while (true) {
            bp.sync({waitFor: whiteMoves, block: blackMoves});
            bp.sync({waitFor: blackMoves, block: whiteMoves});
        }
    });

// Requirement : Moving Pieces only inside the board bounds.  - not in wikipedia
    bp.registerBThread("Movement in bounds", function () {
        bp.sync({block: outBoundsMoves});
    });

//<editor-fold desc="Pawn Rules">

    function MovePawnOneForward(cell, forward) {
        let contextEndedEvent = CTX.AnyContextEndedEvent("CellsWith" + cell.piece.color + "Pawn", cell);
        let currentCell = cell;
        if (currentCell.row == 0 || currentCell.row == 7){
            return;
        }
        let targetCell = currentCell.shift(forward, 0);
        let occupiedTargetCellNewContextEvent = CTX.AnyNewContextEvent("NotEmptyCell", targetCell);
        let unoccupiedTargetCellNewContextEvent = CTX.AnyNewContextEvent("EmptyCell", targetCell);
        while (true) {
            if (targetCell.piece == null) {
                bp.sync({
                    request: Move(currentCell, targetCell),
                    waitFor: occupiedTargetCellNewContextEvent,
                    interrupt: contextEndedEvent
                });
            } else {
                bp.sync({
                    waitFor: unoccupiedTargetCellNewContextEvent,
                    interrupt: contextEndedEvent
                });
            }
            targetCell = currentCell.shift(forward, 0);
        }
    }

    CTX.subscribe("WhitePawn - Move 1 forward", "CellsWithWhitePawn", function (cell) {
        MovePawnOneForward(cell, 1);
    }, true);

    CTX.subscribe("BlackPawn - Move 1 forward", "CellsWithBlackPawn", function (cell) {
        MovePawnOneForward(cell, -1);
    }, true);

    /*CTX.subscribe("WhitePawn - Move 1 forward - other option", "CellsWithWhitePawn", function (cell) {
        if (cell.piece.didMove == false){
            bp.sync({waitFor:donePopulationEvent});
        }
        let contextWhiteEndedEvent = CTX.AnyContextEndedEvent("CellsWithWhitePawn",cell);
        let targetCell = getCell(cell.row + 1, cell.col);
        CTX.subscribe("Move Pawn forward from "+cell+" to empty cell", "SpecificEmptyCell_"+targetCell,function(c) {
            let contextEmptyEndedEvent = CTX.AnyContextEndedEvent("EmptyCell_"+targetCell, c);
            bp.sync({   request: Move(cell, targetCell),
                interrupt: [contextEmptyEndedEvent, contextWhiteEndedEvent]});
        }, true);
        bp.sync({waitFor: contextWhiteEndedEvent});
        bp.sync({request:CTX.UnsubscribeEvent("Move Pawn forward from "+cell+" to empty cell"), block:NonUnsubscribeEvents});
    });*/

    CTX.subscribe("Pawn - Move 2 forward", "UnmovedPawns", function (pawn) {
        var contextEndedEvent = CTX.AnyContextEndedEvent("UnmovedPawns", pawn);
        let forward1 = pawn.color.equals(Piece.Color.Black) ? -1 : 1;
        var forward2 = forward1 * 2;
        var currentCell = pawn.cell;
        if (currentCell.row == 0 || currentCell.row == 7){
            return;
        }
        var targetCell = currentCell.shift(forward2, 0);
        var pathtotargetCell = currentCell.shift(forward1, 0);
        let occupiedTargetCellNewContextEvent = CTX.AnyNewContextEvent("NotEmptyCell", targetCell);
        let unoccupiedTargetCellNewContextEvent = CTX.AnyNewContextEvent("EmptyCell", targetCell);
        let occupiedPathToTargetCellNewContextEvent = CTX.AnyNewContextEvent("NotEmptyCell", pathtotargetCell);
        let unoccupiedPathToTargetCellNewContextEvent = CTX.AnyNewContextEvent("EmptyCell", pathtotargetCell);
        while (true) {
            if (targetCell.piece == null && pathtotargetCell.piece == null) {
                bp.sync({
                    request: Move(currentCell, targetCell),
                    waitFor: [occupiedTargetCellNewContextEvent, occupiedPathToTargetCellNewContextEvent],
                    interrupt: contextEndedEvent
                });
            } else {
                bp.sync({
                    waitFor: [unoccupiedTargetCellNewContextEvent, unoccupiedPathToTargetCellNewContextEvent],
                    interrupt: contextEndedEvent
                });
            }
            targetCell = currentCell.shift(forward2, 0);
            pathtotargetCell = currentCell.shift(forward1, 0);
        }
    }, true);

    function PawnCapturing(cell) {
        let color = cell.piece.color;
        let opponentColor = color.equals(Piece.Color.Black) ? Piece.Color.White : Piece.Color.Black;
        let contextEndedEvent = CTX.AnyContextEndedEvent("CellsWith" + color + "Pawn", cell);
        let forward = color.equals(Piece.Color.Black) ? -1 : 1;
        let currentCell = cell;
        let targetCellR = null;
        let targetCellL = null;
        while (true) {
            if (currentCell.row == 0 || currentCell.row == 7){
                break;
            }
            if (currentCell.col + 1 <= 7) {
                targetCellR = currentCell.shift(forward, 1);
            } else {
                targetCellR = currentCell.shift(forward, -1);
            }
            if (currentCell.col - 1 >= 0) {
                targetCellL = currentCell.shift(forward, -1);
            } else {
                targetCellL = currentCell.shift(forward, 1);
            }
            let occupiedByOpponentTargetCellLNewContextEvent = CTX.AnyNewContextEvent("OccupiedBy" + opponentColor + "Piece", targetCellL);
            let occupiedByOpponentTargetCellLContextEndedEvent = CTX.AnyContextEndedEvent("OccupiedBy" + opponentColor + "Piece", targetCellL);
            let occupiedByOpponentTargetCellRNewContextEvent = CTX.AnyNewContextEvent("OccupiedBy" + opponentColor + "Piece", targetCellR);
            let occupiedByOpponentTargetCellRContextEndedEvent = CTX.AnyContextEndedEvent("OccupiedBy" + opponentColor + "Piece", targetCellR);
            if (targetCellR.piece != null && targetCellL.piece != null && targetCellR.piece.color.equals(opponentColor) && targetCellL.piece.color.equals(opponentColor)) {
                bp.sync({
                    request: [Move(currentCell, targetCellR), Move(currentCell, targetCellL)],
                    waitFor: [occupiedByOpponentTargetCellRContextEndedEvent, occupiedByOpponentTargetCellLContextEndedEvent],
                    interrupt: contextEndedEvent
                });
            } else if (targetCellR.piece != null && targetCellL.piece == null && targetCellR.piece.color.equals(opponentColor)) {
                bp.sync({
                    request: Move(currentCell, targetCellR),
                    waitFor: [occupiedByOpponentTargetCellRContextEndedEvent, occupiedByOpponentTargetCellLNewContextEvent],
                    interrupt: contextEndedEvent
                });
            } else if (targetCellR.piece == null && targetCellL.piece != null && targetCellL.piece.color.equals(opponentColor)) {
                bp.sync({
                    request: Move(currentCell, targetCellL),
                    waitFor: [occupiedByOpponentTargetCellLContextEndedEvent, occupiedByOpponentTargetCellRNewContextEvent],
                    interrupt: contextEndedEvent
                });
            } else {
                bp.sync({
                    waitFor: [occupiedByOpponentTargetCellRNewContextEvent, occupiedByOpponentTargetCellLNewContextEvent],
                    interrupt: contextEndedEvent
                });
            }
        }
    }

    CTX.subscribe("BlackPawn - Capturing", "CellsWithBlackPawn", function (cell) {
        PawnCapturing(cell);
    }, true);

    CTX.subscribe("WhitePawn - Capturing", "CellsWithWhitePawn", function (cell) {
        PawnCapturing(cell);
    }, true);

    CTX.subscribe("WhitePawn - Promotion", "PromotionCellsWithWhitePawn", function (cell) {
        bp.sync({request: Promotion(cell), block: moves})
    }, true);

    CTX.subscribe("BlackPawn - Promotion", "PromotionCellsWithBlackPawn", function (cell) {
        bp.sync({request: Promotion(cell), block: moves})  
    }, true);

    CTX.subscribe("WhitePawn - Capturing en passant", "EnPassantCellsWithWhitePawn", function (cell) {
        PawnEnPassant(cell)
    }, true);

    CTX.subscribe("BlackPawn - Capturing en passant", "EnPassantCellsWithBlackPawn", function (cell) {
        PawnEnPassant(cell)
    }, true);

    function PawnEnPassant(cell) {
        let color = cell.piece.color;
        let contextEndedEvent = CTX.AnyContextEndedEvent("EnPassantCellsWith" + color + "Pawn", cell);
        let forward = color.equals(Piece.Color.Black) ? -2 : 2;
        let currentCell = cell;
        let pawnCellR = null;
        let pawnCellL = null;
        if (currentCell.col + 1 <= 7) {
            pawnCellR = currentCell.shift(forward, 1);
        } else {
            pawnCellR = currentCell.shift(forward, -1);
        }
        if (currentCell.col - 1 >= 0) {
            pawnCellL = currentCell.shift(forward, -1);
        } else {
            pawnCellL = currentCell.shift(forward, 1);
        }
        for (i=0;i<2;i++){
            let e1 = bp.sync({
                        waitFor: [Move(pawnCellR, pawnCellR.shift(-forward, 0)),Move(pawnCellL, pawnCellL.shift(-forward, 0))],
                        interrupt: contextEndedEvent
            });
            let e2 = bp.sync({
                        request: Move(cell, e1.source.shift(-forward/2, 0)),
                        waitFor:moves,
                        interrupt: contextEndedEvent
            });
            if (e2.equals(Move(cell, e.source.shift(-forward/2, 0)))){
                bp.sync({request: EnPassant(cell,e1.source), block: moves})
            }
        }
    }
});