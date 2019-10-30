package ai;

import kalaha.GameState;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;


public class Minimax {
    private Node rootNode;
    public int bestMove = -1;//Initialize bestMove to an invalid move
    long startTime = System.currentTimeMillis();
    boolean timeOut = false;

    //constructor function
    public Minimax(GameState gameState) {
        rootNode = new Node();
        rootNode.this_GameState = gameState.clone();
    }

    public int mainMinimax(Node node, int depth, int player){

        int minEvaluation = 0;      //player2??
        int maxEvaluation = 0;      //player1??
        int evaluation = 0;            //存的是mainMinimax
        boolean firstTime = true;   
        if ( System.currentTimeMillis()-startTime>5000) {
            timeOut = true;
            return 0;
        }          
        //if time> 5000ms, the program will end the iterative deepening and reture an invalid value 0.
        if (node.this_GameState.gameEnded()){
            return node.getScore(player);
        }
        // reture the end value of the game to the evaluation.
        
        if(depth >0) {
            //If maxDepth has not been reached, expand the node tree
         
            for(int i = 1; i <= 6; i++){
                //Create a new child
                Node child = new Node();
                //Clone the gamestate to the new child
                child.this_GameState = node.this_GameState.clone();
                //Check if the move is possible
                if(child.this_GameState.moveIsPossible(i)) {
                    //If out theoretical move is possible, do it and add this as a legitimate child for the parent node
                    child.this_GameState.makeMove(i);//这是child走的呀
                    //System.out.println(child.this_GameState.getNextPlayer()); // 完全正确！1，22222
                    node.AddChild(child, i); // 是空的会continue，所以用i，把null的也放进去，不然，i的值会。
                }
            }
            
            
            
            

            for (int i = 1; i<=node.this_Children.size();i++){         //in this for loop we will do the recursive call the MainMinimax
                if (node.getChild(i)== null){
                    continue;// 最后的一个节点是数值，或者直接node=null所以是不用进入for循环的。it is no need to enter the for loop. it just return their value.
                }
                // 这一步是把，第一个有数值的节点赋值给父节点的
                if(firstTime) {
                    if (node.this_GameState.getNextPlayer() == 1){
                        maxEvaluation = mainMinimax(node.getChild(i), depth -1 , node.this_GameState.getNextPlayer());
                        if(node == rootNode) {
                            bestMove = i;
                        }
                    }
                    else
                    {
                        minEvaluation = mainMinimax(node.getChild(i), depth -1 , node.this_GameState.getNextPlayer());
                        if(node == rootNode) {
                            bestMove = i;
                        } //到达顶层了，就赋值吗？
                    }


                    firstTime = false;
                    continue;
                }
                
                
                // everytime run MainMinimax function the MaxEvaluation and MinEvaluation is empty 
                
             evaluation = mainMinimax(node.getChild(i),depth-1, node.this_GameState.getNextPlayer());// 传的是原来node的player呀?
             
                //System.out.println(evaluation);
                if (node.this_GameState.getNextPlayer() == 1){
                    if (evaluation > maxEvaluation) {
                        maxEvaluation = evaluation;
                        if (node == rootNode){
                            bestMove = i;
                            //System.out.println(bestMove);
                        }
                    }
                    continue;
                }
                else {
                    if (evaluation <minEvaluation) {
                        minEvaluation = evaluation;
                        if (node == rootNode){
                            bestMove = i;
                            //System.out.println(bestMove);
                        }
                    }

                    continue;
                }

            }
        }

        return  node.getScore(player);//  就是得到你的玩家减去对方玩家的分数。
    }
    public int getBestMove(int maxDepth, int player){
        for (int depth = 0; ; depth ++) {
            mainMinimax(rootNode, maxDepth, player);
            if (timeOut){
                break;
            }
        }
        return bestMove;
    }
    public static void main(String[] args) {
        int p = 1;
        GameState currentBoard = new GameState();
        Minimax minimax = new Minimax(currentBoard);
        int myMove = minimax.getBestMove(1,p);
        System.out.println("Ai move: " + myMove);
    }
}


