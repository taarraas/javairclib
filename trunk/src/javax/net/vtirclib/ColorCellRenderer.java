/*
 * ColorListRenderer.java
 *
 * Created on 21 липня 2008, 19:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package javax.net.vtirclib;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;
/**
 *
 * @author taras
 */
public class ColorCellRenderer extends JLabel implements ListCellRenderer {
    
    /** Creates a new instance of ColorListRenderer */
 public Component getListCellRendererComponent(
    JList list,
    Object value,            // value to display
    int index,               // cell index
    boolean isSelected,      // is the cell selected
    boolean cellHasFocus)    // the list and the cell have the focus
 {
     String s = value.toString();
     setText(s);
     if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
     }
     else {
        if (value instanceof ColorString) setBackground(((ColorString)value).getBackground());
        else setBackground(list.getBackground());
        setForeground(list.getForeground());
     }
     setEnabled(list.isEnabled());
     setFont(list.getFont());
     setOpaque(true);
     return this;
 }

}
