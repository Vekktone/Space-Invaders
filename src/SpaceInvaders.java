import java.awt.*;
import java.util.Random;
import java.applet.Applet;
/* This is part 1 of project 3 of CS 1063 for Spring 2015
 */

public class Project3Part7 extends Applet {
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public static final int SLEEP_TIME = 50;
  
  // keys
  public static final int RIGHT_ARROW = 39;
  public static final int LEFT_ARROW = 37;
  public static final int UP_ARROW = 38;
  
  // panel size
  public static final int PANEL_WIDTH = 600;
  public static final int PANEL_HEIGHT = 400;
  
  // patrol ship
  public static final int PATROL_Y = PANEL_HEIGHT-50;
  public static final int PATROL_WIDTH = 31;
  public static final int PATROL_HEIGHT = 17;
  public static final int PATROL_MOVE_AMOUNT = 3;
  public static final int PATROL_INITIAL_X = PANEL_WIDTH - PATROL_WIDTH - 5;
  public static final int ENEMY_COUNT = 5;
  public static int patrolX;
  
  // enemy ship
  public static final int ENEMY_Y = 20;
  public static final int ENEMY_WIDTH = 36;
  public static final int ENEMY_HEIGHT = 21;
  public static final int ENEMY_INITIAL_X = 5;
  public static final int ENEMY_DEAD_X = -1;
  public static final double ENEMY_POSITION_CHANGE_PROBABILITY = 0.1;
  public static int[] enemyX;
  public static int enemyMoveAmount;
  
  // patrol missile
  public static final int PATROL_MISSILE_LENGTH = 10;
  public static final int PATROL_MISSILE_MOVE_AMOUNT = 5;
  public static final int PATROL_MISSILE_AVAILABLE_Y = 0;
  public static final int PATROL_MISSILE_MAX = 5;
  public static int[] patrolMissileX;
  public static int[] patrolMissileY;
  
   // enemy missile
  public static final int ENEMY_MISSILE_LENGTH = 5;
  public static final int ENEMY_MISSILE_MOVE_AMOUNT = 5;
  public static final int ENEMY_MISSILE_AVAILABLE_Y = 0;
  public static final int ENEMY_MISSILE_MAX = 20;
  public static final double ENEMY_MISSILE_FIRE_PROBABILITY = 0.02;
  public static int[] enemyMissileX;
  public static int[] enemyMissileY;
  public static int[] enemyMissileDx;
  
  // colors
  public static final Color BACKGROUND_COLOR = Color.WHITE;
  public static final Color ENEMY_COLOR = Color.RED;
  public static final Color ENEMY_DEAD_COLOR = Color.BLACK;
  public static final Color ENEMY_MISSILE_COLOR = Color.RED;
  public static final Color PATROL_COLOR = Color.GREEN;
  public static final Color PATROL_DEAD_COLOR = Color.BLACK;
  public static final Color PATROL_MISSILE_COLOR = Color.GREEN;
  public static final Color HEADING_COLOR = Color.BLACK;
  public static final Color MESSAGE_WIN_COLOR = Color.GREEN;
  public static final Color MESSAGE_LOSE_COLOR = Color.RED;
  public static final Color MESSAGE_DRAW_COLOR = Color.BLUE;
  public static final Color MESSAGE_SPACE_BAR_COLOR = Color.BLACK;
  
  // messages
  public static final String HEADING_MESSAGE = "Space Invaders- A Game";
  public static final String START_MESSAGE = "Push Space Bar to Start";
  public static final String ENEMY_HIT_MESSAGE = "All Enemy Ships Destroyed!";
  public static final String PATROL_HIT_MESSAGE = "Patrol ship destroyed!";
  public static final String MUTUAL_DESTRUCTION_MESSAGE = "                                                          "+
    "Mutual Destruction!";
  
  // message positions
  public static final int HEADING_X = 10;
  public static final int HEADING_Y = 15;
  public static final int MESSAGE_X = 10;
  public static final int MESSAGE_Y = PANEL_HEIGHT - 10;
  
  public static Random random = new Random();
  
  public static boolean running;
  public static boolean hit;
  public static boolean patrolHit;
  public static boolean enemyHit;
  
