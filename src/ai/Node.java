package ai;

import kalaha.GameState;
import java.util.*;


public class Node {
    List<Node> this_Children;
    GameState this_GameState;
    
 
	
	
    public int getScore(int player) {
        return this_GameState.getScore(player) - this_GameState.getScore(2 / player);
    }
    //这里也是调用的gamestate里的getscore
    //当player为1  房间1里面的棋子减去房间2里面的棋子
    //当player为2  房间2里面的棋子减去房间1里面的棋子
    //也就是得到的差值的分数最大，那种可能就是最可能 赢得吧 而分数就是各自house里面的ambos
    
    public Node() {
        this_Children = new ArrayList<Node>(6);
        for(int i = 0; i < 6; i++) {
            this_Children.add(null);
        }
    }

    public void AddChild(Node child, int index) {
        this_Children.set(index-1,child);
        
    }


    public Node getChild(int index) {
        Node node = this_Children.get(index-1);
        return node;
    }
    //


}