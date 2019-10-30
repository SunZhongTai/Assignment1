package ai;

import ai.Global;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import kalaha.*;
import ai.Minimax;
import java.util.Date;

/**
 * This is the main class for your Kalaha AI bot. Currently
 * it only makes a random, valid move each turn.
 * 
 * @author Johan Hagelbäck
 */
public class AIClient implements Runnable
{
    private int player;
    private JTextArea text;
    
    private PrintWriter out;
    private BufferedReader in;
    private Thread thr;
    private Socket socket;
    private boolean running;
    private boolean connected;
    	
    /**
     * Creates a new client.
     */
    public AIClient()
    {
	player = -1;
        connected = false;
        
        //This is some necessary client stuff. You don't need
        //to change anything here.
        initGUI();
	
        try
        {
            addText("Connecting to localhost:" + KalahaMain.port);
            socket = new Socket("localhost", KalahaMain.port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            addText("Done");
            connected = true;
        }
        catch (Exception ex)
        {
            addText("Unable to connect to server");
            return;
        }
    }
    
    /**
     * Starts the client thread.
     */
    public void start()
    {
        //Don't change this
        if (connected)
        {
            thr = new Thread(this);
            thr.start();
        }
    }
    
    /**
     * Creates the GUI.
     */
    private void initGUI()
    {
        //Client GUI stuff. You don't need to change this.
        JFrame frame = new JFrame("My AI Client");
        frame.setLocation(Global.getClientXpos(), 445);
        frame.setSize(new Dimension(420,250));
        frame.getContentPane().setLayout(new FlowLayout());
        
        text = new JTextArea();
        JScrollPane pane = new JScrollPane(text);
        pane.setPreferredSize(new Dimension(400, 210));
        
        frame.getContentPane().add(pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);
    }
    
    /**
     * Adds a text string to the GUI textarea.
     * 
     * @param txt The text to add
     */
    public void addText(String txt)
    {
        //Don't change this
        text.append(txt + "\n");
        text.setCaretPosition(text.getDocument().getLength());
    }
    
    /**
     * Thread for server communication. Checks when it is this
     * client's turn to make a move.
     */
    public void run()
    {
        String reply;
        running = true;
        
        try
        {
            while (running)
            {
                //Checks which player you are. No need to change this.
                if (player == -1)
                {
                    out.println(Commands.HELLO);
                    reply = in.readLine();

                    String tokens[] = reply.split(" ");
                    player = Integer.parseInt(tokens[1]);
                    
                    addText("I am player " + player);
                }
                
                //Check if game has ended. No need to change this.
                out.println(Commands.WINNER);
                reply = in.readLine();
                if(reply.equals("1") || reply.equals("2") )
                {
                    int w = Integer.parseInt(reply);
                    if (w == player)
                    {
                        addText("I won!");
                    }
                    else
                    {
                        addText("I lost...");
                    }
                    running = false;
                }
                if(reply.equals("0"))
                {
                    addText("Even game!");
                    running = false;
                }

                //Check if it is my turn. If so, do a move
                out.println(Commands.NEXT_PLAYER);
                reply = in.readLine();
                if (!reply.equals(Errors.GAME_NOT_FULL) && running)
                {
                    int nextPlayer = Integer.parseInt(reply);

                    if(nextPlayer == player)
                    {
                        out.println(Commands.BOARD);
                        String currentBoardStr = in.readLine();
                        boolean validMove = false;
                        while (!validMove)
                        {
                            long startT = System.currentTimeMillis();
                            //This is the call to the function for making a move.
                            //You only need to change the contents in the getMove()
                            //function.
                            GameState currentBoard = new GameState(currentBoardStr);
                            int cMove = getMove(currentBoard);
                            
                            //Timer stuff
                            long tot = System.currentTimeMillis() - startT;
                            double e = (double)tot / (double)1000;
                            
                            out.println(Commands.MOVE + " " + cMove + " " + player);
                            reply = in.readLine();
                            if (!reply.startsWith("ERROR"))
                            {
                                validMove = true;
                                addText("Made move " + cMove + " in " + e + " secs");
                            }
                        }
                    }
                }
                
                //Wait
                Thread.sleep(100);
            }
	}
        catch (Exception ex)
        {
            running = false;
        }
        
        try
        {
            socket.close();
            addText("Disconnected from server");
        }
        catch (Exception ex)
        {
            addText("Error closing connection: " + ex.getMessage());
        }
    }
    
    /**
     * This is the method that makes a move each time it is your turn.
     * Here you need to change the call to the random method to your
     * Minimax search.
     * 
     * @param currentBoard The current board state
     * @return Move to make (1-6)
     */
    public int getMove(GameState currentBoard)
    {
        Minimax minimax=new Minimax(currentBoard);        
        int myMove = minimax.getBestMove(6,player);
        //预先假定为9层,player1 先走
        addText("AIClient move:"+myMove);
        return myMove;
             
    }
    
    
    
//    
//    
//    public int getMove(GameState currentBoard) {
//
//       return iterativeWithPurning(currentBoard,5000)[1];//limit time in 5s //返回移动哪个
//     
//    }
//
//
//
//    private int[] pruning(GameState node, int depth, int alpha, int beta, long startTime, long limitTime) throws Exception {
//        int[] result = new int[2];  //result[0] is utilityValue,result[1] is move
//        int move = -1;//初始化move为-1
//        if (depth == 0 || !hasChild(node)) //如果深度达到最深或者它为叶节点（没有子节点）
//        {
//            result[0] = valuation(node);//get value by valuation()
//            return result;//最深层返回uitility value
//        }
//
//        boolean isMyTurn = this.player == node.getNextPlayer();
//        int utilityValue = isMyTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;//我的回合初始值设置为最小（we want max），地方回合初始值设置为最大（enemy want min）
//
//        for (int index = 1; index <= 6; index++) 
//        {
//            if (getTime() - startTime >= limitTime) throw new Exception();//when time is over,we throw error,stop searching
//
//            GameState childNode = node.clone();
//            if (childNode.makeMove(index)) //判断是否可以走这一步,可以则执行
//            {
//            	//如果可以递归获取他的子节点的uitility value
//                int childUtilityValue = pruning(childNode, depth - 1, alpha, beta, startTime, limitTime)[0];
//                if (isMyTurn) //我的回合，取最大的值为本节点的uitility value
//                {
//                    if (childUtilityValue > utilityValue) {
//                        utilityValue = childUtilityValue;
//                        move = index;
//                    }
//                    alpha = Math.max(alpha, utilityValue); //alpha purning
//                } 
//                else //敌人回合取最小值
//                {
//                    if (childUtilityValue < utilityValue) {
//                        utilityValue = childUtilityValue;
//                        move = index;
//                    }
//                    beta = Math.min(beta, utilityValue);  //beta purning
//                }
//                if ((childNode.getNextPlayer()!=node.getNextPlayer())&&(beta <= alpha)) break;//剪去不需要的情况
//            }
//        }
//        result[0] = utilityValue;  
//        result[1] = move;          
//        return result;
//    }
//
//    private int[] iterativeWithPurning(GameState node, long limitTime) {
//        int[] result = new int[2], temp;
//        long startTime=getTime();
//        for (int depth = 0; true; depth++) {
//            try 
//            {
//                //alpha's initial value is -∞,beta's initial value is +∞.
//                temp = pruning(node, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, startTime, limitTime); //temp里储存
//            } 
//            catch (Exception e) 
//            {
//                break;   //because time is over,we use result in last depth.
//            }
//            result = temp;  //time enough in this depth,update result
//        }
//        return result;
//    }
//
//
//
//    /**
//     * To validate weather a node(it's a game state) has children node.
//     * Or said if it is a terminal node.
//     *
//     * @param node the node need to be validated
//     * @return a boolean value
//     */
//    private boolean hasChild(GameState node) {
//        for (int i = 1; i <= 6; i++)
//            if (node.moveIsPossible(i))
//                return true;
//        return false;
//    }
//
//    private long getTime() {
//        Date now = new Date();
//        return now.getTime();
//    }


    private int valuation(GameState node) {
        //  utility value : my score minus enemy score
        return node.getScore(player) - node.getScore(player == 1 ? 2 : 1);
    }

 

    /**
     * Returns a random ambo number (1-6) used 
     * making
     * a random move.
     * 
     * @return Random ambo number
     */
    public int getRandom()
    {
        return 1 + (int)(Math.random() * 6);
    }
}

