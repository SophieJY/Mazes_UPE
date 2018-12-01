import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class Main {
    private static HttpConnection httpConnection;
    private final static String[] directions = {"UP", "LEFT", "DOWN", "RIGHT"};
    private final static int[] rArray = {-1, 0, 1, 0};
    private final static int[] cArray = {0, -1, 0, 1};
    private static HashMap<String, String> oppositeDirection = new HashMap<>();
    private static boolean[][] localGrid = null;

    public static void main(String[] args) {
        oppositeDirection.put("UP", "DOWN");
        oppositeDirection.put("DOWN", "UP");
        oppositeDirection.put("LEFT", "RIGHT");
        oppositeDirection.put("RIGHT", "LEFT");
        httpConnection = new HttpConnection();
        httpConnection.initToken();

        solveNewMaze(httpConnection);
    }

    public static void solve(int r, int c, String prevDirection){
        localGrid[r][c] = true;
        for(int i = 0; i < directions.length; i++) {
            if(!isOutOfBound(r+rArray[i], c+cArray[i]) && !localGrid[r+rArray[i]][c+cArray[i]]) {
                if(!oppositeDirection.get(directions[i]).equals(prevDirection)) {
                    String result = httpConnection.tryMove(directions[i]);
                    //System.out.print("point: " + r + " " + c + " direction: " + directions[i] + " result: " + result + "\n");

                    if (result.equals("SUCCESS")) {
                        solve(r + rArray[i], c + cArray[i], directions[i]);
                    } else if (result.equals("END")) {
                        solveNewMaze(httpConnection);
                    }
                }
            }
        }
        if(prevDirection != null) {
            httpConnection.tryMove(oppositeDirection.get(prevDirection));
        }
        return;
    }

    private static boolean isOutOfBound(int r, int c) {
        if(r < 0 || r >= localGrid.length || c < 0 || c >= localGrid[0].length) {
            return true;
        } else {
            return false;
        }
    }

    public static void solveNewMaze(HttpConnection httpConnection) {
        JSONObject jsonObject = httpConnection.getMazeState();

        String status = jsonObject.get("status").toString();
        if(status.equals("FINISHED")) {
            System.out.print("Maze Finished!\n");
            return;
        } else if (status.equals("NONE")) {
            System.out.print("Session expired!\n");
            return;
        } else if (status.equals("GAME_OVER")) {
            System.out.print("Oops! You stepped out of bound.\n");
            return;
        } else {
            JSONArray mazeSize = jsonObject.optJSONArray("maze_size");
            int lengthC = mazeSize.optInt(0);
            int lengthR = mazeSize.optInt(1);
            localGrid = new boolean[lengthR][lengthC];
            JSONArray locationArray = jsonObject.optJSONArray("current_location");
            //System.out.print("currentLocation: " + locationArray.optInt(1) + " " + locationArray.optInt(0) + "\n");
            int c = locationArray.optInt(0);
            int r = locationArray.optInt(1);
            solve(r, c, null);
        }
    }
}
