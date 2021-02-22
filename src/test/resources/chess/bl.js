 /*
   1. PGN basics :
        1.a. Moves :
                Pawn -> e4
                Knight -> Nf6
                Bishop -> Be3
                Rook -> Rh3
                Queen -> Qd2
                King -> Kc2
        1.b. Taking :
                Pawn -> exd5
                Knight -> Nxd5
                Bishop -> Bxf4
                Rook -> Rxd2
                Queen -> Qxe3
                King -> Kxg1
        1.c. Special Exceptions :
                Short castle -> O-O
                Long castle -> O-O-O
                Check ( moving or taking ) -> Qxd3+ , e4+
                Mate ( moving or taking ) -> Qxd3# , e4#
                Two pieces from the same king can move to the same cell ( or take on this cell ) -> R1d1 , fxe5

    2. Center squares - c4, d4, e4, f4, c5, d5, e5, f5

 */

 // define moves
 const centerCaptureMoves = [moveEvent("Pawn", "e2", "e4")]
 const pawnDevelopingMoves = [moveEvent("Pawn", "c2", "c4")]


 // const pawnDevelopingMoves = bp.EventSet("pawnDevelopingMoves", function (e) {
 //     moveEvent("", "c2", "c4")
 //     //moveEvent("", "d2", "d4")
 //     //moveEvent("", "e2", "e4")
 //     //moveEvent("", "f2", "f4")
 // })

 // Game bthread
bthread("Game thread", function (entity) {
     while (true) {
         sync({request: bp.Event("Game Phase", "Opening")})
         sync({request: bp.Event("Game Phase", "Mid Game")})
         sync({request: bp.Event("Game Phase", "End Game")})
         // need for a way to block all events
         break
     }
 });

/* Strategies in the opening:
    1. Developing pieces
    2. Strengthening the center squares
 */

 // Game phase changed event
 ctx.registerEffect("Game Phase", e => {
     bp.log.info("Game phase effect")
     if(e.data == "Opening") { bp.log.info("Opening Phase Starting")}
 })

 ctx.registerQuery("Phase.Opening", function(entity) { // returned entities
     let phase = ctx.getEntityById("Phase.Opening")
     return true
 })


 // Develop event
 ctx.registerEffect("Develop", e => {
     let entity = ctx.getEntityById("Phase.Developing")
     bp.log.info(entity)
     entity.phase = "Phase.Developing.Pawn"
     bp.log.info(entity)
     ctx.updateEntity(entity)
 })
/*
 // Develop event
 ctx.registerEffect("Strengthen", e => {
     let entity = ctx.getEntityById("Phase.Strengthen")
     bp.log.info(entity)
     entity.phase = "Strengthen pawn"
     //ctx.updateEntity(entity)
 })
*/
 ctx.bthread("Developing pieces", "Phase.Opening", function (entity) {
     while (true) {
         //sync({wait : bp.Event("Game Phase", "Opening")})
         sync({request: bp.Event("Develop", "pawn")})
         //sync({request: bp.Event("Develop", "bishop and knight")})
         // sync({request: bp.Event("Develop", "queen and rook")})
     }
 });
/*
 ctx.bthread("Strengthening the center", "Phase.Opening", function (entity) {
     while (true) {
         sync({request: bp.Event("Strengthen")});
     }
 });
*/

 ctx.registerQuery("Phase.Developing.Pawn", function(entity) { //returned entities -> pawn
     let phase = ctx.getEntityById("Phase.Developing")
     if(phase == null || phase.phase != "Phase.Developing.Pawn" || entity.type != "Piece" ||
            entity.subtype != "Pawn" || entity.color != "Black")
         return false
     return true
 })
/*
 ctx.registerQuery("Phase.Strengthen.Pawn", function(entity) { //returned entities -> pawn
     let phase = ctx.getEntityById("Phase.Strengthen")
     if(phase == null || phase.phase != "Phase.Strengthen.Pawn" || entity.type != "Piece" ||
            entity.subtype != "Pawn" || entity.color != "Black")
         return false
     return true
 })
*/

 ctx.bthread("Developing Pawn", "Phase.Developing.Pawn", function (entity) {
     while (true) {
         sync({request: pawnDevelopingMoves});
         bp.log.info("~~~ Explanation ... ~~~ ")
         // sync({wait: centerCaptureMoves});
     }
 });
/*
 ctx.bthread("Strengthen Pawn", "Phase.Strengthen.Pawn", function (entity) {
     while (true) {
         sync({request: centerCaptureMoves});
         bp.log.info("~~~ Explanation ... ~~~ ")
         sync({wait: pawnDevelopingMoves});
     }
 });
*/
 bthread("populate data", function() {
     ctx.beginTransaction()
     ctx.insertEntity("Phase.Developing", "Phase.Developing",{phase:""})
     //ctx.insertEntity("Phase.Strengthen", "Phase.Strengthen", {phase: ""})
     ctx.endTransaction()
 })



