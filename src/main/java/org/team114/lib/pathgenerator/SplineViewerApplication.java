package org.team114.lib.pathgenerator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.team114.lib.util.Geometry;
import org.team114.lib.util.Point;


/**
 * 
 * This is for the purpose of drawing the spline paths for viewing, it does not have
 * javadocs because it a tool not a library. Also this is my excuse for messy code and
 * jamming everything into one class.
 * 
 * Aris please do not read this code.
 *
 */
@SuppressWarnings("serial")
public class SplineViewerApplication extends JFrame implements MouseMotionListener, MouseListener, ActionListener, FocusListener {


    private HermiteWaypointSpline spline;

    private Waypoint n = new Waypoint(0, 0);

    private BufferedImage field = null, base = null;
    private static final String fieldImagePath = "/org/team114/lib/util/field.jpg";

    private static boolean vectors = false, addingPoint = true;

    private static JTextArea editor = new JTextArea("");

    private final splineView splines = new splineView();

    private double grayout = 0.5;

    public static void main(String[] args) { 
        try {
            new SplineViewerApplication(); 
        }catch(Exception e) { }
    }


    public SplineViewerApplication() throws InterruptedException {
        super("Spline Viewer");

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;

        content.add(editor, c);

        editor.setPreferredSize(new Dimension(100,1));
        editor.setMaximumSize(new Dimension(100,1));
        editor.setLineWrap(true);
        editor.setWrapStyleWord(true);
        editor.addFocusListener(this);


        editor.setFont(new Font("monospace", Font.PLAIN, 12));

        c.gridwidth = 5;
        c.gridx = 1;
        c.weightx = 5;
        content.add(splines, c);

        setContentPane(content);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(getJMenu("File", this, "Set Image",null, "Save","Open","Export"));
        menuBar.add(getJMenu("Edit", this, "Add Point", "Toggle Vectors","Update From Editor"));
        menuBar.add(getJMenu("View", this, "Cleanup Editor", "Auto Scale Image"));
        setJMenuBar(menuBar);

        setSize(1500, 800);
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        spline  = new HermiteWaypointSpline(new Waypoint[] { new Waypoint(100,100), new Waypoint(300, 300) });
        splines.addMouseMotionListener(this);
        splines.addMouseListener(this);

        setVisible(true);

        //  updateEditor();

        try {
            base = ImageIO.read(getClass().getResource(fieldImagePath));
            field = resize(base, splines.getWidth(), (int)((double)splines.getWidth() / (double)base.getWidth() * base.getHeight()));
            super.setSize(base.getWidth()+150, base.getHeight()+30);
        }catch(Exception e) {
            error("Could not find default image.");
        }

        while(true) {
            Thread.sleep(5);
            repaint();
        }
    }

    private void updateEditor() {
        String s = "";
        if(field != null)
            s += "Image Pixel Width: "+field.getWidth()+"\nImage Pixel Height: "+field.getHeight()+"\nImage Gray Out: "+grayout+"\n\n";
        for(Waypoint p : spline.pointList) {
            s += "(" + p.x + ", " + p.y + ")\n";
            s += "    Assign Derivative: " + p.autoAssignDerivative + "\n";
            s += "    Derivative X: " + p.derivativeX + "\n";
            s += "    Derivative Y:  " + p.derivativeY + "\n\n";
        }
        editor.setText(s);
    }

