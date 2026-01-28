import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.applet.Applet;

public class Sniiice extends Applet implements Runnable {
	/**
	 * Dan Quinn
	 * NOISIA Films
	 * 2007
	 */
    Thread runner = null;

    private static final long serialVersionUID = 1L;
    static final Color[] colors = {Color.blue,Color.red,Color.orange,Color.green};
    static final int keys[][] = {{KeyEvent.VK_UP,KeyEvent.VK_RIGHT,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT},
    			    {KeyEvent.VK_W,KeyEvent.VK_D,KeyEvent.VK_S,KeyEvent.VK_A},
    			    {KeyEvent.VK_Y,KeyEvent.VK_J,KeyEvent.VK_H,KeyEvent.VK_G},
    			    {KeyEvent.VK_8,KeyEvent.VK_6,KeyEvent.VK_5,KeyEvent.VK_4}};
    final int WIDTH  = 600, HEIGHT = 400, GAMESPEED = 5, BLOCKLENGTH=50;
    int players, playersleft;//0: starting, 1: game over, -x: x wins
    Block[] blocks;
    
    Image image;
    Graphics graphics;

    public void init() {
        image = createImage(WIDTH, HEIGHT);
        graphics = image.getGraphics();
        setSize(WIDTH, HEIGHT);
    }
    
    public void start() {
        // user visits the page, create a new thread
        if (runner == null) {
            runner = new Thread(this);
            runner.start();
            addKeyListener(new ShiftListener());
            playersleft=0;
            graphics.clearRect(0,0,WIDTH,HEIGHT);
        }
    }
    
    public void stop() {
        // user leaves the page, stop the thread
        if (runner != null && runner.isAlive())
            runner.stop();
        runner = null;
    }

    public void run() {
        while (runner != null) {
            repaint();
            try {Thread.sleep(150/GAMESPEED);}catch (InterruptedException e){}
        }
    }
    public void paint( Graphics g ) {
        update(g);
    }
    public void update( Graphics g ) {
    	graphics.setColor(Color.black);
    	graphics.drawRect(25,25,WIDTH-50,HEIGHT-50);
    	graphics.drawString("Graveyard:",25,HEIGHT-5);
    	graphics.drawString("NOISIA Films",28,40);
    	if(playersleft<0){
    		graphics.setColor(colors[-(playersleft+1)]);
			graphics.drawString("Player "+(-playersleft)+" wins!", WIDTH/2-40, HEIGHT/2);
			graphics.drawString("Take a victory dance, Player "+(-playersleft), WIDTH/2-85,HEIGHT/2+20);
    		if(!blocks[-(playersleft+1)].isAlive())
    			graphics.drawString("- oops, you're dead",WIDTH/2-55,HEIGHT/2+40);
    		graphics.drawString("(Control-R to play again)", WIDTH/2-75,HEIGHT/2+80);
    			}
    	if(playersleft!=0 && playersleft<=players)
    		for(int i=0;i<players;i++){
    			graphics.setColor(colors[i]);
    			if(blocks[i].isAlive()){
					if(playersleft==1)
						playersleft=-i-1;
    				graphics.clearRect(blocks[i].getX(),HEIGHT-blocks[i].getY(),BLOCKLENGTH,BLOCKLENGTH);
    				int xPos = blocks[i].getX(), yPos = blocks[i].getY();
    				if(xPos>WIDTH-25 || xPos+BLOCKLENGTH<25 || yPos-BLOCKLENGTH>HEIGHT-25 || yPos<25){
    					blocks[i].kill();
    					if(playersleft>0)
    						playersleft-=1;
    					break;}
    				blocks[i].act();
    				for(int j=i;j<players-1;j++)
    					handleCollisions(blocks[i],blocks[j+1]);
    				graphics.fillRect(blocks[i].getX(),HEIGHT-blocks[i].getY(),BLOCKLENGTH,BLOCKLENGTH);
    			}
    			else
    				graphics.fillRect(100+(60*i), HEIGHT-25, BLOCKLENGTH, 25);
    		}
    	else if(playersleft==0)
    		graphics.drawString("How many players? (Type 1, 2, 3, or 4)", WIDTH/2-110, HEIGHT/2);
    		
    	g.drawImage(image, 0, 0, this);
    }
    private void handleCollisions(Block a, Block b){
    	int xi=Math.abs(a.getX()-b.getX());
    	int yi=Math.abs(a.getY()-b.getY());
    	if(xi>=BLOCKLENGTH/2 && xi<=BLOCKLENGTH)
    		if(yi<=BLOCKLENGTH){
    		double temp = a.getHSpeed();
    		a.setHSpeed(b.getHSpeed());
    		b.setHSpeed(temp);
    		}
    	if(yi>=BLOCKLENGTH/2 && yi<=BLOCKLENGTH)
    		if(xi<=BLOCKLENGTH){
        		double temp = a.getVSpeed();
        		a.setVSpeed(b.getVSpeed());
        		b.setVSpeed(temp);
    		}
    }
    private void initBlocks(int a){
		    playersleft=a;
		    players=playersleft;
            blocks =new Block[players];
            int halfwayV = (HEIGHT/2)+(BLOCKLENGTH/2);
            int halfwayH = (WIDTH/2)-(BLOCKLENGTH/2);
            for(int i=0;i<players;i++)
            	switch(i){
            		case 0:blocks[i] = new Block(35,halfwayV);
            			break;
            		case 1:blocks[i] = new Block(WIDTH-85,halfwayV);
            			break;
            		case 2:blocks[i] = new Block(halfwayH,HEIGHT-35);
            			break;
            		case 3:blocks[i] = new Block(halfwayH,85);
            			break;}

            graphics.clearRect(0,0,WIDTH,HEIGHT);
    }
    private class ShiftListener implements KeyListener {
    	boolean control=false;
		 public void keyPressed(KeyEvent e)             
        {
		   int code = e.getKeyCode();
		   if(code==KeyEvent.VK_CONTROL)
			   control=true;
		   else if(code==KeyEvent.VK_R && control){
			   stop();
			   start();
		   }
		   else if(playersleft==0)
			  if(code==KeyEvent.VK_1)
			 	 initBlocks(1);
			  else if(code==KeyEvent.VK_2)
				 initBlocks(2);
			  else if(code==KeyEvent.VK_3)
				 initBlocks(3);
			  else if(code==KeyEvent.VK_4)
				 initBlocks(4);
		   for(int i=0;i<players;i++)
			  if(code == keys[i][0])
				 blocks[i].setVDirection(1);
			  else if(code == keys[i][1])
			     blocks[i].setHDirection(1);
			  else if(code == keys[i][2])
			     blocks[i].setVDirection(-1);
			  else if(code == keys[i][3])
			     blocks[i].setHDirection(-1);
        }
        public void keyReleased(KeyEvent e)
        {
        	int code = e.getKeyCode();
        	if(code==KeyEvent.VK_CONTROL)
        		control=false;
        	else for(int i=0;i<players;i++)
  			   if(code == keys[i][0])
  				  blocks[i].setVDirection(0);
  			   else if(code == keys[i][1])
  				   blocks[i].setHDirection(0);
  			   else if(code == keys[i][2])
  				   blocks[i].setVDirection(0);
  			   else if(code == keys[i][3])
  				   blocks[i].setHDirection(0);
        }
     
        public void keyTyped(KeyEvent e)     
        {}
	}
}