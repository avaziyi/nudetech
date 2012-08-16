/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.amaes.nudetech.ui.utililities;

import java.util.ResourceBundle;

/**
 *
 * @author Angelo Balaguer
 */
public class MessagesUtil {
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");
    
    public static String getMessage(String key) {
        return MESSAGES.getString(key);
    }
}