    private void updateFromEditor() {
        String s = editor.getText().replaceAll(" ", "");
        ArrayList<Waypoint> tmp = new ArrayList<Waypoint>();
        Waypoint current = null;
        try {
            while(s.length() > 0) {
                String b = s.substring(0, s.indexOf("\n"));

                if(s.charAt(0) == '(') {
                    current = new Waypoint(Double.parseDouble(b.substring(b.indexOf("(")+1,b.indexOf(","))),Double.parseDouble(b.substring(b.indexOf(",")+1,b.indexOf(")"))));
                    tmp.add(current);
                }else if(b.indexOf("AssignDerivative") != -1) {
                    if(current != null) 
                        current.autoAssignDerivative = b.charAt(b.indexOf(":")+1) == 't';
                }else if(b.indexOf("DerivativeX") != -1) {
                    if(current != null && !current.autoAssignDerivative) 
                        current.derivativeX = Double.parseDouble(b.substring(b.indexOf(":")+1, b.length()));
                }else if(b.indexOf("DerivativeY") != -1) {
                    if(current != null && !current.autoAssignDerivative) 
                        current.derivativeY = Double.parseDouble(b.substring(b.indexOf(":")+1, b.length()));
                }else if(b.indexOf("ImagePixelWidth") != -1) {
                    if(base != null && field != null) 
                        field = resize(base, Integer.parseInt(b.substring(b.indexOf(":")+1, b.length())), field.getHeight());
                }else if(b.indexOf("ImagePixelHeight") != -1) {
                    if(base != null && field != null) 
                        field = resize(base, field.getWidth(),Integer.parseInt(b.substring(b.indexOf(":")+1, b.length())));
                }else if(b.indexOf("ImageGrayOut") != -1) {
                    grayout = Double.parseDouble(b.substring(b.indexOf(":")+1, b.length()));
                }
                if(s.indexOf("\n") == -1)
                    break;
                s = s.substring(s.indexOf("\n")+1,s.length());

            }
        }catch(Exception e) {
            error("Was unable to read from point editor");
        }
        spline.pointList = tmp;
        spline.reloadSpline();
        for(Waypoint p : spline.pointList)
            System.out.println(p);
    }


    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        n.x = e.getX();
        n.y = e.getY();
        if(addingPoint)
            updateEditor();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(addingPoint)
            updateEditor();
        addingPoint = false;
        n = new Waypoint(e.getX(), e.getY());
        spline.reloadSpline();
        splines.requestFocus();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void focusGained(FocusEvent e) {}


    @Override
    public void focusLost(FocusEvent e) {
        updateFromEditor();
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    } 

