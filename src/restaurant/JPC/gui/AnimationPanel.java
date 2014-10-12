package restaurant.JPC.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {
    private final int WINDOW_XSIZE = 800;
    private final int WINDOW_YSIZE = 800;
    static final int WINDOW_XPOS = 0;
    static final int WINDOW_YPOS = 0;
    
    // Tables
    static final int START_TABLE_XPOS = 200;
    static final int START_TABLE_YPOS = 150;
    static final int TABLE_LENGTH = 50;
    static final int TABLE_WIDTH = 50;
    
    // Customer Waiting Area
    static final int WAITING_AREA_XPOS = 10;
    static final int WAITING_AREA_YPOS = 15;
    static final int WAITING_AREA_LENGTH = 100;
    static final int WAITING_AREA_WIDTH = 100;

    // Kitchen
    static final int PLATING_AREA_XPOS = 450;
    static final int PLATING_AREA_YPOS = 150;
    static final int PLATING_AREA_LENGTH = 50;
    static final int PLATING_AREA_WIDTH = 150;
    
    static final int COOKING_AREA_XPOS = 500;
    static final int COOKING_AREA_YPOS = 150;
    static final int COOKING_AREA_LENGTH = 215;
    static final int COOKING_AREA_WIDTH = 50;
    
    // Grills
    static final int START_GRILL_XPOS = 515;
    static final int START_GRILL_YPOS = 165;
    static final int GRILL_LENGTH = 30;
    static final int GRILL_WIDTH = 30;
    
    // Fridge
    static final int START_FRIDGE_XPOS = 685;
    static final int START_FRIDGE_YPOS = 265;
    static final int FRIDGE_LENGTH = 30;
    static final int FRIDGE_WIDTH = 30;

    
    static final int TIMER_MILLISECONDS = 20;
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOW_XSIZE, WINDOW_YSIZE);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(TIMER_MILLISECONDS, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(WINDOW_XPOS, WINDOW_YPOS, WINDOW_XSIZE, WINDOW_YSIZE);

        // Tables
        g2.setColor(Color.BLUE);
        g2.fillRect(START_TABLE_XPOS, START_TABLE_YPOS, TABLE_LENGTH, TABLE_WIDTH);
        g2.fillRect(START_TABLE_XPOS, START_TABLE_YPOS + 100, TABLE_LENGTH, TABLE_WIDTH);
        g2.fillRect(START_TABLE_XPOS + 100, START_TABLE_YPOS, TABLE_LENGTH, TABLE_WIDTH);
        g2.fillRect(START_TABLE_XPOS + 100, START_TABLE_YPOS + 100, TABLE_LENGTH, TABLE_WIDTH);
        
        // Waiting Area
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(WAITING_AREA_XPOS, WAITING_AREA_YPOS, WAITING_AREA_LENGTH, WAITING_AREA_WIDTH);
        
        // Plating Area
        g2.setColor(Color.GRAY);
        g2.fillRect(PLATING_AREA_XPOS, PLATING_AREA_YPOS, PLATING_AREA_LENGTH, PLATING_AREA_WIDTH);
        
        // Cooking Area
        g2.setColor(Color.GRAY);
        g2.fillRect(COOKING_AREA_XPOS, COOKING_AREA_YPOS, COOKING_AREA_LENGTH, COOKING_AREA_WIDTH);
        
        // Grills
        g2.setColor(Color.BLACK);
        g2.fillRect(START_GRILL_XPOS, START_GRILL_YPOS, GRILL_LENGTH, GRILL_WIDTH);
        g2.fillRect(START_GRILL_XPOS + 50, START_GRILL_YPOS, GRILL_LENGTH, GRILL_WIDTH);
        g2.fillRect(START_GRILL_XPOS + 100, START_GRILL_YPOS, GRILL_LENGTH, GRILL_WIDTH);
        g2.fillRect(START_GRILL_XPOS + 150, START_GRILL_YPOS, GRILL_LENGTH, GRILL_WIDTH);

        // Fridge
        g2.setColor(Color.GRAY);
        g2.fillRect(START_FRIDGE_XPOS, START_FRIDGE_YPOS, FRIDGE_LENGTH, FRIDGE_WIDTH);
        
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }
    
    public void addGui(CookGui gui) {
        guis.add(gui);
    }
    
    public void addGui(FoodGui gui){
    	guis.add(gui);
    }
}
