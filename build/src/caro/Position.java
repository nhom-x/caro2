package caro;


public class Position {
    private int row;
    private int column;
    private int point;  //point of this position
    
    public Position(){
        row = column = -1;
        point = 0;
    }
    public Position(int row, int column){
        this.row = row;
        this.column = column;
        point = 0;
    }
    
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
