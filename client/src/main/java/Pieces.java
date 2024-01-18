/**
 * This class organizes and declares what each piece is and then what moves they make.
 */
public class Pieces {
    //King, Queen, Bishop, Knight, Castle, Pawn;
    private String piece_name;
    private String location;
    private String team_color;

    public Pieces(String piece_name,String location, String color) {
        this.piece_name = piece_name;
        this.location = location;
        this.team_color = color;
    }

    public String getPiece(String location){
        //should use location to say what piece is there to help validate moves/kills and see what piece will move
        return this.piece_name;
    }

}