  // Main method for Project 3
  public static void main(String[] args) {
    DrawingPanel panel = new DrawingPanel(PANEL_WIDTH, PANEL_HEIGHT);
    Graphics g = panel.getGraphics( );
    patrolMissileX = new int[PATROL_MISSILE_MAX];
    patrolMissileY = new int[PATROL_MISSILE_MAX];
    enemyMissileX = new int[ENEMY_MISSILE_MAX];
    enemyMissileY = new int[ENEMY_MISSILE_MAX];
    enemyMissileDx = new int[ENEMY_MISSILE_MAX];
    enemyX = new int[ENEMY_COUNT];
    restart(g);
    running = false;
    showMessage(g,START_MESSAGE,MESSAGE_SPACE_BAR_COLOR);
    startGame(panel, g);
  }
  
  // show a message at the bottom of the screen
  public static void showMessage(Graphics g, String message, Color c) {
    g.setColor(c);
    g.drawString(message,MESSAGE_X,MESSAGE_Y);
  }
  
  // show the heading message at the top of the screen
  public static void showHeading(Graphics g, String message, Color c) {
    g.setColor(c);
    g.drawString(message,HEADING_X,HEADING_Y);
  }
  
  // This method contains the main loop for the program
  // The loop runs forever, sleeping after each iteration
  public static void startGame(DrawingPanel panel, Graphics g) {
    drawPatrol(g, Color.green);
    while(true) {
      moveEnemyShipsAndDraw(g);
      handleKeys(panel, g);
      movePatrolMissilesAndDraw(g);
      fireEnemyMissileIfAvailable();
      moveEnemyMissilesAndDraw(g);
      if (detectHit(g))
        hit = true;
      if (hit) {
        if (patrolHit && enemyHit) {
          showMessage(g,MUTUAL_DESTRUCTION_MESSAGE,MESSAGE_DRAW_COLOR);
          drawPatrol(g,PATROL_DEAD_COLOR);
        }
        else if (patrolHit) {
          showMessage(g,PATROL_HIT_MESSAGE,MESSAGE_LOSE_COLOR);
          drawPatrol(g,PATROL_DEAD_COLOR);
        }
        else
          showMessage(g,ENEMY_HIT_MESSAGE,MESSAGE_WIN_COLOR);
      }
      panel.sleep(SLEEP_TIME);
    }
  }
  