    public JMenu getJMenu(String menuName, ActionListener listener, String... items){
        JMenu menu = new JMenu(menuName);
        for(String s: items){
            if(s==null){
                menu.addSeparator();
                continue;
            }
            JMenuItem item = new JMenuItem(s);  
            menu.add(item);
            item.addActionListener(listener);
        }
        return menu;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Open")){
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Spline path files", "path");
            chooser.setFileFilter(filter);
            if(chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
                ObjectInputStream i;
                try {
                    i = new ObjectInputStream(new FileInputStream(chooser.getSelectedFile().getAbsolutePath()));
                } catch (IOException e2) {
                    error("Could not get file.");
                    return;
                }
                try {
                    Object o = i.readObject();
                    i.close();
                    if(o instanceof List<?>)
                        spline.pointList = (List<Waypoint>) o;
                    else
                        throw new IOException();
                    spline.reloadSpline();

                } catch (ClassNotFoundException | IOException e1) {
                    error("Inncorrect type or corrupt file");
                }
            }

        }else if(e.getActionCommand().equals("Save")){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("/Documents"));
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                try(ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(chooser.getSelectedFile()+".path"))) {
                    o.writeObject(spline.pointList);
                } catch (IOException e1) {
                    error("Error writing file!\n"+exceptionText(e1));
                }
            }
        }else if(e.getActionCommand().equals("Export")){
            //TODO: Export!
            error("This feature is not yet supported.");


            //            
            //            JFileChooser chooser = new JFileChooser();
            //            chooser.setCurrentDirectory(new File("/Documents"));
            //            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            //                try(FileOutputStream out = new FileOutputStream(chooser.getSelectedFile()+".path")) {
            //                   
            //                    out
            //                    
            //                } catch (IOException e1) {
            //                    error("Error writing file!\n"+exceptionText(e1));
            //                }
            //            }





        }else if(e.getActionCommand().equals("Add Point")){
            addingPoint = true;
            spline.appendPoint(n);
        }else if(e.getActionCommand().equals("Toggle Vectors")){
            vectors =! vectors;
        }else if(e.getActionCommand().equals("Set Image")){
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png","jpg");
            chooser.setFileFilter(filter);
            if(chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    System.out.println(path);
                    URL tmp = getClass().getResource(path);
                    System.out.println(tmp);
                    base = ImageIO.read(new File(path));

                    field = resize(base, splines.getWidth(), (int)((double)splines.getWidth() / (double)base.getWidth() * base.getHeight()));
                    super.setSize(base.getWidth()+150, base.getHeight()+30);
                } catch (IOException e2) {
                    error("Could not get file.");
                    return;
                }
            }
        }else if(e.getActionCommand().equals("Update From Editor")){
            updateFromEditor();
        }else if(e.getActionCommand().equals("Cleanup Editor")){
            updateEditor();
        }else if(e.getActionCommand().equals("Auto Scale Image")){
            field = resize(base, splines.getWidth(), (int)((double)splines.getWidth() / (double)base.getWidth() * base.getHeight()));
        }




    }

    private static void error(String message) {
        JOptionPane.showMessageDialog(new JPanel(), message, "Error", JOptionPane.WARNING_MESSAGE);
    }

    private static String exceptionText(Exception e) {
        StringWriter error = new StringWriter();
        e.printStackTrace(new PrintWriter(error));
        return error.toString();
    }

    private class splineView extends JPanel {

        @Override
        public void paint(Graphics b) {
            BufferedImage b2 = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g =(Graphics2D) b2.getGraphics();
            spline.reloadSpline();

            if(field != null)
                g.drawImage(field, 0, 0, null);

            g.setColor(new Color(0,0,0,(int)(Math.min(Math.max(grayout * 255, 0), 255))));
            g.fillRect(0, 0, getWidth(), getHeight());


            g.setStroke(new BasicStroke(3));

            if(vectors) {
                g.setColor(Color.BLUE);
                for(int i = 0; i < spline.getWaypointList().size()-1; i++) {
                    Waypoint a = spline.getWaypointList().get(i);
                    Waypoint h = spline.getWaypointList().get(i+1);
                    g.drawLine((int)a.x, (int)a.y, (int)(a.x + a.derivativeX * Geometry.dist(a.x, a.y, h.x, h.y)), (int)(a.y + a.derivativeY * Geometry.dist(a.x, a.y, h.x, h.y)));
                }
            }


            g.setColor(Color.WHITE);
            double[] last = spline.getPointAtT(0.00001);
            for(double t = 0; t < spline.getSplineDomain(); t += 0.001) {
                double[] next = spline.getPointAtT(t);
                if((int)next[0] == 0 || (int)next[1] == 0)
                    continue;
                g.drawLine((int)last[0], (int)last[1], (int)next[0], (int)next[1]);
                last = next;
            }
            g.setColor(Color.GREEN);
            for(Waypoint w :spline.getWaypointList())
                g.fillOval((int)w.x-5, (int)w.y-5, 10, 10);
            Point p = null;
            try {
                p = spline.getClosestPointOnSpline(new Point(n.x,n.y));
            }catch(Exception e) {
                e.printStackTrace();
            }
            g.setColor(Color.RED);
            if(p != null) {
                g.fillOval((int)p.x-5, (int)p.y-5, 10, 10);
                double dist = Geometry.dist(p.x, p.y, n.x, n.y);
                g.drawOval((int)(n.x-dist), (int)(n.y-dist), (int)(dist*2), (int)(dist*2));
            } else if(spline.getSplineDomain() > 1)
                System.err.println("Point is null");

            b.drawImage(b2, 0, 0, null);
        }

    }

}