  // redraw the screen and reset the game to the beginning
  public static void restart(Graphics g) {
    running = true;
    hit = false;
    patrolHit = false;
    enemyHit = false;
    enemyMoveAmount = 0;
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0,0,PANEL_WIDTH,PANEL_HEIGHT);
    showHeading(g,HEADING_MESSAGE,HEADING_COLOR);
    for (int i=0;i<patrolMissileY.length;i++)
       patrolMissileY[i] = PATROL_MISSILE_AVAILABLE_Y;
    for (int i=0;i<enemyMissileY.length;i++)
       enemyMissileY[i] = ENEMY_MISSILE_AVAILABLE_Y;
    for (int i=0;i<enemyX.length;i++)
      enemyX[i] = ENEMY_INITIAL_X + 2*i*ENEMY_WIDTH;
    patrolX = PATROL_INITIAL_X;
    drawPatrol(g,PATROL_COLOR);
  }
  
  // draw the patrol ship in a given color
  public static void drawPatrol(Graphics g, Color c) {
    g.setColor(c);
    g.fillRect(patrolX, PATROL_Y, PATROL_WIDTH, PATROL_HEIGHT);
//    drawPatrolFancy(g,c);
  }
  
    public static void drawPatrolFancy(Graphics g, Color c) {
    int[] x = {7,6,1,0};
    int[] y = {0,1,2,3};
    int[] dx = {1,3,13,15};
    int[] dy = {1,2,1,5};
    int miniSize = 2;
//    Graphics2D gg = (Graphics2D)g.create();
//    gg.translate(-PATROL_WIDTH/2,-PATROL_HEIGHT/2);
    
//    gg.rotate(-Math.PI/4,patrolX+PATROL_WIDTH/2,PATROL_Y+PATROL_HEIGHT/2);
//    gg.translate(PATROL_WIDTH/2,PATROL_HEIGHT/2);
    
//    gg.setColor(Color.BLACK);
//    gg.drawRect(patrolX,PATROL_Y,PATROL_WIDTH,PATROL_HEIGHT);
    g.setColor(c);
    for (int i=0; i<x.length; i++)
      g.fillRect(patrolX + miniSize*x[i],PATROL_Y + miniSize*y[i],miniSize*dx[i],miniSize*dy[i]);
//    gg.dispose();
  }
    
      public static void drawEnemyFancy(Graphics g, Color c, int xPos) {
    int[] x = {4,1,0,0,5,9,0,3,5,8,2,5,8};
    int[] y = {0,1,2,3,3,3,4,5,5,5,6,6,6};
    int[] dx = {4,10,12,3,2,3,12,1,2,1,2,2,2};
    int[] dy = {1,1,1,1,1,1,1,1,1,1,1,1,1};
    int miniSize = 3;
    g.setColor(c);
    for (int i=0; i<x.length; i++)
      g.fillRect(xPos + miniSize*x[i],ENEMY_Y + miniSize*y[i],miniSize*dx[i],miniSize*dy[i]);
  }
  
  // draw the enemy ship in a given color
  public static void drawEnemy(Graphics g, Color c, int which) {
    g.setColor(c);
    g.fillRect(enemyX[which], ENEMY_Y, ENEMY_WIDTH, ENEMY_HEIGHT);
//    drawEnemyFancy(g,c,enemyX[which]);
  }
  
  public static void moveEnemyShipAndDraw(Graphics g, int which) {
    if (enemyX[which] == ENEMY_DEAD_X)
      return;
    if (running) {
       drawEnemy(g,BACKGROUND_COLOR,which);
       enemyX[which] += enemyMoveAmount;
    }
    drawEnemy(g,ENEMY_COLOR,which);
  }
  
  // move the enemy ship and redraw it, erasing the old position
  public static void moveEnemyShipsAndDraw(Graphics g) {
    int delta;
    if (running) {
      if (random.nextDouble() < ENEMY_POSITION_CHANGE_PROBABILITY) {
        delta = 2*random.nextInt(2) - 1;  // 0 -> -1, 1-> +1
        enemyMoveAmount += delta;
        if (enemyMoveAmount < -1)
          enemyMoveAmount = +1;
        if (enemyMoveAmount > 1)
          enemyMoveAmount = -1;
      }
      fixEnemyMove();
    }
    for (int i=0;i<enemyX.length;i++)
      moveEnemyShipAndDraw(g,i);
  }
  
  // If the next move will cause any part of the ship to move off the screen,
  // change the direction of the move
    public static void fixEnemyMove() {
    if (enemyMoveAmount < 0)
      for (int i=0;i<enemyX.length;i++) {
        if ((enemyX[i] != ENEMY_DEAD_X)&&(enemyX[i] + enemyMoveAmount < 0)) {
           enemyMoveAmount = -enemyMoveAmount;
           return;
        }
      }
    else if (enemyMoveAmount > 0)
      for (int i=0;i<enemyX.length;i++) {
         if ((enemyX[i] != ENEMY_DEAD_X) && (enemyX[i] + enemyMoveAmount > PANEL_WIDTH - ENEMY_WIDTH)) {
           enemyMoveAmount = -enemyMoveAmount;
           return;
       }
    }
  }
  
  // handle the space bar and arrow keys
  public static void handleKeys(DrawingPanel panel, Graphics g) {
    int missilePosition;
    int keyCode = panel.getKeyCode();
    if (keyCode == 0)
      return; 
    if (keyCode == ' ')
      restart(g);
    if (keyCode == LEFT_ARROW)
      movePatrol(g,-PATROL_MOVE_AMOUNT);
    if (keyCode == RIGHT_ARROW)
      movePatrol(g,PATROL_MOVE_AMOUNT);
    if (!patrolHit && (keyCode == UP_ARROW) && ((missilePosition = getEmptyPatrolMissilePosition()) != -1)) {
      patrolMissileX[missilePosition] = patrolX + PATROL_WIDTH/2;
      patrolMissileY[missilePosition] = PATROL_Y - PATROL_MISSILE_LENGTH - 1;
    }
  }
  
  // move the patrol ship by a given amount, erasing the ship at the old position
  public static void movePatrol(Graphics g, int delta) {
    if (patrolHit)
      return;
    drawPatrol(g, Color.white);
    patrolX += delta;
    if (patrolX < -PATROL_WIDTH/2)
      patrolX = -PATROL_WIDTH/2;
    if (patrolX >= PANEL_WIDTH - PATROL_WIDTH/2)
      patrolX = PANEL_WIDTH - PATROL_WIDTH/2 - 1;
    drawPatrol(g, PATROL_COLOR);
  }
  
  // draw the missile with (x,y) at the top of the missile and given length
  public static void drawMissile(Graphics g, int x, int y, int length, Color c) {
    g.setColor(c);
    g.drawLine(x,y,x,y+length);
  }
  
  // move all patrol missiles
  public static void movePatrolMissilesAndDraw(Graphics g) {
    for (int i=0;i<patrolMissileY.length;i++)
      movePatrolMissileAndDraw(g,i);
  }
  
  // move a patrol missile, erasing the old one
  public static void movePatrolMissileAndDraw(Graphics g, int which) {
    if (patrolMissileY[which] == PATROL_MISSILE_AVAILABLE_Y)
      return;
    drawMissile(g,patrolMissileX[which],patrolMissileY[which],PATROL_MISSILE_LENGTH,BACKGROUND_COLOR);
    patrolMissileY[which] -= PATROL_MISSILE_MOVE_AMOUNT;
    g.setColor(PATROL_MISSILE_COLOR);
    if (patrolMissileY[which] <= 0)
      patrolMissileY[which] = PATROL_MISSILE_AVAILABLE_Y;
    else
      drawMissile(g,patrolMissileX[which],patrolMissileY[which],PATROL_MISSILE_LENGTH,PATROL_MISSILE_COLOR);
  }
  
  // fire the enemy missile if it is available
  public static void fireEnemyMissileIfAvailable() {
    if (hit || !running)
      return;
    for (int i=0;i<enemyX.length;i++)
      fireOneEnemyMissileIfAvailable(i);
  }
  
  public static void fireOneEnemyMissileIfAvailable(int which) {
    if (enemyX[which] == ENEMY_DEAD_X)
      return;
    if (random.nextDouble() > ENEMY_MISSILE_FIRE_PROBABILITY)
      return;
    int pos = getEmptyEnemyMissilePosition();
    if (pos == -1)
      return;
    enemyMissileX[pos] = enemyX[which] + ENEMY_WIDTH/2;
    enemyMissileY[pos] = ENEMY_Y + ENEMY_HEIGHT;
    enemyMissileDx[pos] = enemyMoveAmount;
  }
  
  // move all enemy missiles, erasing the old ones
  public static void moveEnemyMissilesAndDraw(Graphics g) {
    for (int i=0;i<enemyMissileY.length;i++)
      moveEnemyMissileAndDraw(g,i);
  }
  
  // move the enemy missile, erasing the old one
  public static void moveEnemyMissileAndDraw(Graphics g, int which) {
    if (enemyMissileY[which] == ENEMY_MISSILE_AVAILABLE_Y)
      return;
    drawMissile(g,enemyMissileX[which],enemyMissileY[which],ENEMY_MISSILE_LENGTH,
                BACKGROUND_COLOR);
    enemyMissileY[which] += ENEMY_MISSILE_MOVE_AMOUNT;
    enemyMissileX[which] += enemyMissileDx[which];
    g.setColor(ENEMY_MISSILE_COLOR);
    if (enemyMissileY[which] > PANEL_HEIGHT)
      enemyMissileY[which] = ENEMY_MISSILE_AVAILABLE_Y;
    else
      drawMissile(g,enemyMissileX[which],enemyMissileY[which],ENEMY_MISSILE_LENGTH,ENEMY_MISSILE_COLOR);
  }
  
  // return true if the missile intersects the enemy ship
  public static boolean detectHit(Graphics g) {
    for (int i=0;i<patrolMissileY.length;i++)
      for (int j=0;j<enemyX.length;j++)
         if (enemyX[j] != ENEMY_DEAD_X && patrolMissileX[i] >= enemyX[j] && patrolMissileX[i] <= enemyX[j] + ENEMY_WIDTH &&
             patrolMissileY[i] >= ENEMY_Y && patrolMissileY[i] <= ENEMY_Y + ENEMY_HEIGHT) {
               drawEnemy(g,BACKGROUND_COLOR,j);
               enemyX[j] = ENEMY_DEAD_X;
         }
    int enemyCount = 0;
    for (int i=0;i<enemyX.length;i++)
      if (enemyX[i] != ENEMY_DEAD_X)
         enemyCount++;
    enemyHit = (enemyCount == 0);
    boolean pHit = false;
    for (int i=0;i<enemyMissileY.length;i++)
       if (enemyMissileX[i] >= patrolX && enemyMissileX[i] <= patrolX + PATROL_WIDTH &&
          enemyMissileY[i] >= PATROL_Y && enemyMissileY[i] <= PATROL_Y + PATROL_HEIGHT)
             pHit = true;
    if (pHit)
       patrolHit = true;
    return enemyHit || patrolHit;
  } 
  
  public static int getEmptyPatrolMissilePosition() {
    for (int i=0;i<patrolMissileY.length;i++)
      if (patrolMissileY[i] == 0)
        return i;
    return -1;
  }
  
  public static int getEmptyEnemyMissilePosition() {
    for (int i=0;i<enemyMissileY.length;i++)
      if (enemyMissileY[i] == 0)
        return i;
    return -1;
  }
  
  public void init() {
       main(null);
  }
  
}
